package cn.ololee.usbserialassistant.fragment.locate;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.ololee.usbserialassistant.MainActivity;
import cn.ololee.usbserialassistant.R;
import cn.ololee.usbserialassistant.databinding.FragmentLocatedBinding;
import cn.ololee.usbserialassistant.util.DataDealUtils;
import cn.ololee.usbserialassistant.util.NumberFormatUtils;

/**
 * 打点模式
 */
public class LocatedFragment extends Fragment implements View.OnClickListener {
  private FragmentLocatedBinding binding;


  public static LocatedFragment newInstance() {
    return new LocatedFragment();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_located, container, false);
    binding = FragmentLocatedBinding.bind(view);
    View btnA = binding.locationBtns.findViewById(R.id.btn_a);
    View btnB = binding.locationBtns.findViewById(R.id.btn_b);
    View btnC = binding.locationBtns.findViewById(R.id.btn_c);
    View btnD = binding.locationBtns.findViewById(R.id.btn_d);
    btnA.setOnClickListener(this);
    btnB.setOnClickListener(this);
    btnC.setOnClickListener(this);
    btnD.setOnClickListener(this);
    binding.btnBack.setOnClickListener(this);
    init();
    return view;
  }

  private void init() {
    initEvent();
  }

  private void initEvent() {
    MainActivity activity = (MainActivity) getActivity();
    activity.getModel()
        .getDataModelMutableLiveData()
        .observe(getViewLifecycleOwner(), dataModel -> {
          binding.baselineAngleTv.setText(NumberFormatUtils.formatFloat(dataModel.getBaseLineAngle()));
          binding.positionATv.setText(formatPoint(dataModel.getPositionAX(), dataModel.getPositionAY()));
          binding.positionBTv.setText(formatPoint(dataModel.getPositionBX(), dataModel.getPositionBY()));
          binding.positionCTv.setText(formatPoint(dataModel.getPositionCX(), dataModel.getPositionCY()));
          binding.positionDTv.setText(formatPoint(dataModel.getPositionDX(), dataModel.getPositionDY()));
        });
  }



  @Override public void onClick(View v) {
    MainActivity activity = (MainActivity) getActivity();
    byte[] locateCmd = null;
    switch (v.getId()) {
      case R.id.btn_a:
        locateCmd = DataDealUtils.sendControlCodeFunc((byte) 0xaa);
        break;
      case R.id.btn_b:
        locateCmd = DataDealUtils.sendControlCodeFunc((byte) 0xbb);
        break;
      case R.id.btn_c:
        locateCmd = DataDealUtils.sendControlCodeFunc((byte) 0xcc);
        break;
      case R.id.btn_d:
        locateCmd = DataDealUtils.sendControlCodeFunc((byte) 0xdd);
        break;
      case R.id.btn_back:
        activity.onBackPressed();
        return;
    }
    activity.getModel().send(locateCmd);
  }

  private String formatPoint(float x, float y) {
    return String.format("(%s,%s)",NumberFormatUtils.formatFloat(x),NumberFormatUtils.formatFloat(y));
  }
}