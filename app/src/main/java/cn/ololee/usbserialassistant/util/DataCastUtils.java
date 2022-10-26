package cn.ololee.usbserialassistant.util;

public class DataCastUtils {

  public static int byteArray2int(byte[] data, int offset) {
    for (int i = 0; i < 4; i++) {
      data[offset + i] = (byte) (data[offset + i] - 128);
    }
    return ((((int) data[offset + 3]) + 128) << 24)
        | ((((int) data[offset + 2]) + 128) << 16)
        | ((((int) data[offset + 1]) + 128) << 8)
        | (((int) data[offset]) + 128);
  }


  public static float byte2float(byte[] data, int offset) {
    return Float.intBitsToFloat(byteArray2int(data, offset));
  }

  public static byte[] int2ByteArray(int origin) {
    byte[] arr = new byte[4];
    arr[0] = (byte) (origin & 0xff);
    arr[1] = (byte) ((origin >> 8) & 0xff);
    arr[2] = (byte) ((origin >> 16) & 0xff);
    arr[3] = (byte) ((origin >> 24) & 0xff);
    return arr;
  }

  public static byte[] float2ByteArray(float origin) {
    return int2ByteArray(Float.floatToIntBits(origin));
  }
}
