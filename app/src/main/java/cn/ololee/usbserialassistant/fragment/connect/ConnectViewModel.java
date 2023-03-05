package cn.ololee.usbserialassistant.fragment.connect;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import cn.ololee.usbserialassistant.constants.bean.CustomProber;
import cn.ololee.usbserialassistant.constants.bean.UsbDeviceItem;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import java.util.ArrayList;
import java.util.List;

public class ConnectViewModel extends ViewModel {
  private static final int defaultBaudRate = 115200;
  private MutableLiveData<Integer> baudRateData = new MutableLiveData<>();
  private List<UsbDeviceItem> usbDeviceItemList = new ArrayList<>();
  private MutableLiveData<List<UsbDeviceItem>>  usbDeviceListMutableLiveData = new MutableLiveData<>();

  public ConnectViewModel() {
    baudRateData.setValue(defaultBaudRate);
    usbDeviceListMutableLiveData.setValue(usbDeviceItemList);
  }



  void refresh(Activity activity) {
    UsbManager usbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
    UsbSerialProber usbDefaultProber = UsbSerialProber.getDefaultProber();
    UsbSerialProber usbCustomProber = CustomProber.getCustomProber();
    usbDeviceItemList.clear();
    for(UsbDevice device : usbManager.getDeviceList().values()) {
      Log.e("TAG", "refresh: "+device.getProductId() );
      UsbSerialDriver driver = usbDefaultProber.probeDevice(device);
      if(driver == null) {
        driver = usbCustomProber.probeDevice(device);
      }
      if(driver != null) {
        for(int port = 0; port < driver.getPorts().size(); port++) {
          usbDeviceItemList.add(new UsbDeviceItem(device, port, driver));
        }
      }
    }
    usbDeviceListMutableLiveData.postValue(usbDeviceItemList);
  }


  public MutableLiveData<Integer> getBaudRateData() {
    return baudRateData;
  }

  public MutableLiveData<List<UsbDeviceItem>> getUsbDeviceListMutableLiveData() {
    return usbDeviceListMutableLiveData;
  }
}