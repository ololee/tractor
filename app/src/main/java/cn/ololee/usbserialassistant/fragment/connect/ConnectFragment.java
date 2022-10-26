package cn.ololee.usbserialassistant.fragment.connect;

import android.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import cn.ololee.usbserialassistant.MainActivity;
import cn.ololee.usbserialassistant.R;
import cn.ololee.usbserialassistant.adapter.UsbDevicesAdapter;
import cn.ololee.usbserialassistant.bean.ConnectBean;
import cn.ololee.usbserialassistant.bean.UsbDeviceItem;
import cn.ololee.usbserialassistant.databinding.FragmentConnectBinding;
import java.util.Arrays;

public class ConnectFragment extends Fragment {
  private FragmentConnectBinding binding;
  private ConnectViewModel mViewModel;
  private UsbDevicesAdapter adapter;

  public static ConnectFragment newInstance() {
    return new ConnectFragment();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_connect, container, false);
    binding = FragmentConnectBinding.bind(root);
    mViewModel = new ViewModelProvider(this).get(ConnectViewModel.class);
    init();
    return root;
  }

  private void init() {
    initView();
    initEvent();
    initData();
  }

  private void initData() {
    mViewModel.refresh(getActivity());
  }

  private void initView() {
    adapter = new UsbDevicesAdapter();
    binding.usbDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.usbDevicesRecyclerView.setAdapter(adapter);
  }

  private void initEvent() {

    adapter.setOnItemClickListener(item -> {
      gotoOperation(item);
    });
    binding.baudSettingsFab.setOnClickListener(v -> {
      showBaudSettings();
    });
    binding.mainSwipe.setOnRefreshListener(() -> {
      binding.mainSwipe.setRefreshing(false);
      mViewModel.refresh(getActivity());
    });

    mViewModel.getUsbDeviceListMutableLiveData().observe(getViewLifecycleOwner(),
        usbDeviceItems -> {
          adapter.setUsbDevices(usbDeviceItems);
          binding.notFoundDevicesTip.setVisibility(
              usbDeviceItems.size() == 0 ? View.VISIBLE : View.GONE);
        });
  }

  /**
   * 设置波特率
   */
  private void showBaudSettings() {
    final String[] values = getResources().getStringArray(R.array.baud_rates);
    int baudRate = mViewModel.getBaudRateData().getValue();
    int pos = Arrays.asList(values).indexOf(String.valueOf(baudRate));
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(R.string.baud_rate);
    builder.setSingleChoiceItems(values, pos, (dialog, which) -> {
      mViewModel.getBaudRateData().setValue(Integer.parseInt(values[which]));
      dialog.dismiss();
    });
    builder.create().show();
  }

  private void gotoOperation(UsbDeviceItem item) {
    MainActivity activity = (MainActivity) getActivity();
    int baudRate = mViewModel.getBaudRateData().getValue();
    Log.e("ololeeTAG", "gotoOperation: baudRate: " + baudRate);
    ConnectBean connectBean =
        new ConnectBean(item.getUsbDevice().getDeviceId(), item.getPort(), baudRate);
    activity.getModel().setConnectBean(connectBean);
    activity.getModel().tryConnect();
    activity.gotoOperationFragment();
  }
}