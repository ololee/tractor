package cn.ololee.usbserialassistant;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import cn.ololee.usbserialassistant.bean.ConnectBean;
import cn.ololee.usbserialassistant.constants.Constants;
import cn.ololee.usbserialassistant.databinding.ActivityMainBinding;
import cn.ololee.usbserialassistant.fragment.amplitude.AmplitudeSettingFragment;
import cn.ololee.usbserialassistant.fragment.connect.ConnectFragment;
import cn.ololee.usbserialassistant.fragment.locate.LocatedFragment;
import cn.ololee.usbserialassistant.fragment.operation.OperationFragment;
import cn.ololee.usbserialassistant.fragment.operation.UsbPermission;
import cn.ololee.usbserialassistant.util.DataDealUtils;
import com.tencent.mmkv.MMKV;

public class MainActivity extends FragmentActivity {
  private ActivityMainBinding binding;
  private MainViewModel model;
  private BroadcastReceiver broadcastReceiver;
  public static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    init();
  }

  private void init() {
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

  public void setConnectStatusVisible(){
    binding.connectStatusDtv.setVisibility(View.VISIBLE);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(broadcastReceiver);
  }
}