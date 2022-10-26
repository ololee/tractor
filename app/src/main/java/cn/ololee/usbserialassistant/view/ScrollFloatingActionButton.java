package cn.ololee.usbserialassistant.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import cn.ololee.usbserialassistant.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ScrollFloatingActionButton extends FloatingActionButton {
  private float mX;
  private float mY;
  private int mParentWidth;
  private int mParentHeight;
  private boolean mScrollEnable = true;
  private int mScrollLeft;
  private int mScrollTop;
  private int mRight;
  private int mScrollRight;
  private int mScrollBottom;
  private boolean hasScroll;
  boolean isScroll = false;

  public ScrollFloatingActionButton(Context context) {
    this(context, null);
  }

  public ScrollFloatingActionButton(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ScrollFloatingActionButton(Context context, AttributeSet attrs, final int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScrollFloatingActionButton);
    mScrollEnable = ta.getBoolean(R.styleable.ScrollFloatingActionButton_scrollEnable, true);
    ta.recycle();
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    //防止布局重置时重置ScrollFloatinigButton的位置
    ViewParent parent = getParent();
    if (parent instanceof ViewGroup) {
      ((ViewGroup) getParent()).addOnLayoutChangeListener(new OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
          if (hasScroll && mScrollRight != 0 && mScrollBottom != 0) {
            layout(mScrollLeft, mScrollTop, mScrollRight, mScrollBottom);
          }
        }
      });
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    if (getParent() instanceof ViewGroup) {
      mParentWidth = ((ViewGroup) getParent()).getWidth();
      mParentHeight = ((ViewGroup) getParent()).getHeight();
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    if (!mScrollEnable) {
      return super.onTouchEvent(ev);
    }
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        mX = ev.getX();
        mY = ev.getY();
        super.onTouchEvent(ev);
        return true;
      case MotionEvent.ACTION_MOVE:
        int scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        float x = ev.getX();
        float y = ev.getY();
        x = x - mX;
        y = y - mY;
        if (Math.abs(scaledTouchSlop) < Math.abs(x) || Math.abs(scaledTouchSlop) < Math.abs(y)) {
          isScroll = true;
        }
        if (isScroll) {
          mScrollLeft = (int) (getX() + x);
          mScrollTop = (int) (getY() + y);
          mScrollRight = (int) (getX() + getWidth() + x);
          mScrollBottom = (int) (getY() + getHeight() + y);
          //防止滑出父界面
          if (mScrollLeft < 0 || mScrollRight > mParentWidth) {
            mScrollLeft = (int) getX();
            mScrollRight = (int) getX() + getWidth();
          }
          if (mScrollTop < 0 || mScrollBottom > mParentHeight) {
            mScrollTop = (int) getY();
            mScrollBottom = (int) getY() + getHeight();
          }
          layout(mScrollLeft, mScrollTop, mScrollRight, mScrollBottom);
          hasScroll = true;
          return true;
        }
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        if (isScroll) {
          isScroll = false;
          setPressed(false);//重置点击状态
          return true;
        }
        break;
    }
    return super.onTouchEvent(ev);
  }

  public void setScrollEnable(boolean scrollEnable) {
    mScrollEnable = scrollEnable;
  }

  public boolean isScrollEnable() {
    return mScrollEnable;
  }
}

