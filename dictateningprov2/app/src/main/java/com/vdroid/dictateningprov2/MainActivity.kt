package com.vdroid.dictateningprov2

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*

import android.os.Build

import java.util.*

import android.app.Activity
import android.content.pm.PackageManager

import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Environment
import android.support.v4.content.LocalBroadcastManager
import android.view.KeyEvent
import android.view.View
import android.widget.*

import com.vdroid.dictateningprov2.utils.MPManager
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.file.Path
import java.nio.file.Paths


class MainActivity : AppCompatActivity()
{
    private val GRANTED: Int = 1
    private val DENIED: Int = 2
    private val BLOCKED_OR_NEVER_ASKED: Int = 3
    private val APP_PERMISSIONS_REQUEST: Int = 44
    private lateinit var mLBReceiver: BroadcastReceiver
    private lateinit var mLBReceiverSave: BroadcastReceiver
    var filePath : String = ""
    var filePathSave : String = ""

    lateinit var mSeekBar : SeekBar
    lateinit var mPlayingTime : TextView
    lateinit var mDuration : TextView
    lateinit var mPlayBtn : ImageButton
    lateinit var mSkipFW : ImageButton
    lateinit var mSkipBW : ImageButton

    lateinit var mpm : MPManager

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mSeekBar= seekbar
        mPlayingTime = playing_time_txt
        mDuration = duration_txt
        mPlayBtn = play_btn
        mSkipFW = skip_fw_btn
        mSkipBW = skip_bw_btn

        //Request permission
        if(isPermissionIsGranted(Manifest.permission.READ_EXTERNAL_STORAGE, this) != GRANTED)
        {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE.toString()))
        }

        //Publish and register broadcast receiver
        val filter = IntentFilter()
        filter.addAction("Path Broadcast")
        mLBReceiver = object : BroadcastReceiver()
        {
            override fun onReceive(context: Context, intent: Intent)
            {
                if (intent.action == "Path Broadcast")
                {
                    filePath = intent.getStringExtra("result_path")
                    mpm.reset()
                    Log.e("LBPath", filePath)
                }
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mLBReceiver, filter)


        //Listen key event when typing input
        input_txt.setOnKeyListener { _, keyCode, event ->
            if ( keyCode == KeyEvent.KEYCODE_ENTER)
            {
                //Toast.makeText(this, input_txt.text, Toast.LENGTH_SHORT).show()
                if(input_txt.text.isNotBlank())
                {
                    text_editor.append("${input_txt.text}\n")
                }
                input_txt.setText("")
                true
            }
            else false

        }

        //Music player
        mpm  = MPManager(this)

        play_btn.setOnClickListener{v ->
            mpm.play()

        }

        skip_fw_btn.setOnClickListener{v ->
            mpm.skipFW()
        }

        skip_bw_btn.setOnClickListener{v ->
            mpm.skipBW()
        }

        mSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
            {
                if(fromUser && mpm.getDuration() != 0)
                {
                    mpm.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?)
            {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?)
            {
            }

        })

    }

    override fun onResume()
    {
        super.onResume()
        //hideNavigationBar()
    }

    override fun onStop()
    {
        super.onStop()
        mpm.pause()
    }

    override fun onDestroy()
    {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLBReceiver)

    }

    fun isPermissionIsGranted(permission: String, activity : Activity): Int
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            return GRANTED
        }

        if(ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED)
        {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
            {
                return BLOCKED_OR_NEVER_ASKED
            }
            return DENIED
        }
        else
        {
            return GRANTED
        }
    }

    fun requestPermissions(permission: Array<String>) : Unit
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            setResult(Activity.RESULT_OK)
            finish()
        }

        var ungrantedPermCount : Int  = 0
        var permissionsToBeAsked : ArrayList<String> = ArrayList()

        for(p in permission)
        {
            if(isPermissionIsGranted(p, this) != GRANTED)
            {
                ungrantedPermCount++
                permissionsToBeAsked.add(p)
            }
        }

        if(ungrantedPermCount == 0)
        {
            setResult(Activity.RESULT_OK)
            finish()
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    permissionsToBeAsked.toArray(
                            Array<String>(permissionsToBeAsked.size, {it->it.toString()})
                    ),
                    APP_PERMISSIONS_REQUEST
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode:Int, permissions : Array<String>, grantResults : IntArray)
    {
        when(requestCode)
        {
            APP_PERMISSIONS_REQUEST -> {
                if (grantResults.size > 0)
                {
                    var isAllPermissionsGranted = true

                    for (i in 0 until grantResults.size)
                    {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            isAllPermissionsGranted = false
                            break
                        }
                    }

                    if (isAllPermissionsGranted)
                    {
                        //Log.e("grant", "Grant permission sucessfully")
                        Toast.makeText(this, "Grant permission sucessfully", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        //Log.e("grant", "Without permission, this app may be not working properly")
                        Toast.makeText(this, "Without permission, this app may be not working properly", Toast.LENGTH_SHORT).show()
                    }

                }
                else
                {
                    //Log.e("grant", "Without permission, this app may be not working properly")
                    Toast.makeText(this, "Without permission, this app may be not working properly", Toast.LENGTH_SHORT).show()
                }
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
            R.id.select_file -> {
                val intent = Intent(this@MainActivity, StorageActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_exit -> {
                finish()
                return true
            }
            R.id.save_text -> {
                val intent = Intent(this@MainActivity, SaveFileActivity::class.java)

                intent.putExtra("editor_content", text_editor.text.toString())
                intent.putExtra("current_playing_time", mpm.getCurrentPlayingTime())
                intent.putExtra("current_playing_file", mpm.getCurrentPlayingFile())
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 123)
        {
            Log.e("Log from main activity", "mainnnnn ${data?.getStringExtra("result_path")}")
        }
    }


}
