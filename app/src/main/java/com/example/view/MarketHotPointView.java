package com.example.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.dashboard.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 中心热点气泡图
 * Created by yzh on 2023/8/18 19:04.
 */
public class MarketHotPointView extends View {
    //把当前画布平分成 宽高 6x4 的格子
    private static final int X_NUM = 6;
    private static final int Y_NUM = 4;

    private static final PathEffect LINE_EFFECT = new DashPathEffect(new float[]{20, 10}, 0);//网格虚线
    private RectF mTempRect = new RectF();
    // 默认控件大小
    private final static int DEFAULT_SIZE = 250;
    private int mRiseColor, mRiseColor70;
    private int mFallColor, mFallColor70;
    private int mColor1, mColor2, mColor3;
    private int mBorderColor;
    private Paint mBorderPaint;
    private Paint mBgPaint;
    private Paint mCirclePaint;

    private Paint mCircleStrokePaint;
    public static final int MAX_POINT_SIZE = 15;
    protected Paint mPaintText;
    private static final float TEXT_SIZE = 11f;
    float mProgress;//动画进度百分比

    private List<MyData> mDataList=new ArrayList<>();

    ItemClickListener mItemClickListener;

    private List<Region> mRegionList = new ArrayList<>();
    private int mClickIndex;

    public void setItemClickListener(ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void setDataList(List<MyData> mDataList) {
        this.mDataList = mDataList;
        invalidate();
    }

    public MarketHotPointView(Context context) {
        this(context, null);
    }

    public MarketHotPointView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarketHotPointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mRiseColor = ContextCompat.getColor(context, R.color.up_common_rise_color);
        mFallColor = ContextCompat.getColor(context, R.color.up_common_fall_color);
        mRiseColor70 = ContextCompat.getColor(context, R.color.up_common_rise_color_70);
        mFallColor70 = ContextCompat.getColor(context, R.color.up_common_fall_color_70);
        mColor1 = ContextCompat.getColor(context, R.color.up_market_jqrd_circle1);
        mColor2 = ContextCompat.getColor(context, R.color.up_market_jqrd_circle2);
        mColor3 = ContextCompat.getColor(context, R.color.up_market_jqrd_circle3);
        mBorderColor = ContextCompat.getColor(context, R.color.up_common_divider_strong_color);
        mBorderPaint = new Paint();
        mBorderPaint.setStrokeWidth(1);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mClickIndex=-1;
        mBgPaint = new Paint();
        mBgPaint.setDither(true);
        mBgPaint.setAntiAlias(true);

        mCirclePaint = new Paint();
        mCirclePaint.setDither(true);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mCircleStrokePaint = new Paint();
        mCircleStrokePaint.setDither(true);
        mCircleStrokePaint.setAntiAlias(true);
        mCircleStrokePaint.setStyle(Paint.Style.STROKE);
        mCircleStrokePaint.setStrokeWidth(dp2px(4));

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(sp2px(TEXT_SIZE));
        mPaintText.setColor(ContextCompat.getColor(context, R.color.up_common_black_text));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = dp2px(DEFAULT_SIZE);
//        mPadding = Math.max(Math.max(getPaddingLeft(), getPaddingTop()),
//                Math.max(getPaddingRight(), getPaddingBottom()));
//        mPadding = Math.max(dp2px(DEFAULT_PADDING), mPadding);

        setMeasuredDimension(measureSize(widthMeasureSpec, size), measureSize(heightMeasureSpec, size));
    }

    /**
     * 判断当前控件宽高类型
     */
    private int measureSize(int measureSpec, int defaultSize) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                break;
            case MeasureSpec.EXACTLY:
                defaultSize = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        return defaultSize;
    }

    private float getUnitWidth() {
        float cellW = (float) getWidth() / X_NUM;
        return cellW / 10f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int drawerWidth = getWidth();
        int drawerHeight = getHeight();

        drawBorder(canvas, drawerWidth, drawerHeight);
        drawBgCircle(canvas, mBgPaint, drawerWidth, drawerHeight);
        drawBubble(canvas, drawerWidth, drawerHeight);
    }

    private void drawBubble(Canvas canvas, int drawerWidth, int drawerHeight) {
        mRegionList.clear();
        for (int i = 0; i < mDataList.size(); i++) {
            if(i>=MAX_POINT_SIZE) break;
            MyData data=mDataList.get(i);
            data.isSelect=data.index==mClickIndex;
            drawPoint(canvas, drawerWidth, drawerHeight, data);
        }
    }

    private void drawBgCircle(Canvas canvas, Paint paint, int drawerWidth, int drawerHeight) {
        //float centerY = (float) mGraphRect.bottom / 2;
        float centerY = (float) drawerHeight / 2;
        float centerX = (float) drawerWidth / 2;
        paint.reset();
        paint.setAlpha(255);
        paint.setColor(mColor1);
        float radius = (float) drawerWidth / 5;
        canvas.drawCircle(centerX, centerY, radius, paint);

        paint.setColor(mColor2);
        canvas.drawCircle(centerX, centerY, radius * 1.8f, paint);

        paint.setColor(mColor3);
        canvas.drawCircle(centerX, centerY, radius * 2.4f, paint);
    }

    private void drawPoint(Canvas canvas, int drawerWidth, int drawerHeight, MyData myData) {

        float cellW = (float) drawerWidth / X_NUM;
        float cellH = (float) drawerHeight / Y_NUM;

        float centerY = (float) drawerHeight / 2;
        float centerX = (float) drawerWidth / 2;
        mCirclePaint.reset();
        mCirclePaint.setAlpha(255);
        if(myData.isSelect){
            mCirclePaint.setColor(myData.index % 2 == 0 ? mRiseColor70 : mFallColor70);
        }else{
            mCirclePaint.setColor(myData.index % 2 == 0 ? mRiseColor : mFallColor);
        }
        mCircleStrokePaint.setColor(myData.index % 2 == 0 ? mRiseColor70 : mFallColor70);
        float radius;
        if (myData.index == 0) {
            radius = cellW / 2;
        } else {
            float maxRadius = cellW / 2 - getUnitWidth()*2;
            float minRadius = cellW / 15;
            radius = (float) Math.min(maxRadius, getUnitWidth()/2 * (myData.ratio + 1));
            radius = Math.max(radius, minRadius);
        }
        float offsetX, offsetY;
        if (myData.index == 1) {
            offsetX = getUnitWidth() * 3;
            centerX = cellW * 2 + offsetX;
            centerY = cellH * 3;
        } else if (myData.index == 2) {
            offsetX = -getUnitWidth() * 3;
            offsetY = -getUnitWidth();
            centerX = cellW * 2.5f + offsetX;
            centerY = cellH+offsetY;
        } else if (myData.index == 3) {
            offsetX = getUnitWidth() * 2;
            centerX = cellW * 4 + offsetX;
            centerY = cellH * 3;
        } else if (myData.index == 4) {
            offsetX = -getUnitWidth();
            offsetY = -getUnitWidth();
            centerX = cellW * 4 + offsetX;
            centerY = cellH + offsetY;
        } else if (myData.index == 5) {
            offsetX = getUnitWidth();
            offsetY = -getUnitWidth() * 5;
            centerX = cellW + offsetX;
            centerY = cellH + offsetY;
        } else if (myData.index == 6) {
            offsetX = -getUnitWidth() * 2;
            offsetY = -getUnitWidth() * 3;
            centerX = cellW * 2 + offsetX;
            centerY = cellH * 2 + offsetY;
        } else if (myData.index == 7) {
            offsetX = -getUnitWidth() * 4;
            offsetY = getUnitWidth() * 4;
            centerX = cellW + offsetX;
            centerY = cellH * 3 + offsetY;
        } else if (myData.index == 8) {
            offsetX = -getUnitWidth() * 6;
            offsetY = getUnitWidth() * 3;
            centerX = cellW + offsetX;
            centerY = cellH * 2 + offsetY;
        } else if (myData.index == 9) {
            offsetX = -getUnitWidth() * 4;
            offsetY = getUnitWidth() * 4;
            centerX = cellW + offsetX;
            centerY = cellH  + offsetY;
        } else if (myData.index == 10) {
            offsetX = getUnitWidth();
            offsetY = -getUnitWidth() * 3;
            centerX = cellW + offsetX;
            centerY = cellH * 3 + offsetY;
        } else if (myData.index == 11) {
            offsetY = -getUnitWidth() * 3;
            centerX = cellW * 5;
            centerY = cellH  + offsetY;
        } else if (myData.index == 12) {
            offsetX = getUnitWidth() * 5;
            offsetY = getUnitWidth() * 5;
            centerX = cellW * 5 + offsetX;
            centerY = cellH + offsetY;
        } else if (myData.index == 13) {
            offsetX = getUnitWidth() * 3;
            offsetY = getUnitWidth() * 2;
            centerX = cellW * 5 + offsetX;
            centerY = cellH * 3 + offsetY;
        } else if (myData.index == 14) {
            offsetX = -getUnitWidth() * 4;
            offsetY = getUnitWidth() * 2;
            centerX = cellW * 5 + offsetX;
            centerY = cellH * 2 + offsetY;
        }else if (myData.index >= MAX_POINT_SIZE) {
            return;
        }
        String text="NO."+myData.index;
        //外圈空心圆半径=内圈实心圆半径+StrokeWidth画笔边框/2
        float strokeRadius=radius+mCircleStrokePaint.getStrokeWidth()/2f;
        //直接展示
//        canvas.drawCircle(centerX, centerY, radius, mCirclePaint);
//        canvas.drawCircle(centerX, centerY, strokeRadius, mCircleStrokePaint);
//        canvas.drawText(text,centerX,centerY+getPaintHeight(mPaintText,text)/2,mPaintText);

        mRegionList.add(new Region((int) (centerX - radius), (int) (centerY - radius), (int) (centerX + radius), (int) (centerY + radius)));
        //动画展示
        canvas.drawCircle(centerX, centerY, radius*mProgress, mCirclePaint);
        canvas.drawCircle(centerX, centerY, strokeRadius*mProgress, mCircleStrokePaint);
        if(onAnimationEnd){
            canvas.drawText(text,centerX,centerY+getPaintHeight(mPaintText,text)/2,mPaintText);
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        startAnim();
    }

    private boolean onAnimationEnd;
    /**
     * 启动进度动画
     */
    private void startAnim(){
        mClickIndex=-1;
        onAnimationEnd=false;
        //启动变动动画
        ValueAnimator angleAnim = ValueAnimator.ofFloat(0,1);
        angleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        angleAnim.setDuration(800);
        angleAnim.addUpdateListener(valueAnimator -> {
            //设置当进度
            mProgress= (float) valueAnimator.getAnimatedValue();
            if(mProgress<10){
                postInvalidate();
            }
            Log.w("drawCircle","mProgressRadius="+mProgress);
        });
        angleAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                onAnimationEnd=true;
                postInvalidate();
            }
        });
        angleAnim.start();

    }

    /**
     * 画 宽高 6x4 的格子
     *
     * @param canvas
     * @param drawerWidth
     * @param drawerHeight
     */
    private void drawBorder(Canvas canvas, int drawerWidth, int drawerHeight) {

        mTempRect.set(0, 0, drawerWidth, drawerHeight);
        canvas.drawRect(mTempRect, mBorderPaint);
        //画网格虚线
        mBorderPaint.setPathEffect(LINE_EFFECT);
        float offsetY = (float) drawerHeight / Y_NUM;
        float startY = offsetY;
        //画3条横线
        for (int i = 1; i < Y_NUM; i++) {
            canvas.drawLine(0, startY, drawerWidth, startY, mBorderPaint);
            startY += offsetY;
        }

        float offsetX = (float) drawerWidth / (X_NUM);
        float startX = offsetX;
        //画5条竖线
        for (int i = 1; i < X_NUM; i++) {
            canvas.drawLine(startX, 0, startX, drawerHeight, mBorderPaint);
            startX += offsetX;
        }
//        paint.setPathEffect(null);
//        paint.setStyle(Paint.Style.FILL);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < mRegionList.size(); i++) {
                    Region region = mRegionList.get(i);
                    if (region.contains((int) x, (int) y)) {
                        mClickIndex = i;
                        invalidate();
                        mItemClickListener.onBubbleClickListener(mDataList.get(i));
                        break;
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public interface ItemClickListener {
        void onBubbleClickListener(MyData data);
    }

    /**
     * sp2px
     */
    private  int sp2px(float spValue) {
        float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 测量画笔高度
     */
    private float getPaintHeight(Paint paint,String text){
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.height();
    }

    /**
     * 测量画笔宽度
     */
    private float getPaintWidth(Paint paint,String text){
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    protected int dp2px(float dpValue) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    public static class MyData{
        public MyData(int index, double ratio) {
            this.index = index;
            this.ratio = ratio;
        }

        public int index;
        public double ratio;
        public boolean isSelect;
    }
}

