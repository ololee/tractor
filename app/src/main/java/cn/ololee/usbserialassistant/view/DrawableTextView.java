package cn.ololee.usbserialassistant.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

import cn.ololee.usbserialassistant.R;


/**
 * Created by Corey_Jia on 2019-06-11.
 */
public class DrawableTextView extends AppCompatTextView {
    public static final int LEFT = 1, TOP = 2, RIGHT = 3, BOTTOM = 4;

    private int mHeight, mWidth;

    private Drawable mDrawable;

    private int mLocation;

    public DrawableTextView(Context context) {
        this(context, null);
    }

    public DrawableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DrawableTextView);
        mWidth = a.getDimensionPixelSize(R.styleable.DrawableTextView_drawable_width, 0);
        mHeight = a.getDimensionPixelSize(R.styleable.DrawableTextView_drawable_height, 0);
        mDrawable = a.getDrawable(R.styleable.DrawableTextView_drawable_src);
        mLocation = a.getInt(R.styleable.DrawableTextView_drawable_location, LEFT);
        a.recycle();
        //绘制Drawable宽高,位置
        drawDrawable();

    }

    /**
     * 绘制Drawable宽高,位置
     */
    public void drawDrawable() {
        if (mDrawable != null) {
            Drawable drawable;
            if (!(mDrawable instanceof BitmapDrawable)) {
                drawable = mDrawable;
            } else {
                Bitmap bitmap = ((BitmapDrawable) mDrawable).getBitmap();
                if (mWidth != 0 && mHeight != 0) {
                    drawable = new BitmapDrawable(getResources(), getBitmap(bitmap, mWidth, mHeight));
                } else {
                    drawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true));
                }
            }
            switch (mLocation) {
                case LEFT:
                    this.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                    break;
                case TOP:
                    this.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                    break;
                case RIGHT:
                    this.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                    break;
                case BOTTOM:
                    this.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
                    break;
            }
        }
    }

    public void setNullDrawable(){
        this.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    /**
     * 缩放图片
     *
     * @param bm
     * @param newWidth
     * @param newHeight
     * @return
     */
    public Bitmap getBitmap(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = (float) newWidth / width;
        float scaleHeight = (float) newHeight / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    /**
     * 设置图片
     * @param res
     */
    public void setImageResource(int res){
        mDrawable = getResources().getDrawable(res);
        drawDrawable();
    }

    /**
     * 设置图片
     *
     * @param res
     * @param location
     */
    public void setLocationDrawable(int res, int location) {
        mDrawable = getResources().getDrawable(res);
        mLocation = location;
        drawDrawable();
    }

    /**
     * 设置图片
     * @param res
     * @param location
     * @param width
     * @param height
     */
    public void setLocationDrawable(int res, int location, int width, int height) {
        mDrawable = getResources().getDrawable(res);
        mLocation = location;
        mWidth = width;
        mHeight = height;
        drawDrawable();
    }

}
