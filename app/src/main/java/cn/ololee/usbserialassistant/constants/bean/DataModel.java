package cn.ololee.usbserialassistant.constants.bean;

public class DataModel {
  /*
   * 横向偏差
   * */
  private float lateralDeviation;
  /*
   * 航向偏差
   * */
  private float courseDeviation;
  /**
   * 前轮转角
   */
  private float frontWheelAngle;

  /**
   * rtk航向
   */
  private float rtkDirection;

  /**
   * 车辆X的位置
   */
  private float vehicleX;

  /**
   * 车辆Y的位置
   */
  private float vehicleY;

  /**
   * rtk模式
   */
  private int rtkMode;

  private float baseLineAngle;

  /**
   * 到DA的距离
   */
  private float toDADistance;
  /**
    * 到BC的距离
   */
  private float toBCDistance;

  /**
   * 速度
   */
  private float speed;

  private float positionAX;
  private float positionAY;
  private float positionBX;
  private float positionBY;
  private float positionCX;
  private float positionCY;
  private float positionDX;
  private float positionDY;



  public DataModel() {
  }

  public DataModel(float lateralDeviation, float courseDeviation, float frontWheelAngle,
      float rtkDirection, float vehicleX, float vehicleY) {
    this.lateralDeviation = lateralDeviation;
    this.courseDeviation = courseDeviation;
    this.frontWheelAngle = frontWheelAngle;
    this.rtkDirection = rtkDirection;
    this.vehicleX = vehicleX;
    this.vehicleY = vehicleY;
  }

  public DataModel(float lateralDeviation, float courseDeviation, float frontWheelAngle,
      float rtkDirection, float vehicleX, float vehicleY, int rtkMode, float baseLineAngle) {
    this.lateralDeviation = lateralDeviation;
    this.courseDeviation = courseDeviation;
    this.frontWheelAngle = frontWheelAngle;
    this.rtkDirection = rtkDirection;
    this.vehicleX = vehicleX;
    this.vehicleY = vehicleY;
    this.rtkMode = rtkMode;
    this.baseLineAngle = baseLineAngle;
  }

  public float getLateralDeviation() {
    return lateralDeviation;
  }

  public void setLateralDeviation(float lateralDeviation) {
    this.lateralDeviation = lateralDeviation;
  }

  public float getCourseDeviation() {
    return courseDeviation;
  }

  public void setCourseDeviation(float courseDeviation) {
    this.courseDeviation = courseDeviation;
  }

  public float getFrontWheelAngle() {
    return frontWheelAngle;
  }

  public void setFrontWheelAngle(float frontWheelAngle) {
    this.frontWheelAngle = frontWheelAngle;
  }

  public float getRtkDirection() {
    return rtkDirection;
  }

  public void setRtkDirection(float rtkDirection) {
    this.rtkDirection = rtkDirection;
  }

  public float getVehicleX() {
    return vehicleX;
  }

  public void setVehicleX(float vehicleX) {
    this.vehicleX = vehicleX;
  }

  public float getVehicleY() {
    return vehicleY;
  }

  public void setVehicleY(float vehicleY) {
    this.vehicleY = vehicleY;
  }

  public int getRtkMode() {
    return rtkMode;
  }

  public void setRtkMode(int rtkMode) {
    this.rtkMode = rtkMode;
  }

  public float getBaseLineAngle() {
    return baseLineAngle;
  }

  public void setBaseLineAngle(float baseLineAngle) {
    this.baseLineAngle = baseLineAngle;
  }

  public float getToDADistance() {
    return toDADistance;
  }

  public void setToDADistance(float toDADistance) {
    this.toDADistance = toDADistance;
  }

  public float getToBCDistance() {
    return toBCDistance;
  }

  public void setToBCDistance(float toBCDistance) {
    this.toBCDistance = toBCDistance;
  }

  public float getSpeed() {
    return speed;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }

  public float getPositionAX() {
    return positionAX;
  }

  public void setPositionAX(float positionAX) {
    this.positionAX = positionAX;
  }

  public float getPositionAY() {
    return positionAY;
  }

  public void setPositionAY(float positionAY) {
    this.positionAY = positionAY;
  }

  public float getPositionBX() {
    return positionBX;
  }

  public void setPositionBX(float positionBX) {
    this.positionBX = positionBX;
  }

  public float getPositionBY() {
    return positionBY;
  }

  public void setPositionBY(float positionBY) {
    this.positionBY = positionBY;
  }

  public float getPositionCX() {
    return positionCX;
  }

  public void setPositionCX(float positionCX) {
    this.positionCX = positionCX;
  }

  public float getPositionCY() {
    return positionCY;
  }

  public void setPositionCY(float positionCY) {
    this.positionCY = positionCY;
  }

  public float getPositionDX() {
    return positionDX;
  }

  public void setPositionDX(float positionDX) {
    this.positionDX = positionDX;
  }

  public float getPositionDY() {
    return positionDY;
  }

  public void setPositionDY(float positionDY) {
    this.positionDY = positionDY;
  }

  @Override public String toString() {
    return "DataModel{" +
        "lateralDeviation=" + lateralDeviation +
        ", courseDeviation=" + courseDeviation +
        ", frontWheelAngle=" + frontWheelAngle +
        ", rtkDirection=" + rtkDirection +
        ", vehicleX=" + vehicleX +
        ", vehicleY=" + vehicleY +
        ", rtkMode=" + rtkMode +
        ", baseLineAngle=" + baseLineAngle +
        '}';
  }
}
