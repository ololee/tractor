package cn.ololee.usbserialassistant.fragment.amplitude;

import androidx.lifecycle.ViewModel;
import cn.ololee.usbserialassistant.MainActivity;

public class AmplitudeSettingViewModel extends ViewModel {
  private MainActivity activity;
  public void init(MainActivity activity){
    this.activity=activity;
  }

  public void sendAmplitudeSettingData(float v1) {
    activity.sendAmplitudeSettingData(v1);
  }
}