package cn.ololee.usbserialassistant.util;

import java.text.DecimalFormat;

public class NumberFormatUtils {
  private static DecimalFormat decimalFormat = new DecimalFormat("0.000");
  private static DecimalFormat speedFormat = new DecimalFormat("0.0");
  public static String formatFloat(float number){
    return decimalFormat.format(number);
  }


  public static String formatSpeed(float number){
    return speedFormat.format(number);
  }
}
