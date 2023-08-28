package com

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.HotPointActivity
import com.example.RangeSeekBarActivity
import com.example.dashboard.R

class MainActivity : AppCompatActivity() , View.OnClickListener{

    private var mBtn1: Button? =null
    private var mBtn2: Button? =null
    private var mBtn3: Button? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBtn1 = findViewById(R.id.mBtn1)
        mBtn2 = findViewById(R.id.mBtn2)
        mBtn3 = findViewById(R.id.mBtn3)
        setOnClickListener(this@MainActivity,mBtn1,mBtn2,mBtn3)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.mBtn1 -> {
                startActivity(Intent(this, DashboardActivity::class.java))
            }
            R.id.mBtn2 -> {
                startActivity(Intent(this, HotPointActivity::class.java))
            }
            R.id.mBtn3 -> startActivity(Intent(this, RangeSeekBarActivity::class.java))
        }
    }

    private fun setOnClickListener(listener:View.OnClickListener, vararg args: View?){
            for(v in args){
                v?.setOnClickListener(listener)
            }
    }
}