package cn.ololee.usbserialassistant;

import android.app.Application;
import android.content.Context;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mmkv.MMKV;

public class App extends Application {
  public static App INSTANCE = null;
  @Override public void onCreate() {
    super.onCreate();
    MMKV.initialize(this);
    SDKInitializer.initialize(this);
    SDKInitializer.setCoordType(CoordType.BD09LL);
    CrashReport.initCrashReport(getApplicationContext(), "889afd63c6", true);
    INSTANCE = this;
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
  }
}
