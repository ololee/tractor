package cn.ololee.usbserialassistant.util;

public class CastUtils {
  public static int[] byteArray2IntArray(byte[] ba, int len) {
    try {
      int[] result = new int[len];
      for (int i = 0; i < len; i++) {
        if (ba[i] < 0) {
          result[i] = ba[i] + 256;
        } else {
          result[i] = ba[i];
        }
      }
      return result;
    }catch(ArrayIndexOutOfBoundsException e){
      return new int[]{0,0,0,0};
    }
  }

  public static byte[] intArray2ByteArray(int[] ia,int len){
    byte[] result = new byte[len];
    //System.arraycopy(ia,0,result,0,len);
    for (int i = 0; i < len; i++) {
      result[i]= (byte) ia[i];
    }
    return result;
  }
}
