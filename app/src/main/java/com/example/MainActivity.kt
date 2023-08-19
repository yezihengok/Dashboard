package com

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.HotPointActivity
import com.example.dashboard.R

class MainActivity : AppCompatActivity() , View.OnClickListener{

    private var mBtn1: Button? =null
    private var mBtn2: Button? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBtn1 = findViewById(R.id.mBtn1)
        mBtn2 = findViewById(R.id.mBtn2)

        setOnClickListener(this@MainActivity,mBtn1,mBtn2)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.mBtn1 -> {
                startActivity(Intent(this, DashboardActivity::class.java))
            }
            R.id.mBtn2 -> {
                startActivity(Intent(this, HotPointActivity::class.java))
            }
        }
    }

    private fun setOnClickListener(listener:View.OnClickListener, vararg args: View?){
            for(v in args){
                v?.setOnClickListener(listener)
            }
    }
}