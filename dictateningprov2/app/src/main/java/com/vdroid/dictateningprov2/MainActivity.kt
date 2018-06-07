package com.vdroid.dictateningprov2

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import android.os.Environment.getExternalStorageDirectory
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import android.text.TextUtils
import java.util.*
import android.os.Build.VERSION.SDK_INT
import android.R.attr.order
import android.app.Activity
import android.content.pm.PackageManager
import android.support.annotation.DrawableRes
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.view.View


class MainActivity : AppCompatActivity()
{
    private val GRANTED: Int = 1
    private val DENIED: Int = 2
    private val BLOCKED_OR_NEVER_ASKED: Int = 3
    private val APP_PERMISSIONS_REQUEST: Int = 44
    private lateinit var mLBReceiver: BroadcastReceiver
    private var filePath : String = ""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        if(isPermissionIsGranted(Manifest.permission.READ_EXTERNAL_STORAGE, this) != GRANTED)
        {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE.toString()))
        }

        val filter = IntentFilter()
        filter.addAction("Path Broadcast")
        mLBReceiver = object : BroadcastReceiver()
        {
            override fun onReceive(context: Context, intent: Intent)
            {
                if (intent.action == "Path Broadcast")
                {
                    filePath = intent.getStringExtra("result_path")
                    Log.e("LBPath", filePath)
                }
            }
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mLBReceiver, filter)

    }

    override fun onResume()
    {
        super.onResume()
        hideNavigationBar()
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
            R.id.action_settings -> {
                val intent = Intent(this@MainActivity, StorageActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_exit -> {
                finish()
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

    fun hideNavigationBar() : Unit
    {
        val decorView : View = window.decorView
        decorView.setOnSystemUiVisibilityChangeListener {visibility ->
            Log.i("LOG visibility","Menu Shown is this $visibility")
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean)
    {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus)
        {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }

}
