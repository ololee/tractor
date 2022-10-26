package cn.ololee.usbserialassistant.constants;

public interface Constants {
  interface ConnectStatus {
    int CONNECTING = 100;
    int CONNECTED = 101;
    int DISCONNECTING = 102;
    int DISCONNECTED = 103;
  }

  interface DeviceConnectFailedReason {
    //Device not found
    int NO_DEVICE = 0;
    // No Driver for Device
    int NO_DRIVER = 1;
    //not enough ports at device
    int NO_PORT = 2;
    // permission denied
    int PERMISSION_DENIED = 3;
    //open device failed
    int OPEN_DEVICE_FAILED = 4;
    //parameter error
    int PARAMETER_ERROR = 5;
    //run error
    int RUN_ERROR = 6;
  }

  /**
   * 波特率
   */
  String BAUD_RATE = "baud_rate";
  /**
   * 设备ID
   */
  String DEVICE_ID = "device_id";
  /**
   * 端口
   */
  String PORT = "port";
}
