package com.example

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dashboard.R
import com.example.view.MarketHotPointView
import com.example.view.MarketHotPointView.MyData
import java.util.*

class HotPointActivity : AppCompatActivity(), MarketHotPointView.ItemClickListener {
    private var tv: TextView? = null
    private val dataList: MutableList<MyData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotpoint)
        tv = findViewById(R.id.tv)
        val view: MarketHotPointView = findViewById(R.id.mMarketHotPointView)
        for (i in 0 until MarketHotPointView.MAX_POINT_SIZE) {

            val ratio = Random().nextInt(20)
            Log.i("HotPointActivity", "i=${i} ratio=${ratio}")
            val myData = MyData(i, ratio.toDouble())
            dataList.add(myData)
        }
        view.setDataList(dataList)
        view.setItemClickListener(this)
    }

    override fun onBubbleClickListener(data: MyData?) {
        val str: String = "你点击了NO.${data?.index} ratio=${data?.ratio}"
        Toast.makeText(this@HotPointActivity, str, Toast.LENGTH_SHORT).show()
        tv?.text = str
    }


}