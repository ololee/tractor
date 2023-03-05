package cn.ololee.usbserialassistant;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import cn.ololee.usbserialassistant.constants.Constants;
import cn.ololee.usbserialassistant.databinding.ActivityMainBinding;
import cn.ololee.usbserialassistant.fragment.amplitude.AmplitudeSettingFragment;
import cn.ololee.usbserialassistant.fragment.connect.ConnectFragment;
import cn.ololee.usbserialassistant.fragment.locate.LocatedFragment;
import cn.ololee.usbserialassistant.fragment.operation.OperationFragment;
import cn.ololee.usbserialassistant.fragment.operation.UsbPermission;
import cn.ololee.usbserialassistant.util.DataDealUtils;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
  private ActivityMainBinding binding;
  private MainViewModel model;
  private BroadcastReceiver broadcastReceiver;
  public static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
  private static final int PERMISSION_REQUEST_CODE = 0x01;
  private boolean isPermissionRequested = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // 申请动态权限
    requestPermission();
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    init();
  }

  private void init() {
    broadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        if (INTENT_ACTION_GRANT_USB.equals(intent.getAction())) {
          UsbPermission usbPermission =
              intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                  ? UsbPermission.Granted : UsbPermission.Denied;
          if (getModel() != null) {
            getModel().setPermission(usbPermission);
            getModel().tryConnect();
          }
        }
      }
    };
    registerReceiver(broadcastReceiver, new IntentFilter(INTENT_ACTION_GRANT_USB));
    ViewModelProvider.Factory viewModelFactory =
        (ViewModelProvider.Factory) new MainViewModelFactory(getApplication());
    model = new ViewModelProvider(this, viewModelFactory).get(MainViewModel.class);
    binding.setViewModel(model);
    initEvent();
    gotoOperationFragment();
  }

  private void initEvent() {
    model.getConnectedStatus().observe(this, connected -> {
      switch (connected) {
        case Constants.ConnectStatus.CONNECTED:
          binding.connectStatusDtv.setText(R.string.connected);
          binding.connectStatusDtv.setImageResource(R.drawable.connected);
          break;
        case Constants.ConnectStatus.CONNECTING:
          binding.connectStatusDtv.setText(R.string.connecting);
          binding.connectStatusDtv.setImageResource(R.drawable.connected);
          break;
        case Constants.ConnectStatus.DISCONNECTING:
          binding.connectStatusDtv.setText(R.string.disconnecting);
          binding.connectStatusDtv.setImageResource(R.drawable.connected);
          break;
        case Constants.ConnectStatus.DISCONNECTED:
        default:
          binding.connectStatusDtv.setText(R.string.disconnected);
          binding.connectStatusDtv.setImageResource(R.drawable.disconnected);
          break;
      }
    });
    binding.connectStatusDtv.setOnClickListener(v -> {
      switch (model.getConnectedStatus().getValue()) {
        case Constants.ConnectStatus.CONNECTED:
          getModel().disconnect();
          break;
        case Constants.ConnectStatus.CONNECTING:
          break;
        case Constants.ConnectStatus.DISCONNECTING:
          break;
        /**
         * 未连接，点击去往连接界面
         */
        case Constants.ConnectStatus.DISCONNECTED:
        default:
          gotoConnectFragment();
          break;
      }
    });
    binding.connectStatusDtv.setOnLongClickListener(v -> {
      // showForgetDialog();
      return true;
    });
  }

  public void showForgetDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.forget_device);
    builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
      clearLocalData();
    });
    builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
    });
    builder.show();
  }

  public void clearLocalData() {
    model.clearLocalData();
  }

  public void gotoConnectFragment() {
    binding.connectStatusDtv.setVisibility(View.GONE);
    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction transaction = fm.beginTransaction();
    Fragment connectFragment = ConnectFragment.newInstance();
    transaction.replace(R.id.fragment, connectFragment);
    transaction.addToBackStack("connect");
    transaction.commit();
  }

  public void gotoOperationFragment() {
    binding.connectStatusDtv.setVisibility(View.VISIBLE);
    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction transaction = fm.beginTransaction();
    Fragment operationFragment = OperationFragment.newInstance();
    transaction.replace(R.id.fragment, operationFragment);
    transaction.commit();
  }

  public void gotoAmplitudeSettingFragment() {
    binding.connectStatusDtv.setVisibility(View.GONE);
    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction transaction = fm.beginTransaction();
    Fragment amplitudeSettingFragment = AmplitudeSettingFragment.newInstance();
    transaction.replace(R.id.fragment, amplitudeSettingFragment);
    transaction.addToBackStack("amplitudeSetting");
    transaction.commit();
  }

  public void gotoLocateFragment() {
    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction transaction = fm.beginTransaction();
    LocatedFragment locatedFragment = LocatedFragment.newInstance();
    transaction.replace(R.id.fragment, locatedFragment);
    transaction.addToBackStack("locate");
    transaction.commit();
  }

  public MainViewModel getModel() {
    return model;
  }

  @Override
  protected void onNewIntent(Intent intent) {
    if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(intent.getAction())) {

    }
    super.onNewIntent(intent);
  }

  public void setting() {
    gotoAmplitudeSettingFragment();
  }

  public void sendAmplitudeSettingData(float amplitude) {
    byte[] amplitudeSettingData = DataDealUtils.sendAmplitudeWidth(amplitude);
    getModel().send(amplitudeSettingData);
    binding.connectStatusDtv.setVisibility(View.VISIBLE);
    onBackPressed();
  }

  public void setConnectStatusVisible() {
    binding.connectStatusDtv.setVisibility(View.VISIBLE);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(broadcastReceiver);
  }

  /**
   * Android6.0之后需要动态申请权限
   */
  private void requestPermission() {
    if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {
      isPermissionRequested = true;
      ArrayList<String> permissionsList = new ArrayList<>();
      String[] permissions = {
          Manifest.permission.ACCESS_NETWORK_STATE,
          Manifest.permission.INTERNET,
          Manifest.permission.WRITE_EXTERNAL_STORAGE,
          Manifest.permission.READ_EXTERNAL_STORAGE,
          Manifest.permission.ACCESS_COARSE_LOCATION,
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_WIFI_STATE,
          Manifest.permission.ACCESS_NOTIFICATION_POLICY
      };

      for (String perm : permissions) {
        if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
          permissionsList.add(perm);
          // 进入到这里代表没有权限.
        }
      }

      if (!permissionsList.isEmpty()) {
        String[] strings = new String[permissionsList.size()];
        requestPermissions(permissionsList.toArray(strings), PERMISSION_REQUEST_CODE);
      }
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }
}