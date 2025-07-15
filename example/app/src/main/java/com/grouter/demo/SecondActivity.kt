package com.grouter.demo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.grouter.GRouter
import com.grouter.RouterActivity
import com.grouter.RouterField

/**
 * Created by wenjie on 2025/07/15.
 */
@RouterActivity("second")
class SecondActivity : AppCompatActivity() {

    @RouterField("id")
    private var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        GRouter.inject(this)

        Log.e("asd" , id.toString())
    }
}