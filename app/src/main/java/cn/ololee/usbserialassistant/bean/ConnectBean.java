package cn.ololee.usbserialassistant.bean;

public class ConnectBean {
  private int deviveID;
  private int port;
  private int baudRate;

  public ConnectBean(int deviveID, int port, int baudRate) {
    this.deviveID = deviveID;
    this.port = port;
    this.baudRate = baudRate;
  }

  public int getDeviveID() {
    return deviveID;
  }

  public void setDeviveID(int deviveID) {
    this.deviveID = deviveID;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getBaudRate() {
    return baudRate;
  }

  public void setBaudRate(int baudRate) {
    this.baudRate = baudRate;
  }

  @Override public String toString() {
    return "ConnectBean{" +
        "deviveID=" + deviveID +
        ", port=" + port +
        ", baudRate=" + baudRate +
        '}';
  }
}
