package com.vdroid.dictening

import android.annotation.TargetApi
import android.os.Bundle

import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.util.Log
import java.io.BufferedReader
import java.io.FileReader
import java.io.Reader
import java.nio.file.Path
import java.nio.file.Paths


class MainActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        select_file.setOnClickListener{ view ->
            //Toast.makeText(this, "aaaaaaaa", Toast.LENGTH_LONG).show()
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "*/*"
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            startActivity(Intent.createChooser(intent, "Select a File to Upload"))

            val buf_reader  = BufferedReader( FileReader("/proc/mounts") as Reader)

            val lines : List<String> = buf_reader.readLines()
            for(line in lines)
            {
                Log.e("123", line)
            }



        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId)
        {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
