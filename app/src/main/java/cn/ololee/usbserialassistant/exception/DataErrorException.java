package cn.ololee.usbserialassistant.exception;

public class DataErrorException extends IllegalArgumentException {

  public DataErrorException() {
  }

  public DataErrorException(String s) {
    super(s);
  }

  public DataErrorException(String message, Throwable cause) {
    super(message, cause);
  }
}
