package com.example.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.RectF;

import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.dashboard.R;

/**
 * @author yzh created at 2022/9/17
 */
public class MyDashboardView extends BaseDashboardView {

    //外环画笔
    private Paint mPaintOuterArc;
    //内环画笔
    private Paint mPaintInnerArc;
    //进度点画笔
    private Paint mPaintProgressPoint;
    private Paint mPaintProgressPoint2;
    private Paint mPaintProgressPoint3;
    //指示器画笔
    private Paint mPaintIndicator;
    //外环区域
    private RectF mRectOuterArc;
    //内环区域
    private RectF mRectInnerArc;
    //圆环画笔颜色
    private int mOuterArcColor;
    private int mProgressOuterArcColor;
    //内环画笔颜色
    private int mInnerArcColor;
    private int mProgressInnerArcColor;
    //内外环之间的间距
    private float mArcSpacing;
    //进度条的圆点属性
    private float[] mProgressPointPosition;
    private float mProgressPointRadius;
    //指标器的Path
    private Path mIndicatorPath;
    //指示器的起始位置
    private float mIndicatorStart;

    //默认圆环之间间距
    private static final float DEFAULT_ARC_SPACING = 30;
    //外环的默认属性
    private static final float DEFAULT_OUTER_ARC_WIDTH = 16f;
    private static final int DEFAULT_OUTER_ARC_COLOR = Color.argb(80, 255, 255, 255);
    //外环进度的默认属性
    private static final int DEFAULT_PROGRESS_OUTER_ARC_COLOR = Color.argb(200, 255, 255, 255);
    //进度点的默认属性
    private static final float DEFAULT_PROGRESS_POINT_RADIUS = 5;
    private static final int DEFAULT_PROGRESS_POINT_COLOR = Color.WHITE;
    //内环默认属性
    private static final float DEFAULT_INNER_ARC_WIDTH = 1.5f;
    private static final int DEFAULT_INNER_ARC_COLOR = Color.argb(50, 255, 255, 255);
    //内环进度的默认属性
    private static final int DEFAULT_PROGRESS_INNER_ARC_COLOR = Color.argb(170, 255, 255, 255);
    //指示器默认颜色
    private static final int DEFAULT_INDICATOR_COLOR = Color.parseColor("#FF0B00");

    //刻度文字画笔
    protected Paint mPaintCalibrationText;

    //刻度的文本位置
    private float mCalibrationTextStart;

    public MyDashboardView(Context context) {
        this(context, null);
    }

    public MyDashboardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyDashboardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化界面
     */
    @Override
    protected void initView() {
        //默认数据
        mArcSpacing = dp2px(DEFAULT_ARC_SPACING);
        mOuterArcColor = DEFAULT_OUTER_ARC_COLOR;
        mProgressOuterArcColor = DEFAULT_PROGRESS_OUTER_ARC_COLOR;
        mProgressPointRadius = dp2px(DEFAULT_PROGRESS_POINT_RADIUS);
        mInnerArcColor = Color.parseColor("#dcdcdc");
        mProgressInnerArcColor = ContextCompat.getColor(getContext(), R.color.dashboard_gary);

        //初始化画笔
        //外环画笔
        mPaintOuterArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintOuterArc.setStrokeWidth(dp2px(DEFAULT_OUTER_ARC_WIDTH));
        mPaintOuterArc.setStyle(Paint.Style.STROKE);
        mPaintOuterArc.setStrokeCap(Paint.Cap.ROUND);
        mPaintOuterArc.setAntiAlias(true);
        mPaintOuterArc.setDither(true);

        //内环画笔
        mPaintInnerArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintInnerArc.setStrokeWidth(dp2px(4));
        mPaintInnerArc.setStyle(Paint.Style.STROKE);
        mPaintInnerArc.setStrokeCap(Paint.Cap.BUTT);
        PathEffect mPathEffect = new DashPathEffect(new float[] {3, 10 }, 0);
        mPaintInnerArc.setPathEffect(mPathEffect);

        //进度点画笔
        mPaintProgressPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProgressPoint.setStyle(Paint.Style.FILL);
        mPaintProgressPoint.setColor(DEFAULT_INDICATOR_COLOR);

        mPaintProgressPoint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProgressPoint2.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintProgressPoint2.setColor(DEFAULT_PROGRESS_POINT_COLOR);

        mPaintProgressPoint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProgressPoint3.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintProgressPoint3.setColor(ContextCompat.getColor(getContext(),R.color.dashboard_gary_stock));
        //指示器画笔
        mPaintIndicator = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintIndicator.setStrokeCap(Paint.Cap.SQUARE);
        mPaintIndicator.setColor(DEFAULT_INDICATOR_COLOR);
        mPaintIndicator.setStrokeWidth(dp2px(1));

        //进度点的图片
        mProgressPointPosition = new float[2];

        //刻度文字画笔
        mPaintCalibrationText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCalibrationText.setTextAlign(Paint.Align.CENTER);
        mPaintCalibrationText.setTextSize(sp2px(12f));

        mPaintCalibrationText.setColor(colors[0]);
        mPaintValue.setColor(ContextCompat.getColor(getContext(), R.color.dashboard_red));
        mPaintValueSmall.setColor(ContextCompat.getColor(getContext(), R.color.dashboard_red));
        mPaintDate.setTextSize(sp2px(15f));
        mPaintDate.setColor(ContextCompat.getColor(getContext(), R.color.dashboard_gary));

    }

    /**
     * 初始化圆环区域
     */
    @Override
    protected void initArcRect(float left, float top, float right, float bottom) {
        //外环区域
        mRectOuterArc = new RectF(left, top, right, bottom);

        //刻度文字位置
        mCalibrationTextStart =  dp2px(10);

        initInnerRect();
    }

    /**
     * 初始化内部的区域
     */
    private void initInnerRect() {
        //内环位置
        mRectInnerArc = new RectF(mRectOuterArc.left + mArcSpacing,mRectOuterArc.top + mArcSpacing,
                mRectOuterArc.right - mArcSpacing , mRectOuterArc.bottom - mArcSpacing);

        //指标器的路径
        mIndicatorStart = mRectInnerArc.top - mArcSpacing / 2;
        mIndicatorPath = new Path();
        mIndicatorPath.moveTo(mRadius, mIndicatorStart);

        //rLineTo 相当于于上一个点的坐标为原点，进行偏移
        mIndicatorPath.rLineTo(-dp2px(3), dp2px(7));
        mIndicatorPath.rLineTo(dp2px(6), 0);
        mIndicatorPath.close();
    }

    /**
     * 绘制圆环
     */
    @Override
    protected void drawArc(Canvas canvas, float arcStartAngle, float arcSweepAngle) {
        //绘制刻度数字
        drawCalibration(canvas, arcStartAngle);
        //绘制圆环
        mPaintOuterArc.setColor(mOuterArcColor);
        canvas.drawArc(mRectOuterArc, arcStartAngle, arcSweepAngle, false, mPaintOuterArc);

        //绘制内环
        mPaintInnerArc.setColor(mInnerArcColor);
        canvas.drawArc(mRectInnerArc, arcStartAngle, arcSweepAngle, false, mPaintInnerArc);
    }

    /**
     * 绘制刻度
     */
    private void drawCalibration(Canvas canvas, float arcStartAngle) {
        if(mLargeCalibrationNumber == 0){
            return;
        }
        canvas.save();
        //旋转画布
        canvas.rotate(arcStartAngle - 270, mRadius, mRadius);
        //遍历数量
        for (int i = 0; i < mCalibrationTotalNumber; i++) {
            if(mCalibrationNumberText != null && mCalibrationNumberText.length > i){
                if(i==1){
                    mPaintCalibrationText.setColor(colors[0]);
                }else if(i==2){
                    mPaintCalibrationText.setColor(colors[1]);
                }else if(i==3){
                    mPaintCalibrationText.setColor(colors[2]);
                }else{
                   // mPaintCalibrationText.setColor(colors[3]);
                }
                //第一个与最后一个不画刻度
                if(i!=0&&i!=mCalibrationTotalNumber-1){
                    //环绕圆弧画文字
                    canvas.drawText(String.valueOf(mCalibrationNumberText[i]), mRadius, mCalibrationTextStart, mPaintCalibrationText);
                }
            }
            //旋转
            canvas.rotate(mSmallCalibrationBetweenAngle, mRadius, mRadius);
        }
        canvas.restore();
    }

    /**
     * 绘制进度圆环
     */
    @Override
    protected void drawProgressArc(Canvas canvas, float arcStartAngle, float progressSweepAngle) {
        //绘制进度点
        if(progressSweepAngle == 0) {
            return;
        }
        Path path = new Path();
        //添加进度圆环的区域
        path.addArc(mRectOuterArc, arcStartAngle, progressSweepAngle);
        //计算切线值和为重
        PathMeasure pathMeasure = new PathMeasure(path, false);
        pathMeasure.getPosTan(pathMeasure.getLength(), mProgressPointPosition, null);
        //绘制圆环
//        mPaintOuterArc.setColor(mProgressOuterArcColor);
//        canvas.drawPath(path, mPaintOuterArc);
        //绘制进度点
        if(mProgressPointPosition[0] != 0 && mProgressPointPosition[1] != 0) {
            //描边
            canvas.drawCircle(mProgressPointPosition[0], mProgressPointPosition[1], mProgressPointRadius+dp2px(6), mPaintProgressPoint3);
            //大圆点
            canvas.drawCircle(mProgressPointPosition[0], mProgressPointPosition[1], mProgressPointRadius+dp2px(5), mPaintProgressPoint2);
            //小圆点
            canvas.drawCircle(mProgressPointPosition[0], mProgressPointPosition[1], mProgressPointRadius, mPaintProgressPoint);
        }

        //绘制内环
        mPaintInnerArc.setColor(mProgressInnerArcColor);
        canvas.drawArc(mRectInnerArc, arcStartAngle, progressSweepAngle, false, mPaintInnerArc);

        //绘制指针
        canvas.save();
        canvas.rotate(arcStartAngle + progressSweepAngle - 270, mRadius, mRadius);
        mPaintIndicator.setStyle(Paint.Style.FILL);
        canvas.drawPath(mIndicatorPath, mPaintIndicator);
//        mPaintIndicator.setStyle(Paint.Style.STROKE);
//        canvas.drawCircle(mRadius, mIndicatorStart + dp2px(6) + 1, dp2px(2), mPaintIndicator);
        canvas.restore();
    }



    /**
     * 绘制4个刻度圆环
     */
    @Override
    protected void drawKDArc(Canvas canvas, float arcStartAngle, float arcSweepAngle) {
        if(arcSweepAngle == 0) {
            return;
        }
        canvas.save();
        int cNum=colors.length;//刻度数量
        float itemStartAngle=arcStartAngle;
        int placeHolderAngle=3;//白色占位圆弧度数
        float itemAngle=arcSweepAngle/cNum;//每个圆弧占1/4

        float[] startAngles =new float[cNum];//每个刻度开始角度
        for (int i = 0; i <cNum ; i++) {
            startAngles[i]=itemStartAngle;
            Log.w("tag","itemStartAngle:"+itemStartAngle +"  itemAngle:"+itemAngle);
            itemStartAngle+=itemAngle;
        }
        //倒序画刻度
        for (int i = startAngles.length-1; i >=0 ; i--) {
            Path path = new Path();
            //画白底圆弧
            path.addArc(mRectOuterArc, startAngles[i], itemAngle);//添加进度圆环的区域
            if(i!=startAngles.length-1) {
                setPaintColor(false,i,Color.WHITE);
            }else{
                setPaintColor(false,i,colors[i]);//最后一个刻度不需要白底圆弧，直接改为对应刻度颜色
            }
            canvas.drawPath(path, mPaintOuterArc);//绘制圆环

            //画有颜色刻度圆弧
            Path path2 = new Path();
            path2.addArc(mRectOuterArc, startAngles[i], itemAngle-placeHolderAngle);
            setPaintColor(true,i,colors[i],colorsEnd[i]);
            canvas.drawPath(path2, mPaintOuterArc);
           // canvas.drawArc(mRectOuterArc,startAngles[i],itemAngle-placeHolderAngle,false,mPaintOuterArc);
        }

//        Path path3 = new Path();
//        path3.addArc(mRectOuterArc, arcStartAngle, arcSweepAngle);
//        setPaintColor(true,0,colors[0],colorsEnd[0]);
//        canvas.drawPath(path3, mPaintOuterArc);

        canvas.restore();
    }

    /**
     * 设置外环画笔颜色
     * @param color
     * @param flag
     */
    private void setPaintColor(boolean flag,int i,int... color) {
        if(color==null)return;
//        if(flag && color.length > 1){
//            //最外层圆环渐变画笔设置
//           // mOuterGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
////            color[0]=Color.GREEN;
////            color[1]=Color.RED;
//
////            int cNum=colors.length;//刻度数量
//            float y=(mRectOuterArc.bottom-mRectOuterArc.top)/2;
////            float tempW=(mRectOuterArc.right-mRectOuterArc.left)/cNum;
////            //设置圆环渐变色渲染
////            SweepGradient shader = new SweepGradient(x, y, colors, null);
////            float rotate = 170f;
////            Matrix gradientMatrix = new Matrix();
////            gradientMatrix.preRotate(rotate, x/2,y/2);
////            shader.setLocalMatrix(gradientMatrix);
////            mPaintOuterArc.setShader(shader);
//
//
//            //以圆矩形的左边与右边坐标，为起止
//            float endX=mRectOuterArc.right;
////            float endX=mRectOuterArc.left+tempW*i;
//            Log.v("tag","mRectOuterArc.left:"+mRectOuterArc.left +"  endX:"+endX);
//            Shader shader =new LinearGradient(mRectOuterArc.left,y,endX,y,color,null,Shader.TileMode.CLAMP);
//            mPaintOuterArc.setShader(shader);
//
//        }else{
            mPaintOuterArc.setShader(null);
            mPaintOuterArc.setColor(color[0]);
//        }
    }

    /**
     * 绘制文字
     */
    @Override
    protected void drawText(Canvas canvas, int value, String valueLevel, String currentTime) {
        //绘制数值
        float marginTop = mRadius-dp2px(5);
        String valueStr=String.valueOf(value);
        canvas.drawText(valueStr, mRadius, marginTop, mPaintValue);

        float valueWidth=getPaintWidth(mPaintValue, valueStr);
        mPaintValueSmall.setTextSize(sp2px(25));
        canvas.drawText("%", mRadius+valueWidth/2+dp2px(15), marginTop, mPaintValueSmall);

//        canvas.save();
//        SpannableString sp=new SpannableString(value+"%");
//        //设置字体大小（相对值,单位：像素） 参数表示为默认字体大小的多少倍   ,0.5表示一半
//        sp.setSpan(new RelativeSizeSpan(0.5f), sp.length()-1, sp.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//
//        StaticLayout staticLayout = new StaticLayout(sp, mPaintValueSmall, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);
//        canvas.translate(mRadius,marginTop/2);
//        staticLayout.draw(canvas);
//        canvas.restore();

        //绘制数值文字信息
//        if(!TextUtils.isEmpty(valueLevel)){
//            float margin = mRadius - mTextSpacing - getPaintHeight(mPaintValue, "9");
//            canvas.drawText(valueLevel, mRadius, margin, mPaintValueLevel);
//        }

        //绘制底部文字
        String str="每日进度";
        marginTop = marginTop + getPaintHeight(mPaintDate, str) + mTextSpacing;
        canvas.drawText(str, mRadius, marginTop, mPaintDate);
        //0 ，100的开始起止数字
        if(mCalibrationNumberText!=null&&mCalibrationNumberText.length>=2){
            mPaintCalibrationText.setColor(colors[0]);
            marginTop+=  mTextSpacing*2;
            canvas.drawText(String.valueOf(mCalibrationNumberText[0]), mRectOuterArc.left, marginTop, mPaintCalibrationText);

            mPaintCalibrationText.setColor(ContextCompat.getColor(getContext(), R.color.dashboard_red));
            canvas.drawText(String.valueOf(mCalibrationNumberText[mCalibrationNumberText.length-1])
                    ,mRectOuterArc.right, marginTop, mPaintCalibrationText);

        }
    }

    /**
     * 设置圆环的距离
     */
    public void setArcSpacing(float dpSize){
        mArcSpacing = dp2px(dpSize);

        initInnerRect();

        postInvalidate();
    }

    /**
     * 设置外环颜色
     */
    public void setOuterArcPaint(float dpSize, @ColorInt int color){
        mPaintOuterArc.setStrokeWidth(dp2px(dpSize));
        mOuterArcColor = color;

        postInvalidate();
    }

    /**
     * 设置进度条的颜色
     */
    public void setProgressOuterArcColor(@ColorInt int color){
        mProgressOuterArcColor = color;

        postInvalidate();
    }

    /**
     * 设置内环的属性
     */
    public void setInnerArcPaint(float dpSize, @ColorInt int color){
        mPaintInnerArc.setStrokeWidth(dp2px(dpSize));
        mInnerArcColor = color;

        postInvalidate();
    }

    /**
     * 设置内环的属性
     */
    public void setProgressInnerArcPaint(@ColorInt int color){
        mProgressInnerArcColor = color;

        postInvalidate();
    }

    /**
     * 设置内环实线和虚线状态
     */
    public void setInnerArcPathEffect(float[] intervals){
        PathEffect mPathEffect = new DashPathEffect(intervals, 0);
        mPaintInnerArc.setPathEffect(mPathEffect);

        postInvalidate();
    }

    /**
     * 设置进度圆点的属性
     */
    public void setProgressPointPaint(float dpRadiusSize,@ColorInt int color){
        mProgressPointRadius = dp2px(dpRadiusSize);
        mPaintProgressPoint.setColor(color);

        postInvalidate();
    }

    /**
     * 设置指示器属性
     */
    public void setIndicatorPaint(@ColorInt int color){
        mPaintIndicator.setColor(color);

        postInvalidate();
    }
}
