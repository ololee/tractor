package cn.ololee.usbserialassistant.fragment.operation;

import android.util.Log;
import androidx.databinding.DataBindingUtil;
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
import cn.ololee.usbserialassistant.databinding.FragmentOperationBinding;

public class OperationFragment extends Fragment
    implements View.OnClickListener {
  private OperationViewModel mViewModel;
  private FragmentOperationBinding binding;

  private boolean startBtnStatus = false;
  //手动控制部分成员变量
  private float lastThrottleValue = 0.0f, lastLaterDirectionValue = 0.0f;
  private boolean isAutoMode = true;

  public static OperationFragment newInstance() {
    OperationFragment operationFragment = new OperationFragment();
    return operationFragment;
  }

  public OperationFragment() {

  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_operation, container, false);
    binding = DataBindingUtil.bind(view);
    init();
    return view;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  private void init() {
    initView();
  }

  private void initView() {
    binding.btnTurnLeft.setOnClickListener(this);
    binding.btnTurnRight.setOnClickListener(this);
    binding.startPauseBtn.setOnClickListener(this);
    binding.amplitudeSettings.setOnClickListener(this);
    binding.locateSetting.setOnClickListener(this);
    binding.autoManualSwitch.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          binding.setManualMode(isChecked);
          isAutoMode = !isChecked;
        });

    /**
     * 手动部分
     */
    binding.lateralMoveBar.setSlideCallback(value -> {
      if (lastLaterDirectionValue != value) {
        sendDirectionCode(value);
      }
      lastLaterDirectionValue = value;
    });
    binding.throttle.setSlideCallback(value -> {
      if (lastThrottleValue != value) {
        sendThrottle(value);
      }
      lastThrottleValue = value;
    });
    binding.btnForward.setOnClickListener(this);
    binding.btnStop.setOnClickListener(this);
    binding.btnBack.setOnClickListener(this);
    binding.btnLift.setOnClickListener(this);
    binding.btnDown.setOnClickListener(this);
    binding.btnFlip.setOnClickListener(this);
    /**
     * 回中
     */
    binding.rudderCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
      binding.lateralMoveBar.setBackToCenter(isChecked);
    });
    binding.rudderCheckBox.setChecked(true);
    binding.lateralMoveBar.setBackToCenter(true);
    binding.throttleSwitchCheckBox.setChecked(false);
    binding.throttle.setTouchEnable(binding.throttleSwitchCheckBox.isChecked());
    binding.throttleSwitchCheckBox.setOnCheckedChangeListener((v, isChecked) -> {
      binding.throttle.setTouchEnable(isChecked);
    });
  }

  private void initEvent() {
    MainActivity activity = (MainActivity) getActivity();
    activity.getModel()
        .getDataModelMutableLiveData()
        .observe(getViewLifecycleOwner(), dataModel -> mViewModel.receive(dataModel));
    //基准线角度
    mViewModel.getBaselineAngleData()
        .observe(getViewLifecycleOwner(),
            baselineAngleData -> binding.baselineAngleTv.setText(baselineAngleData));
    //到BC的距离
    mViewModel.getBCDistanceData()
        .observe(getViewLifecycleOwner(),
            bcDistanceData -> binding.bCDistanceTv.setText(bcDistanceData));
    //到DA的距离
    mViewModel.getDADistanceData()
        .observe(getViewLifecycleOwner(),
            daDistanceData -> binding.dADistanceTv.setText(daDistanceData));
    //航向偏差
    mViewModel.getCourseDeviationData()
        .observe(getViewLifecycleOwner(),
            courseDeviationData -> binding.courseDeviationTv.setText(courseDeviationData));
    //前轮转角
    mViewModel.getFrontWheelAngleData()
        .observe(getViewLifecycleOwner(),
            frontWheelAngleData -> binding.frontWheelAngleTv.setText(frontWheelAngleData));
    //RTK模式
    mViewModel.getRtkModeData()
        .observe(getViewLifecycleOwner(), rtkModeData -> binding.rtkModeTv.setText(rtkModeData));
    //横向偏差
    mViewModel.getLateralDeviationData()
        .observe(getViewLifecycleOwner(),
            lateralDeviationData -> binding.lateralDeviationTv.setText(lateralDeviationData));
    //拖拉机位置
    mViewModel.getLocationData()
        .observe(getViewLifecycleOwner(), locationData -> binding.locationTv.setText(locationData))
    ;
    //速度
    mViewModel.getSpeedData()
        .observe(getViewLifecycleOwner(), speedData -> binding.speedTv.setText(speedData));
  }

  @Override public void onResume() {
    super.onResume();
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mViewModel = new ViewModelProvider(this).get(OperationViewModel.class);
    mViewModel.init((MainActivity) getActivity());
    initEvent();
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_turn_left:
        sendData(0xe1);
        break;
      case R.id.btn_turn_right:
        sendData(0xe2);
        break;
      case R.id.start_pause_btn:
        binding.startPauseBtn.setText(startBtnStatus ? R.string.start : R.string.stop);
        binding.startPauseBtn.setTextColor(
            getResources().getColor(startBtnStatus ? R.color.green : R.color.red));
        sendData(startBtnStatus ? 0xE3 : 0xE4);
        startBtnStatus = !startBtnStatus;
        break;
      case R.id.amplitude_settings:
        if (getActivity() instanceof MainActivity) {
          MainActivity activity = (MainActivity) getActivity();
          activity.setting();
        }
        break;

      /**
       * 手动部分
       */
      case R.id.btn_forward:
        control(0x1f);
        break;
      case R.id.btn_stop:
        control(0x00);
        break;
      case R.id.btn_back:
        control(0x1b);
        break;
      case R.id.btn_lift:
        control(0xc1);
        break;
      case R.id.btn_down:
        control(0xc2);
        break;
      case R.id.btn_flip:
        control(0xc3);
        break;
      case R.id.locate_setting:
        if (getActivity() instanceof MainActivity) {
          MainActivity activity = (MainActivity) getActivity();
          activity.gotoLocateFragment();
        }
        break;
    }
  }

  public void sendData(int functionCode) {
    mViewModel.sendData(functionCode);
  }

  /**
   * 速度控制及 悬挂行程升降控制
   */
  public void control(int code) {
    mViewModel.control(code);
  }

  public void sendDirectionCode(float x) {
    mViewModel.sendDirectionCode(x);
  }

  public void sendThrottle(float x) {
    mViewModel.sendThrottle(x);
  }
}