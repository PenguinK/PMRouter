package com.penguinmore.kotlinmodule

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.penguinmore.pmannotation.Route

@Route(path = "Kotlin")
class KotlinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)
    }
}
