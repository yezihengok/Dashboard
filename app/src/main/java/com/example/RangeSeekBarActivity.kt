package com.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dashboard.R
import com.example.view.LinePathView
import com.example.view.XRangeSlider
import java.util.*

class RangeSeekBarActivity : AppCompatActivity() {
    private var tv: TextView? = null
    private var mXRangeSlider: XRangeSlider? =null
    private var mLinePathView: LinePathView?=null
    private var mSideMinIndex = 0
    private var mSideMaxIndex = 0
    private val dataList: MutableList<LinePathView.MyData> = ArrayList()

    private val SIZE=100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rangeseekbar)
        tv = findViewById(R.id.tv)
        mXRangeSlider = findViewById(R.id.mXRangeSlider)
        mLinePathView = findViewById(R.id.mLinePathView)


        for (i in 0 until SIZE) {

            var price = i+Random().nextInt(5)
            price =Math.max(30+i+Random().nextInt(10),price)
            price =Math.min(100,price)
            if(price==100){
                price= (dataList[i-1].price- Random().nextInt(5)).toInt()
            }
            Log.i("RangeSeekBarActivity", "i=${i} price=${price}")
            val myData = LinePathView.MyData(i, price.toDouble())

            dataList.add(myData)
        }

        //添加模拟数据
        mLinePathView?.setDataList(dataList)


        mXRangeSlider?.setOnRangeSliderListener(object : XRangeSlider.OnRangeSliderListener{
            @SuppressLint("SetTextI18n")
            override fun onMaxChanged(slider: XRangeSlider?, maxValue: Int) {
                mSideMaxIndex=maxValue
                mLinePathView?.setSideMinMax(mSideMinIndex,mSideMaxIndex)
                tv?.text="价格区间：¥${mSideMinIndex} - ¥${mSideMaxIndex}"
            }

            @SuppressLint("SetTextI18n")
            override fun onMinChanged(slider: XRangeSlider?, minValue: Int) {
                mSideMinIndex=minValue
                mLinePathView?.setSideMinMax(mSideMinIndex,mSideMaxIndex)
                tv?.text="价格区间：¥${mSideMinIndex} - ¥${mSideMaxIndex}"
            }

        })

    }



}