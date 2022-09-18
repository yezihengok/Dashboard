package com.example.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() , View.OnClickListener{

    private var etValue: EditText? =null
    private var dashboardView: MyDashboardView? = null
    private var cbAnim: CheckBox? = null
    private  var cbReset:CheckBox? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        dashboardView = findViewById(R.id.dv)
        etValue = findViewById(R.id.et_value)
        cbAnim = findViewById(R.id.cb_anim)
        cbReset = findViewById(R.id.cb_reset)

        val btn_random: Button =findViewById(R.id.btn_random)
        val btn_set_value: Button = findViewById(R.id.btn_set_value)
        btn_random.setOnClickListener(this)
        btn_set_value.setOnClickListener(this)

        //开始比 9点钟方向少了5° (180-5) ，所以结束就要 (180°+5*2=190)
        dashboardView?.setArcAngle(175f, 190f)

        //设置刻度
        val arr = intArrayOf(0, 25, 50, 75, 100)
        val strs = arrayOf("暂无","较差", "中等", "良好", "优秀")
        dashboardView?.setCalibration(arr, strs,0)
        btn_random.performClick()

    }

    override fun onClick(v: View) {
        val anim = cbAnim?.isChecked
        val reset = cbReset?.isChecked
        when (v.id) {
            R.id.btn_random -> {
                dashboardView?.apply {
                    val max = this.max
                    val min = this.min
                    val ramDomNum=Random().nextInt(max - min)+min
                    this.setValue(ramDomNum,anim!!,reset!!)
                }
            }
            R.id.btn_set_value -> if (etValue!!.length() > 0) {
                dashboardView?.setValue(etValue?.text.toString().toInt(), anim!!, reset!!)
            }
        }
    }
}