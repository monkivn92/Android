package com.vdroid.youtubelayout

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val items : Array<String> = arrayOf("aaaaa","bbbbbb","cccccc")
        val arrAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items)

        listView.adapter = arrAdapter


    }
}
