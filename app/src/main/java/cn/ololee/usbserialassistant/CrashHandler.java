package cn.ololee.usbserialassistant;

import android.widget.Toast;
import androidx.annotation.NonNull;
import com.qmuiteam.qmui.util.QMUIToastHelper;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
  @Override public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
    Toast toast = Toast.makeText(App.INSTANCE, e.getLocalizedMessage(), Toast.LENGTH_SHORT);
    QMUIToastHelper.show(toast);
  }
}
