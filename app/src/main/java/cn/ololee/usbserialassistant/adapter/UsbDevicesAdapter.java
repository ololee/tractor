package cn.ololee.usbserialassistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cn.ololee.usbserialassistant.R;
import cn.ololee.usbserialassistant.constants.bean.UsbDeviceItem;
import cn.ololee.usbserialassistant.databinding.UsbDevicesItemBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UsbDevicesAdapter extends RecyclerView.Adapter<UsbDevicesAdapter.VH> {
  private List<UsbDeviceItem> data = new ArrayList<>();
  private OnItemClickListener itemClickListener;

  public UsbDevicesAdapter() {
  }

  public void setUsbDevices(List<UsbDeviceItem> data) {
    this.data = data;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.usb_devices_item, parent, false);
    UsbDevicesItemBinding binding = UsbDevicesItemBinding.bind(view);
    return new VH(binding);
  }

  @Override public void onBindViewHolder(@NonNull VH holder, int position) {
    UsbDeviceItem item = data.get(position);
    if (item.getDriver() != null) {
      if (item.getDriver().getPorts().size() == 1) {
        holder.binding.title.setText(
            item.getDriver().getClass().getSimpleName().replace("SerialDriver", ""));
      } else {
        holder.binding.title.setText(
            item.getDriver().getClass().getSimpleName().replace("SerialDriver", "")
                + ", Port "
                + item.getPort());
      }
      holder.binding.content.setText(String.format(Locale.US, "厂商ID: %04X, 产品ID: %04X",
          item.getUsbDevice().getVendorId(), item.getUsbDevice().getProductId()));
    }
    holder.binding.getRoot().setOnClickListener(v -> {
      if (itemClickListener != null) {
        itemClickListener.onItemClick(item);
      }
    });
  }

  @Override public int getItemCount() {
    return data.size();
  }

  public void setOnItemClickListener(
      OnItemClickListener itemClickListener) {
    this.itemClickListener = itemClickListener;
  }

  public class VH extends RecyclerView.ViewHolder {
    private UsbDevicesItemBinding binding;

    public VH(@NonNull UsbDevicesItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }
  }

  public interface OnItemClickListener {
    void onItemClick(UsbDeviceItem item);
  }
}
