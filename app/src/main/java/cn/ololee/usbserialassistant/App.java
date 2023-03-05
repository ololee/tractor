package cn.ololee.usbserialassistant;

import android.app.Application;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.tencent.mmkv.MMKV;

public class App extends Application {
  @Override public void onCreate() {
    super.onCreate();
    MMKV.initialize(this);
    SDKInitializer.initialize(this);
    SDKInitializer.setCoordType(CoordType.BD09LL);
  }
}
