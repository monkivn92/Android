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


class MainActivity : AppCompatActivity()
{
    private val GRANTED: Int = 1
    private val DENIED: Int = 2
    private val BLOCKED_OR_NEVER_ASKED: Int = 3
    private val APP_PERMISSIONS_REQUEST: Int = 44

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        if(isPermissionIsGranted(Manifest.permission.READ_EXTERNAL_STORAGE, this) != GRANTED)
        {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE.toString()))
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
                val intent = Intent(this@MainActivity, FilesActivity::class.java)
                startActivityForResult(intent, 123)
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
            Log.e("Log from main activity", "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
        }
    }

}
