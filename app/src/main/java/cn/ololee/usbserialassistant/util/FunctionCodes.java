package cn.ololee.usbserialassistant.util;

public interface FunctionCodes {
  /**
   * 方向盘电机使能
   */
  int STEER_ENABLE = 0xE5;
  /**
   * 方向盘电机失能
   */
  int STEER_DISABLE = 0xE6;
  /**
   * 行号增加
   */
  int LINE_INCREASE = 0xE7;
  /**
   * 行号减少
   */
  int LINE_DECREASE = 0xE8;
}
