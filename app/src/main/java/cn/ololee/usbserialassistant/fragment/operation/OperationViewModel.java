package cn.ololee.usbserialassistant.fragment.operation;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import cn.ololee.usbserialassistant.MainActivity;
import cn.ololee.usbserialassistant.MainViewModel;
import cn.ololee.usbserialassistant.constants.bean.DataModel;
import cn.ololee.usbserialassistant.constants.Constants;
import cn.ololee.usbserialassistant.exception.DataErrorException;
import cn.ololee.usbserialassistant.util.DataDealUtils;
import cn.ololee.usbserialassistant.util.FunctionCodes;
import cn.ololee.usbserialassistant.util.NumberFormatUtils;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import static cn.ololee.usbserialassistant.util.ConfigConstants.SHOW_MAP;
import static cn.ololee.usbserialassistant.util.ConfigConstants.SHOW_TRACK_LOCATION;

public class OperationViewModel extends ViewModel {
  private MainActivity activity;
  private MutableLiveData<String> errorTipsMutableLiveData = new MutableLiveData<>();

  /**
   * 自动模式数据
   */
  private MutableLiveData<String> vichleDirectionData = new MutableLiveData<>();//车辆航向信息
  private MutableLiveData<String> courseDeviationData = new MutableLiveData<>();//车辆航向偏差
  private MutableLiveData<String> frontWheelAngleData = new MutableLiveData<>();//前轮转角
  private MutableLiveData<String> DADistanceData = new MutableLiveData<>();//到DA 距离
  private MutableLiveData<String> BCDistanceData = new MutableLiveData<>();//到BC 距离
  private MutableLiveData<String> lateralDeviationData = new MutableLiveData<>();//横向偏差
  private MutableLiveData<String> baselineAngleData = new MutableLiveData<>();//基准线角度
  private MutableLiveData<String> speedData = new MutableLiveData<>();//车速
  private MutableLiveData<String> locationData = new MutableLiveData<>();//拖拉机位置
  private MutableLiveData<Float> amplitudeData = new MutableLiveData<>();//幅宽数据
  private MutableLiveData<String> rtkModeData = new MutableLiveData<>();//RTK模式
  private MutableLiveData<String> rtkDirectionData = new MutableLiveData<>();//RTK方向
  private MutableLiveData<LatLng> latLngMutableLiveData = new MutableLiveData<>();//拖拉机位置

  private MutableLiveData<Integer> lineNoMutableLiveData = new MutableLiveData<>();//当前行号
  private BaiduMap baiduMap = null;
  private LocationClient locationClient = null;
  private MyLocationListener myLocationListener = null;
  private boolean isFirstLocate = true;
  private double lastVichleLat = 0;
  private double lastVichleLng = 0;

  public void init(MainActivity activity) {
    this.activity = activity;
    errorTipsMutableLiveData.setValue("");
  }

  public MainViewModel getMainViewModel() {
    return activity.getModel();
  }

  public MutableLiveData<Integer> getConnectedStatus() {
    return getMainViewModel().getConnectedStatus();
  }

  public void receive(DataModel dataModel) {
    try {
      lateralDeviationData.postValue(
          NumberFormatUtils.formatFloat(dataModel.getLateralDeviation()));
      courseDeviationData.postValue(
          NumberFormatUtils.formatFloat(dataModel.getCourseDeviation()));
      frontWheelAngleData.postValue(
          NumberFormatUtils.formatFloat(dataModel.getFrontWheelAngle()));
      rtkDirectionData.postValue(
          NumberFormatUtils.formatFloat(dataModel.getRtkDirection()));
      rtkModeData.postValue(dataModel.getRtkMode() + "");
      baselineAngleData.postValue(
          NumberFormatUtils.formatFloat(dataModel.getBaseLineAngle()));

      locationData.postValue(getLocation(dataModel));

      /**
       * 设置DA距离
       */
      DADistanceData.postValue(NumberFormatUtils.formatFloat(dataModel.getToDADistance()));
      /**
       * 设置BC距离
       */
      BCDistanceData.postValue(NumberFormatUtils.formatFloat(dataModel.getToBCDistance()));

      /**
       * 设置速度
       */
      speedData.postValue(NumberFormatUtils.formatSpeed(dataModel.getSpeed()));

      /**
       * 设置行号
       */
      lineNoMutableLiveData.postValue(dataModel.getLineNo());
    } catch (DataErrorException e) {
      errorTips(e.getMessage());
    }
  }

  private void errorTips(String str) {
    errorTipsMutableLiveData.postValue(str);
  }

  private String getLocation(DataModel dataModel) {
    float vehicleX = dataModel.getVehicleX();
    float vehicleY = dataModel.getVehicleY();
    gotoLocation(vehicleX, vehicleY);
    return String.format("(%s,%s)",
        NumberFormatUtils.formatFloat(dataModel.getVehicleX()),
        NumberFormatUtils.formatFloat(dataModel.getVehicleY()));
  }

  /**
   * 速度控制及 悬挂行程升降控制
   */
  public void control(int code) {
    byte[] sendDataCode = DataDealUtils.sendControlCodeFunc(code);
    send(sendDataCode);
  }

  /**
   * 增加行号/减少行号
   */
  public void addOrSubtractRow(boolean add) {
    int code = add ? FunctionCodes.LINE_INCREASE : FunctionCodes.LINE_DECREASE;
    control(code);
  }

  /**
   * 方向盘电机使能/失能
   * @param enable
   */
  public void enableSteer(boolean enable) {
    int code = enable ? FunctionCodes.STEER_ENABLE : FunctionCodes.STEER_DISABLE;
    control(code);
  }

  public void sendDirectionCode(float dir) {
    byte[] sendDataCode = DataDealUtils.sendDirectionCodeFunc(dir);
    send(sendDataCode);
  }

  public void sendThrottle(float x) {
    int throttle = (int) (x * -128) + 128;
    if (throttle == 256) {
      throttle = 255;
    }
    byte[] sendDataCode = DataDealUtils.sendThrottleCodeFunc(throttle);
    send(sendDataCode);
  }

  public void sendData(int functionCode) {
    byte[] sendDataCode = DataDealUtils.sendControlCodeFunc(functionCode);
    send(sendDataCode);
  }

  private void send(byte[] data) {
    activity.getModel().send(data);
  }

  public MutableLiveData<String> getErrorTipsMutableLiveData() {
    return errorTipsMutableLiveData;
  }

  public MutableLiveData<String> getVichleDirectionData() {
    return vichleDirectionData;
  }

  public MutableLiveData<String> getCourseDeviationData() {
    return courseDeviationData;
  }

  public MutableLiveData<String> getFrontWheelAngleData() {
    return frontWheelAngleData;
  }

  public MutableLiveData<String> getDADistanceData() {
    return DADistanceData;
  }

  public MutableLiveData<String> getBCDistanceData() {
    return BCDistanceData;
  }

  public MutableLiveData<String> getLateralDeviationData() {
    return lateralDeviationData;
  }

  public MutableLiveData<String> getBaselineAngleData() {
    return baselineAngleData;
  }

  public MutableLiveData<String> getSpeedData() {
    return speedData;
  }

  public MutableLiveData<String> getLocationData() {
    return locationData;
  }

  public MutableLiveData<Float> getAmplitudeData() {
    return amplitudeData;
  }

  public MutableLiveData<String> getRtkModeData() {
    return rtkModeData;
  }

  public MutableLiveData<String> getRtkDirectionData() {
    return rtkDirectionData;
  }

  public MutableLiveData<LatLng> getLatLngMutableLiveData() {
    return latLngMutableLiveData;
  }

  public MutableLiveData<Integer> getLineNoMutableLiveData() {
    return lineNoMutableLiveData;
  }

  public BaiduMap getBaiduMap() {
    return baiduMap;
  }

  public void initBaiduMap(MapView bdmapView) {
    baiduMap = bdmapView.getMap();
    //将百度map默认设为不可见
    if(SHOW_MAP){
      baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
    }else{
      baiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
    }
    //打开定位图层
    baiduMap.setMyLocationEnabled(SHOW_TRACK_LOCATION);
    //定位初始化
    //设置隐私政策接口
    locationClient = new LocationClient(activity);
    myLocationListener = new MyLocationListener();
    locationClient.registerLocationListener(myLocationListener);

    LocationClientOption option = new LocationClientOption();
    option.setScanSpan(1000 * 1000);
    option.setIsNeedAddress(true);
    option.setOpenGps(true);
    option.setCoorType(Constants.BD_COOR_TYPE);
    locationClient.setLocOption(option);
    locationClient.start();
  }

  public void stopLocate() {
    if (locationClient != null) {
      locationClient.stop();
    }
    if (baiduMap != null) {
      baiduMap.setMyLocationEnabled(false);
    }
  }

  public class MyLocationListener implements BDLocationListener {
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
      // map view 销毁后不在处理新接收的位置
      if (bdLocation == null) {
        return;
      }
      MyLocationData locData = new MyLocationData.Builder()
          .accuracy(bdLocation.getRadius())
          // 此处设置开发者获取到的方向信息，顺时针0-360
          .direction(bdLocation.getDirection())
          .latitude(bdLocation.getLatitude())
          .longitude(bdLocation.getLongitude())
          .build();
      baiduMap.setMyLocationData(locData);

      if (isFirstLocate) {
        Log.e("ololeeTAG", "onReceiveLocation: " + bdLocation.getLatitude() + " " + bdLocation.getLongitude());
        gotoLocation(bdLocation.getLatitude(), bdLocation.getLongitude());
        isFirstLocate = false;
      }
    }
  }

  public void gotoLocation(double latitude, double longitude) {
    if (lastVichleLat == latitude && lastVichleLng == longitude) {
      return;
    }
    LatLng ll = new LatLng(latitude, longitude);
    MapStatus.Builder builder = new MapStatus.Builder();
    builder.target(ll).zoom(18.0f);
    baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    lastVichleLat = latitude;
    lastVichleLng = longitude;
    latLngMutableLiveData.postValue(ll);
  }
}