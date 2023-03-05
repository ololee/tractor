package cn.ololee.usbserialassistant.constants.bean;

import android.hardware.usb.UsbDevice;
import com.hoho.android.usbserial.driver.UsbSerialDriver;

public class UsbDeviceItem {
  private UsbDevice usbDevice;
  private int port;
  private UsbSerialDriver driver;

  public UsbDeviceItem(UsbDevice usbDevice, int port,
      UsbSerialDriver driver) {
    this.usbDevice = usbDevice;
    this.port = port;
    this.driver = driver;
  }

  public UsbDevice getUsbDevice() {
    return usbDevice;
  }

  public void setUsbDevice(UsbDevice usbDevice) {
    this.usbDevice = usbDevice;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public UsbSerialDriver getDriver() {
    return driver;
  }

  public void setDriver(UsbSerialDriver driver) {
    this.driver = driver;
  }
}
