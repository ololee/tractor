package cn.ololee.usbserialassistant.util;

import java.text.DecimalFormat;

public class NumberFormatUtils {
  private static DecimalFormat decimalFormat = new DecimalFormat("0.000");
  public static String formatFloat(float number){
    return decimalFormat.format(number);
  }
}
