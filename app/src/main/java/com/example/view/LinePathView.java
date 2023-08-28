package com.example.view;

import static com.example.util.Utils.dp2px;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.dashboard.R;
import com.example.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yzh on 2023/8/18 19:04.
 */
public class LinePathView extends View {
    private int mSideMinIndex = 0;
    private int mSideMaxIndex = 0;
    //把当前画布平分成 宽高 2x2 的格子
    private static final int X_NUM = 2;
    private static final int Y_NUM = 2;

    private static final PathEffect LINE_EFFECT = new DashPathEffect(new float[]{20, 10}, 0);//网格虚线
    private RectF mTempRect = new RectF();
    // 默认控件大小
    private final static int DEFAULT_W = 250;
    private final static int DEFAULT_H = 100;
    private int mLineColor;

    private int mBorderColor;
    private Paint mBorderPaint;
    private Paint mLinePaint;

    private List<MyData> mDataList = new ArrayList<>();
    public double mMaxValue;
    //public double mMinValue;

    public void setDataList(List<MyData> mDataList) {
        if (mDataList == null) return;
        this.mDataList = mDataList;
        mMaxValue = -Double.MAX_VALUE;
        for (MyData data : mDataList) {
            mMaxValue = Math.max(mMaxValue, data.price);
        }
        invalidate();
    }

    public void setSideMinMax(int sideMinIndex, int sideMaxIndex) {
        mSideMinIndex = sideMinIndex - 1;
        mSideMaxIndex = sideMaxIndex ;
        if (mDataList != null) {
            invalidate();
        }
    }

    public LinePathView(Context context) {
        this(context, null);
    }

    public LinePathView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinePathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mLineColor = ContextCompat.getColor(context, R.color.up_market_line_color);

        mBorderColor = ContextCompat.getColor(context, R.color.up_common_divider_strong_color);
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿
        mBorderPaint.setStrokeWidth(2);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);


        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(2);
        mLinePaint.setColor(mLineColor);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        mPadding = Math.max(Math.max(getPaddingLeft(), getPaddingTop()),
//                Math.max(getPaddingRight(), getPaddingBottom()));
//        mPadding = Math.max(dp2px(DEFAULT_PADDING), mPadding);

        setMeasuredDimension(measureSize(widthMeasureSpec, dp2px(DEFAULT_W)), measureSize(heightMeasureSpec, dp2px(DEFAULT_H)));
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


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int drawerWidth = getWidth();
        int drawerHeight = getHeight();
        drawBorder(canvas, drawerWidth, drawerHeight);
        drawNowPriceLine(canvas, drawerWidth, drawerHeight);
        drawSelectPath(canvas, drawerWidth, drawerHeight);
    }


    private void drawNowPriceLine(Canvas canvas, int drawerWidth, int drawerHeight) {
        final double unitHeight = getUnitHeight(drawerHeight);
        double itemWidth = getUnitWidth(drawerWidth);
        final Path path = new Path();
        final PointF point = new PointF();
        float startX = 0;
        float minY = Float.MAX_VALUE;

        //100条数据 x从0 开始画，画到最后一条数据的位置是99 距离右边框还差 一个 item 距离，。给每个itemWidth额外加上偏移量则刚好铺满表格宽度
        float offsetItem = (float) (itemWidth / mDataList.size());
        itemWidth += offsetItem;
        for (int i = 0, size = mDataList.size(); i < size; ++i) {
            final MyData data = mDataList.get(i);
            final float y = (float) ((mMaxValue - data.price) * unitHeight);

            minY = Math.min(minY, y);
            if (i == 0) {
                point.set(0, y);
            }
            mLinePaint.setColor(mLineColor);
            canvas.drawLine(point.x, point.y, startX, y, mLinePaint);
            point.set(startX, y);
            // 现价路径
            if (i == 0) {
                path.moveTo(startX, drawerHeight);
            }
            path.lineTo(startX, y);
            if (i == size - 1) {
                path.lineTo(startX, drawerHeight);
            }

            startX += itemWidth;
        }
        path.close();

        // 绘制现价路径（渐变式）
        mLinePaint.setShader(new LinearGradient(0, minY, 0, drawerHeight,
                ContextCompat.getColor(getContext(), R.color.up_market_start_color),
                ContextCompat.getColor(getContext(), R.color.up_market_end_color),
                Shader.TileMode.MIRROR));
        // mLinePaint.setColor(mFallColor70);
        canvas.drawPath(path, mLinePaint);
        mLinePaint.setShader(null);
    }

    private void drawSelectPath(Canvas canvas, int drawerWidth, int drawerHeight) {
        final double unitHeight = getUnitHeight(drawerHeight);
        double itemWidth = getUnitWidth(drawerWidth);
        final Path path = new Path();

        float minY = Float.MAX_VALUE;

        //100条数据 x从0 开始画，画到最后一条数据的位置是99 距离右边框还差 一个 item 距离，。给每个itemWidth额外加上偏移量则刚好铺满表格宽度
        float offsetItem = (float) (itemWidth / mDataList.size());
        itemWidth += offsetItem;
        float startX = (float) (mSideMinIndex * itemWidth);

        for (int i = mSideMinIndex, size = mSideMaxIndex; i < size; ++i) {
            final MyData data = mDataList.get(i);
            final float y = (float) ((mMaxValue - data.price) * unitHeight);
            minY = Math.min(minY, y);
            // 现价路径
            if (i == mSideMinIndex) {
                path.moveTo(startX, drawerHeight);
            }
            path.lineTo(startX, y);
            if (i == size - 1) {
                path.lineTo(startX, drawerHeight);
            }
            startX += itemWidth;
        }
        path.close();

        // 绘制现价路径（渐变式）
        mLinePaint.setShader(new LinearGradient(0, minY, 0, drawerHeight,
                ContextCompat.getColor(getContext(), R.color.up_market_start_selected_color),
                ContextCompat.getColor(getContext(), R.color.up_market_end_selected_color),
                Shader.TileMode.MIRROR));
        // mLinePaint.setColor(mFallColor70);
        canvas.drawPath(path, mLinePaint);
        mLinePaint.setShader(null);
    }

    public double getUnitHeight(int drawerHeight) {
        double valueSum = this.mMaxValue;
        return valueSum != 0.0 ? (double) drawerHeight / valueSum : 0.0;
    }

    public double getUnitWidth(int drawerWidth) {
        double valueSum = mDataList == null ? 0 : mDataList.size();
        return valueSum != 0.0 ? (double) drawerWidth / valueSum : 0.0;
    }


    /**
     * 画 宽高 6x4 的格子
     *
     * @param canvas
     * @param drawerWidth
     * @param drawerHeight
     */
    private void drawBorder(Canvas canvas, int drawerWidth, int drawerHeight) {
        mBorderPaint.setPathEffect(null);
        mTempRect.set(0, 0, drawerWidth, drawerHeight);
        canvas.drawRect(mTempRect, mBorderPaint);

        float offsetY = (float) drawerHeight / Y_NUM;
        float startY = offsetY;

        //网格虚线
        mBorderPaint.setPathEffect(LINE_EFFECT);
        Path path = new Path();
        //画3条横线
        for (int i = 1; i < Y_NUM; i++) {
            //canvas.drawLine(0, startY, drawerWidth, startY, mBorderPaint);
            path.moveTo(0, startY);
            path.lineTo(drawerWidth, startY);
            startY += offsetY;
        }
        canvas.drawPath(path, mBorderPaint);//改为用path 画虚线

        float offsetX = (float) drawerWidth / (X_NUM);
        float startX = offsetX;

        path.reset();
        //画5条竖线
        for (int i = 1; i < X_NUM; i++) {
            //canvas.drawLine(startX, 0, startX, drawerHeight, mBorderPaint);
            path.moveTo(startX, 0);
            path.lineTo(startX, drawerHeight);
            startX += offsetX;
        }
        canvas.drawPath(path, mBorderPaint);

    }


    public static class MyData {
        public MyData(int index, double price) {
            this.index = index;
            this.price = price;
        }

        public int index;
        public double price;

    }
}

