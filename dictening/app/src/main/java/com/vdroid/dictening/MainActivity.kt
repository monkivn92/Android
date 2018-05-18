package com.vdroid.dictening

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.os.Bundle

import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.Reader
import java.nio.file.Path
import java.nio.file.Paths


class MainActivity : AppCompatActivity()
{
    val APP_PERMISSIONS_REQUEST : Int = 44
    val DENIED = 1
    val BLOCKED_OR_NEVER_ASKED = 2
    val GRANTED = 3
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        select_file.setOnClickListener{ view ->

            if(isPermissionIsGranted(Manifest.permission.READ_EXTERNAL_STORAGE, this) != GRANTED)
            {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE.toString()))
            }

            val external_path: String = Environment.getExternalStorageDirectory().absolutePath
            val external_path_nonremoveable = Environment.getDataDirectory().absolutePath

            Log.e("123", "$external_path \n $external_path_nonremoveable")

            val directory : File? =  File("$external_path_nonremoveable/dictening/")
            directory?.let{
                if(!it.exists())
                {
                    it.mkdir()
                    Log.e("123", it.absolutePath)
                }
            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //if the activity you are launching uses the singleTask launch mode,
            // it will not run in your task and thus you will immediately receive a cancel result.
            val myIntent : Intent?  = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName"))
            startActivityForResult(myIntent, 2048)
        }
        else
        {
            ShowChatHead()
        }

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
                        Log.e("grant", "grantOKKKKK")
                        //setResult(Activity.RESULT_OK)
                    }
                    else
                    {
                        Log.e("grant", "grantcancel")
                        //setResult(Activity.RESULT_CANCELED)
                    }

                }
                else
                {
                    Log.e("grant", "grantcancel")
                    //setResult(Activity.RESULT_CANCELED)
                }

               /* finish()
                return*/
            }
        }
    }

    fun ShowChatHead(): Unit
    {
        show_chathead.setOnClickListener { view ->

                startService(Intent(this@MainActivity, ChatHeadService::class.java))
                finish()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2048)
        {
            //Check if the permission is granted or not.
            Log.e("rok", Activity.RESULT_CANCELED.toString() )
            Log.e("rok", resultCode.toString() )

            if (resultCode == Activity.RESULT_OK)
            {
                ShowChatHead()
            }
            else
            {
                //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show()

                finish()
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
