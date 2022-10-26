package cn.ololee.usbserialassistant.fragment.amplitude;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.ololee.usbserialassistant.MainActivity;
import cn.ololee.usbserialassistant.R;
import cn.ololee.usbserialassistant.databinding.FragmentAmplitudeSettingBinding;

public class AmplitudeSettingFragment extends Fragment {
  private FragmentAmplitudeSettingBinding binding;
  private AmplitudeSettingViewModel mViewModel;

  public static AmplitudeSettingFragment newInstance() {
    return new AmplitudeSettingFragment();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_amplitude_setting, container, false);
    binding = FragmentAmplitudeSettingBinding.bind(view);
    init();
    return binding.getRoot();
  }

  private void init(){
    initView();
  }

  private void initView(){
    binding.saveAmplitudeWidthBtn.setOnClickListener(v->{
      String s = binding.amplitudeSettingsEt.getText().toString();
      if (s != null && !s.isEmpty()) {
        float v1 = Float.parseFloat(s);
        if (Float.isNaN(v1)) {
          v1 = 0.0f;
        }
        sendAmplitudeSettingData(v1);
      }
    });
  }



  private void sendAmplitudeSettingData(float v1) {
    mViewModel.sendAmplitudeSettingData(v1);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mViewModel = new ViewModelProvider(this).get(AmplitudeSettingViewModel.class);
    mViewModel.init((MainActivity) getActivity());
  }
}