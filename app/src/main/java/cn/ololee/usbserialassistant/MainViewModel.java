package cn.ololee.usbserialassistant;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import cn.ololee.usbserialassistant.constants.bean.ConnectBean;
import cn.ololee.usbserialassistant.constants.bean.CustomProber;
import cn.ololee.usbserialassistant.constants.bean.DataModel;
import cn.ololee.usbserialassistant.constants.Constants;
import cn.ololee.usbserialassistant.fragment.operation.UsbPermission;
import cn.ololee.usbserialassistant.util.DataDealUtils;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.tencent.mmkv.MMKV;
import java.io.IOException;

import static cn.ololee.usbserialassistant.MainActivity.INTENT_ACTION_GRANT_USB;

public class MainViewModel extends AndroidViewModel implements SerialInputOutputManager.Listener {
  private MutableLiveData<Integer> connectedStatus = new MutableLiveData<>();
  private MutableLiveData<String> failedReason = new MutableLiveData<>();
  private ConnectBean connectBean;
  private UsbPermission permission = UsbPermission.Unknown;
  private UsbSerialPort usbSerialPort;
  private SerialInputOutputManager ioManager;
  private UsbDeviceConnection usbDeviceConnection;
  private static final int WRITE_WAIT_MILLIS = 2000;
  private Handler mainLooper;
  private MutableLiveData<DataModel> dataModelMutableLiveData = new MutableLiveData<>();

  public MainViewModel(@NonNull Application application) {
    super(application);
    init();
  }

  private void init() {
    getConnectedStatus().setValue(Constants.ConnectStatus.DISCONNECTED);//default false
    mainLooper = new Handler(Looper.getMainLooper());
  }

  public void setConnectBean(ConnectBean connectBean) {
    this.connectBean = connectBean;
  }

  public void setPermission(UsbPermission permission) {
    this.permission = permission;
  }

  public MutableLiveData<Integer> getConnectedStatus() {
    return connectedStatus;
  }

  public void clearLocalData() {
    MMKV mmkv = MMKV.defaultMMKV();
    mmkv.remove(Constants.DEVICE_ID);
    mmkv.remove(Constants.PORT);
    mmkv.remove(Constants.BAUD_RATE);
    getConnectedStatus().setValue(Constants.ConnectStatus.DISCONNECTED);
  }

  public void sendAmplitudeSettingData(float v1) {

  }

  public void tryConnect() {
    if (permission == UsbPermission.Unknown || permission == UsbPermission.Granted) {
      if (connectBean != null) {
        mainLooper.post(this::connect);
      }
    }
  }

  private void connect() {
    UsbDevice device = null;
    UsbManager usbManager = (UsbManager) getApplication().getSystemService(Context.USB_SERVICE);
    for (UsbDevice ud : usbManager.getDeviceList().values()) {
      if (ud.getDeviceId() == connectBean.getDeviveID()) {
        device = ud;
        break;
      }
    }
    if (device == null) {
      //未找到设备
      setStatus(Constants.DeviceConnectFailedReason.NO_DEVICE);
      return;
    }
    //尝试默认的驱动
    UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
    //尝试客制化的驱动
    if (driver == null) {
      driver = CustomProber.getCustomProber().probeDevice(device);
    }
    if (driver == null) {
      //未找到驱动
      setStatus(Constants.DeviceConnectFailedReason.NO_DRIVER);
      return;
    }
    if (driver.getPorts().size() < connectBean.getPort()) {
      //端口不存在
      setStatus(Constants.DeviceConnectFailedReason.NO_PORT);
      return;
    }
    usbSerialPort = driver.getPorts().get(connectBean.getPort());
    usbDeviceConnection = usbManager.openDevice(driver.getDevice());
    //检查usb连接权限
    if (usbDeviceConnection == null
        && permission == UsbPermission.Unknown
        && !usbManager.hasPermission(driver.getDevice())) {
      int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0;
      PendingIntent usbPermissionIntent =
          PendingIntent.getBroadcast(getApplication(), 0, new Intent(INTENT_ACTION_GRANT_USB),
              flags);
      usbManager.requestPermission(driver.getDevice(), usbPermissionIntent);
      return;
    }
    if (usbDeviceConnection == null) {
      //归因打开Device失败的原因
      if (!usbManager.hasPermission(driver.getDevice())) {
        //没有权限
        setStatus(Constants.DeviceConnectFailedReason.PERMISSION_DENIED);
      } else {
        // 打开设备失败
        setStatus(Constants.DeviceConnectFailedReason.OPEN_DEVICE_FAILED);
      }
      return;
    }

    try {
      usbSerialPort.open(usbDeviceConnection);
      Log.e("ololeeTAG", "connect: baudRate: " + connectBean.getBaudRate());
      usbSerialPort.setParameters(connectBean.getBaudRate(), 8, UsbSerialPort.STOPBITS_1,
          UsbSerialPort.PARITY_NONE);
    } catch (Exception e) {
      e.printStackTrace();
      setStatus(Constants.DeviceConnectFailedReason.PARAMETER_ERROR);
      disconnect();
      return;
    }
    ioManager = new SerialInputOutputManager(usbSerialPort, this);
    ioManager.setReadBufferSize(1024);
    ioManager.start();
    setStatus(Constants.ConnectStatus.CONNECTED);
  }

  public void disconnect() {
    if (ioManager != null) {
      ioManager.setListener(null);
      ioManager.stop();
    }
    ioManager = null;
    try {
      usbSerialPort.close();
    } catch (IOException ignored) {
    }
    usbSerialPort = null;
    setStatus(Constants.ConnectStatus.DISCONNECTED);
  }

  private void setStatus(int status) {
    int finalStatus = status;
    if (status < 100) {
      //连接失败
      finalStatus = Constants.ConnectStatus.DISCONNECTED;
      switch (status) {
        case Constants.DeviceConnectFailedReason.NO_DEVICE:
          failedReason.postValue("未找到设备");
          break;
        case Constants.DeviceConnectFailedReason.NO_DRIVER:
          failedReason.postValue("未找到驱动");
          break;
        case Constants.DeviceConnectFailedReason.NO_PORT:
          failedReason.postValue("端口不存在");
          break;
        case Constants.DeviceConnectFailedReason.PERMISSION_DENIED:
          failedReason.postValue("没有权限");
          break;
        case Constants.DeviceConnectFailedReason.OPEN_DEVICE_FAILED:
          failedReason.postValue("打开设备失败");
          break;
        case Constants.DeviceConnectFailedReason.PARAMETER_ERROR:
          failedReason.postValue("参数错误");
          break;
        default:
          failedReason.postValue("未知错误");
          break;
      }
    }
    connectedStatus.postValue(finalStatus);
  }

  byte[] buffer = new byte[64];
  int index = 0;

  @Override
  public void onNewData(byte[] data) {
    if (index < 38) {
      System.arraycopy(data, 0, buffer, index, Math.min(data.length, 38 - index));
      index += data.length;
    }
    if (index == 38) {
      //Log.e("ololeeTAG", "onNewData: " + Arrays.toString(buffer));
      DataModel dataModel = DataDealUtils.formatData(buffer);
      dataModelMutableLiveData.postValue(dataModel);
    }
    if (index >= 38) {
      index = 0;
    }
  }

  public void send(byte[] data) {
    if (connectedStatus.getValue() != Constants.ConnectStatus.CONNECTED) {
      Toast.makeText(getApplication(), R.string.device_not_connected, Toast.LENGTH_SHORT).show();
      return;
    }
    try {
      usbSerialPort.write(data, WRITE_WAIT_MILLIS);
    } catch (Exception e) {
      onRunError(e);
    }
  }

  @Override
  public void onRunError(Exception e) {
    setStatus(Constants.DeviceConnectFailedReason.RUN_ERROR);
    disconnect();
  }

  public MutableLiveData<DataModel> getDataModelMutableLiveData() {
    return dataModelMutableLiveData;
  }
}
