package cn.ololee.usbserialassistant.fragment.operation;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import cn.ololee.usbserialassistant.MainActivity;
import cn.ololee.usbserialassistant.MainViewModel;
import cn.ololee.usbserialassistant.bean.DataModel;
import cn.ololee.usbserialassistant.exception.DataErrorException;
import cn.ololee.usbserialassistant.util.DataDealUtils;
import cn.ololee.usbserialassistant.util.NumberFormatUtils;

public class OperationViewModel extends ViewModel{
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
     lateralDeviationData.postValue( NumberFormatUtils.formatFloat(dataModel.getLateralDeviation()));
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
     speedData.postValue(NumberFormatUtils.formatFloat(dataModel.getSpeed()));
   }catch (DataErrorException e){
     errorTips(e.getMessage());
     }
  }

  private void errorTips(String str) {
    errorTipsMutableLiveData.postValue(str);
  }


  private String getLocation(DataModel dataModel){
    return String.format("(%s,%s)",
        NumberFormatUtils.formatFloat(dataModel.getVehicleX()),
        NumberFormatUtils.formatFloat(dataModel.getVehicleY()));
  }

  /**
   * 速度控制及 悬挂行程升降控制
   */
  public void control(int code){
    byte[] sendDataCode = DataDealUtils.sendControlCodeFunc(code);
    send(sendDataCode);
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

  private void send(byte[] data){
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
}