package com.vdroid.daggertwopart2

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.vdroid.daggertwopart2.DTApp
import com.vdroid.daggertwopart2.NetworkUtils
import com.vdroid.daggertwopart2.RxUtils
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject lateinit var rxUtils: RxUtils
    @Inject lateinit var networkUtils: NetworkUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DTApp.getComponent().inject(this)

    }
}
