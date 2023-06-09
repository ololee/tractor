package cn.ololee.usbserialassistant.util;

import android.util.Log;
import cn.ololee.usbserialassistant.constants.bean.DataModel;
import cn.ololee.usbserialassistant.exception.DataErrorException;
import java.nio.ByteBuffer;

public class DataDealUtils {
  public static final String TAG = DataDealUtils.class.getSimpleName();
  private static DataModel dataModel = new DataModel();

  public static DataModel formatData(byte[] data) {
    if (data == null) {
      return null;
    }
    if (data[0] != 0x5a) {

    }
    switch (data[1]) {
      case (byte) 0xc1:
        //Log.e(TAG, "formatData:c1,isC1DataValid: " + isC1DataValid(data));
        c1Data(data);
        break;
      /*
       * 基准线角度
       * */
      case (byte) 0xb5:
        Log.e(TAG, "formatData:b1,isB1DataValid: " + isB1DataValid(data));
        b5Data(data);
        break;
      /**
       * 设置ABCD的位置
       */
      case (byte) 0xb1:
        setAPosition(data);
        break;
      case (byte) 0xb2:
        setBPosition(data);
        break;
      case (byte) 0xb3:
        setCPosition(data);
        break;
      case (byte) 0xb4:
        setDPosition(data);
        break;
      case (byte) 0xf1:
        Log.e(TAG, "formatData:F1,isF1DataValid: " + isF1DataValid(data));
        f1Data(data);
        break;
    }

    return dataModel;
  }

  private static void c1Data(byte[] data) {
    try {
      /**
       * 横向偏差
       */
      dataModel.setLateralDeviation(DataCastUtils.byte2float(data, 2));
      /**
       * 航向偏差
       */
      dataModel.setCourseDeviation(DataCastUtils.byte2float(data, 6));
      /**
       * 前轮转角
       */
      dataModel.setFrontWheelAngle(DataCastUtils.byte2float(data, 10));
      /**
       * rtk航向
       */
      dataModel.setRtkDirection(DataCastUtils.byte2float(data, 14));
      /**
       * 车辆X位置
       */
      dataModel.setVehicleX(DataCastUtils.byte2float(data, 18));
      /**
       * 车辆Y位置
       */
      dataModel.setVehicleY(DataCastUtils.byte2float(data, 22));
      /**
       * 到BC距离
       */
      dataModel.setToBCDistance(DataCastUtils.byte2float(data, 26));
      /**
       * 到DA距离
       */
      dataModel.setToDADistance(DataCastUtils.byte2float(data, 30));
      /**
       * 速度
       */
      byte speedByte = data[34];
      /**
       * 行号
       */
      byte lineNumber = data[35];
      if(lineNumber<0) {
        dataModel.setLineNo(lineNumber + 256);
      }else {
        dataModel.setLineNo(lineNumber);
      }
      if (speedByte < 0) {
        dataModel.setSpeed((speedByte + 256) * 0.1f);
      } else {
        dataModel.setSpeed(speedByte * 0.1f);
      }
      /**
       * rtk模式
       */
      dataModel.setRtkMode(data[36]);
    } catch (Exception e) {
      e.printStackTrace();
      //throw new DataErrorException();
    }
  }

  private static void b5Data(byte[] data) {
    try {
      dataModel.setRtkMode(data[2]);
    } catch (Exception e) {
      throw new DataErrorException();
    }
  }

  private static void f1Data(byte[] data) {
    try {
      dataModel.setBaseLineAngle(DataCastUtils.byte2float(data, 2));
    } catch (Exception e) {
      throw new DataErrorException();
    }
  }

  public static byte[] sendControlCodeFunc(int functionCode) {
    byte[] data = new byte[4];
    data[0] = (byte) 0xa5;
    data[1] = (byte) 0xff;
    data[2] = (byte) functionCode;
    data[3] = calcCRC8CheckData(data, 3);
    return data;
  }

  public static byte[] sendLiftControllCodeFunc(int functionCode) {
    byte[] data = new byte[4];
    data[0] = (byte) 0xa5;
    data[1] = (byte) 0xff;
    data[2] = (byte) functionCode;
    data[3] = calcCRC8CheckData(data, 3);
    return data;
  }

  @Deprecated
  /**
   * 因为direction的范围是[-1,1]float
   * 而现在我们需要发送的方向数据应该是[0,255] byte
   */
  public static byte[] sendDirectionCodeFunc(float direction) {
    int intDir = (int) (256 * ((direction + 1.0)*0.5));
    intDir = Math.min(255,intDir);
    Log.e("ololeeTAG","sendDirectionCodeFunc direction: "+direction+",intDir: "+intDir);
    byte[] data = new byte[5];
    data[0] = (byte) 0xa5;
    data[1] = (byte) 0xf1;
    data[2] = (byte) intDir;
    data[3] = calcCRC8CheckData(data, 3);
    data[4] = (byte) 0x5a;
    Log.e("ololeeTAG",bytes2HexString(data,false));
    return data;
  }

  private static final char[] HEX_DIGITS_UPPER =
      {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
  private static final char[] HEX_DIGITS_LOWER =
      {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

  public static String bytes2HexString(final byte[] bytes, boolean isUpperCase) {
    if (bytes == null) {
      return "";
    }
    char[] hexDigits = isUpperCase ? HEX_DIGITS_UPPER : HEX_DIGITS_LOWER;
    int len = bytes.length;
    if (len <= 0) {
      return "";
    }
    char[] ret = new char[len << 1];
    for (int i = 0, j = 0; i < len; i++) {
      ret[j++] = hexDigits[bytes[i] >> 4 & 0x0f];
      ret[j++] = hexDigits[bytes[i] & 0x0f];
    }
    return new String(ret);
  }

  public static byte[] sendDirectionCodeFunc(int direction) {
    byte[] data = new byte[4];
    data[0] = (byte) 0xa5;
    data[1] = (byte) 0xf1;
    data[2] = (byte) direction;
    data[3] = calcCRC8CheckData(data, 3);
    return data;
  }

  public static byte[] sendThrottleCodeFunc(int direction) {
    byte[] data = new byte[4];
    data[0] = (byte) 0xa5;
    data[1] = (byte) 0xf2;
    data[2] = (byte) direction;
    data[3] = calcCRC8CheckData(data, 3);
    return data;
  }

  public static byte[] sendLiftThrottleCodeFunc(int direction) {
    byte[] data = new byte[4];
    data[0] = (byte) 0xa5;
    data[1] = (byte) 0xf3;
    data[2] = (byte) direction;
    data[3] = calcCRC8CheckData(data, 3);
    return data;
  }

  private static boolean validData(byte[] data, int len) {
    return CRCCheckUtils.CRC8_Table_Check(CastUtils.byteArray2IntArray(data, len), len, data[len])
        != 0;
  }

  /**
   * c1数据验证
   */
  public static boolean isC1DataValid(byte[] data) {
    return CRCCheckUtils.CRC8_Table_Check(CastUtils.byteArray2IntArray(data, 26), 26, data[26])
        != 0;
  }

  /**
   * B1数据验证
   */
  public static boolean isB1DataValid(byte[] data) {
    return CRCCheckUtils.CRC8_Table_Check(CastUtils.byteArray2IntArray(data, 6), 6, data[6]) != 0;
  }

  /**
   * F1数据验证
   */
  public static boolean isF1DataValid(byte[] data) {
    return CRCCheckUtils.CRC8_Table_Check(CastUtils.byteArray2IntArray(data, 3), 3, data[3]) != 0;
  }

  public static byte calcCRC8CheckData(byte[] data, int len) {
    return (byte) CRCCheckUtils.CRC8_Table(CastUtils.byteArray2IntArray(data, len), len);
  }

  public static byte calcCRC8CheckData(byte[] data) {
    return (byte) CRCCheckUtils.CRC8_Table(CastUtils.byteArray2IntArray(data, data.length),
        data.length);
  }

  public static void setAPosition(byte[] data) {
    try {
      dataModel.setPositionAX(DataCastUtils.byte2float(data, 2));
      dataModel.setPositionAY(DataCastUtils.byte2float(data, 6));
    } catch (Exception e) {
      throw new DataErrorException();
    }
  }

  public static void setBPosition(byte[] data) {
    try {
      dataModel.setPositionBX(DataCastUtils.byte2float(data, 2));
      dataModel.setPositionBY(DataCastUtils.byte2float(data, 6));
    } catch (Exception e) {
      throw new DataErrorException();
    }
  }

  public static void setCPosition(byte[] data) {
    try {
      dataModel.setPositionCX(DataCastUtils.byte2float(data, 2));
      dataModel.setPositionCY(DataCastUtils.byte2float(data, 6));
    } catch (Exception e) {
      throw new DataErrorException();
    }
  }

  public static void setDPosition(byte[] data) {
    try {
      dataModel.setPositionDX(DataCastUtils.byte2float(data, 2));
      dataModel.setPositionDY(DataCastUtils.byte2float(data, 6));
    } catch (Exception e) {
      throw new DataErrorException();
    }
  }

  public static byte[] sendAmplitudeWidth(float amplitude) {
    byte[] data = new byte[7];
    byte[] byteArray = DataCastUtils.float2ByteArray(amplitude);
    data[0] = (byte) 0xa5;
    data[1] = (byte) 0xff;
    for (int i = 0; i < 4; i++) {
      data[i + 2] = byteArray[i];
    }
    data[6] = calcCRC8CheckData(data, 6);
    return data;
  }
}
