package cn.ololee.usbserialassistant.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.ololee.usbserialassistant.R;

public class LocationButtons extends FrameLayout {
  public LocationButtons(@NonNull Context context) {
    super(context);
    init();
  }

  public LocationButtons(@NonNull Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public LocationButtons(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init(){
    addView(LayoutInflater.from(getContext()).inflate(R.layout.location_btns_layout,this,false));
  }
}
