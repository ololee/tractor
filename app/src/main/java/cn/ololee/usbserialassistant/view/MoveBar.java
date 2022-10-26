package cn.ololee.usbserialassistant.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import cn.ololee.usbserialassistant.R;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

public class MoveBar extends View {
  /**
   * out line paint
   */
  private Paint outlinePaint = new Paint();
  /**
   * finger paint
   */
  private Paint fingerPaint = new Paint();

  private Path outlinePath = new Path();

  private int pressedCircleColor = Color.parseColor("#ededed");
  private int circleColor = Color.WHITE;
  private int backgroundColor = Color.parseColor("#d1d2d6");

  private float radius = 20.0f;
  private float outlineLen = 200.f;
  private float current = radius + outlineLen * 0.5f;
  private ValueAnimator positionAnimator;
  private SlideCallback slideCallback;
  private boolean backToCenter = false;

  private boolean isUpRight = true;

  private boolean useGradientColor = false;
  private int bgStartColor = Color.parseColor("#ff0000");
  private int bgEndColor = Color.parseColor("#0000ff");
  private boolean touchEnable = true;

  public MoveBar(Context context) {
    super(context);
  }

  public MoveBar(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MoveBar);
    isUpRight = ta.getBoolean(R.styleable.MoveBar_isUpRight, isUpRight);
    radius = QMUIDisplayHelper.dpToPx((int) ta.getDimension(R.styleable.MoveBar_barRadius, radius));
    outlineLen =
        QMUIDisplayHelper.dpToPx((int) ta.getDimension(R.styleable.MoveBar_length, outlineLen));
    current = radius + outlineLen * ta.getFloat(R.styleable.MoveBar_defaultInitValue, 0.5f);
    circleColor = ta.getColor(R.styleable.MoveBar_moveBarCircleColor, circleColor);
    pressedCircleColor =
        ta.getColor(R.styleable.MoveBar_moveBarCirclePressedColor, pressedCircleColor);
    backgroundColor = ta.getColor(R.styleable.MoveBar_moveBarBGColor, backgroundColor);
    useGradientColor = ta.getBoolean(R.styleable.MoveBar_useBGGradientColor, useGradientColor);
    bgStartColor = ta.getColor(R.styleable.MoveBar_startColor, bgStartColor);
    bgEndColor = ta.getColor(R.styleable.MoveBar_endColor, bgEndColor);
    ta.recycle();
    init();
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int width, height;
    int resultWSpec = widthMeasureSpec;
    int resultHSpec = heightMeasureSpec;
    int longLen = (int) (2 * radius + outlineLen);
    int shortLen = (int) (2 * radius);
    switch (widthMode) {
      case MeasureSpec.AT_MOST:
        width = isUpRight ? shortLen : longLen;
        resultWSpec = MeasureSpec.makeMeasureSpec(width, widthMode);
        break;
    }
    switch (heightMode) {
      case MeasureSpec.AT_MOST:
        height = isUpRight ? longLen : shortLen;
        resultHSpec = MeasureSpec.makeMeasureSpec(height, heightMode);
        break;
    }
    super.onMeasure(resultWSpec, resultHSpec);
  }

  private void init() {
    Shader shader=null;
    if (isUpRight) {
      RectF outLineTopCircle = new RectF(0, 0, 2 * radius, 2 * radius);
      RectF outLineBottomCircle = new RectF(0, outlineLen, 2 * radius, 2 * radius + outlineLen);
      outlinePath.moveTo(0, radius);
      outlinePath.addArc(outLineBottomCircle, 0, 180);
      outlinePath.lineTo(2 * radius, radius);
      outlinePath.addArc(outLineTopCircle, 0, -180);
      outlinePath.lineTo(0, radius + outlineLen);
      shader =  new LinearGradient(radius, 0, radius, 2 * radius + outlineLen, bgStartColor, bgEndColor,
          Shader.TileMode.CLAMP);
    } else {
      RectF outLineLeftCircle = new RectF(0, 0, 2 * radius, 2 * radius);
      RectF outLineRightCircle = new RectF(outlineLen, 0, 2 * radius + outlineLen, 2 * radius);
      outlinePath.moveTo(radius, 0);
      outlinePath.addArc(outLineRightCircle, 90, -180);
      outlinePath.lineTo(radius, 2 * radius);
      outlinePath.addArc(outLineLeftCircle, 270, -180);
      outlinePath.lineTo(radius + outlineLen, 0);
      shader = new LinearGradient(0, radius, 2 * radius + outlineLen, radius, bgStartColor, bgEndColor,
          Shader.TileMode.CLAMP);
    }
    if(!useGradientColor){
      outlinePaint.setColor(backgroundColor);
    }else {
      outlinePaint.setShader(shader);
    }
    outlinePaint.setAntiAlias(true);

    fingerPaint.setColor(Color.WHITE);
    fingerPaint.setAntiAlias(true);

    positionAnimator = ValueAnimator.ofFloat(1);
    positionAnimator.addUpdateListener(animation -> {
      Float currentProgress = (Float) animation.getAnimatedValue();
      chageFingerPosition(
          (current - (radius + 0.5f * outlineLen)) * currentProgress + radius + outlineLen * 0.5f);
    });
    positionAnimator.setDuration(1000);
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    if(!touchEnable) {
      return false;
    }
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
      case MotionEvent.ACTION_MOVE:
        fingerPaint.setColor(pressedCircleColor);
        if (isUpRight) {
          if (event.getY() < radius) {
            current = radius;
          } else if (event.getY() > (radius + outlineLen)) {
            current = radius + outlineLen;
          } else {
            current = event.getY();
          }
        } else {
          if (event.getX() < radius) {
            current = radius;
          } else if (event.getX() > (radius + outlineLen)) {
            current = radius + outlineLen;
          } else {
            current = event.getX();
          }
        }
        chageFingerPosition(current);
        positionAnimator.cancel();
        break;
      case MotionEvent.ACTION_UP:
        if (backToCenter) {
          positionAnimator.start();
        }
        fingerPaint.setColor(circleColor);
        break;
    }
    return true;
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawPath(outlinePath, outlinePaint);
    canvas.drawCircle(isUpRight ? radius : current, isUpRight ? current : radius, radius,
        fingerPaint);
  }

  private void chageFingerPosition(float value) {
    if (slideCallback != null) {
      slideCallback.slide((2.0f * (value - radius) - outlineLen) / (outlineLen));
    }
    current = value;
    invalidate();
  }

  public interface SlideCallback {
    void slide(float value);
  }

  public void setSlideCallback(SlideCallback slideCallback) {
    this.slideCallback = slideCallback;
  }

  public void setBackToCenter(boolean backToCenter) {
    this.backToCenter = backToCenter;
    if (current != (radius + 0.5 * outlineLen)) {
      current = (float) (radius + 0.5 * outlineLen);
      if(slideCallback!=null) {
        slideCallback.slide(current);
      }
      invalidate();
    }
  }

  public void setTouchEnable(boolean touchEnable) {
    this.touchEnable = touchEnable;
  }
}
