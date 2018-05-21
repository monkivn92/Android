package com.vdroid.dictateningprov2

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
import android.support.annotation.DrawableRes






class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val list_storages : ArrayList<String> = getStorageDirectories()

        list_storages?.let{
            for(s in list_storages)
            {
                Log.e("Path", s)
            }
            getStarageName(list_storages)
        }

    }

    fun getStarageName(storageDirectories : ArrayList<String>)
    {
        for (file in storageDirectories)
        {
            val f = File(file)

            val name: String

            if ("/storage/emulated/legacy" == file || "/storage/emulated/0" == file || "/mnt/sdcard" == file)
            {
                name = "Storage"
            }
            else if ("/storage/sdcard1" == file)
            {
                name = "External Storage"
            }
             else
            {
                name = f.name
            }

            if (f.isDirectory() || f.canExecute())
            {
                Log.e("Name", name)
            }
        }
    }

    fun getStorageDirectories() : ArrayList<String>
    {
        // Final set of paths
        val rv = ArrayList<String>()

        // Primary physical SD-CARD (not emulated)
        val rawExternalStorage = System.getenv("EXTERNAL_STORAGE")

        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        val rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE")

        // Primary emulated SD-CARD
        val rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET")


        if(rawEmulatedStorageTarget.isNullOrEmpty())
        {
            // Device has physical external storage; use plain paths.
            if (rawExternalStorage.isNullOrEmpty())
            {
                // EXTERNAL_STORAGE undefined; falling back to default.
                rv.add("/storage/sdcard0")
            }
            else
            {
                rv.add(rawExternalStorage)
            }
        }
        else
        {
            // Device has emulated storage; external storage paths should have
            // userId burned into them.
            val rawUserId: String

            if (SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
            {
                rawUserId = ""
            }
            else
            {
                val path = Environment.getExternalStorageDirectory().absolutePath
                val folders = path.split("/")
                val lastFolder = folders[folders.size - 1]
                var isDigit = false
                try
                {
                    Integer.valueOf(lastFolder)
                    isDigit = true
                }
                catch (ignored: NumberFormatException)
                {
                    /////ignored
                }

                rawUserId = if (isDigit) lastFolder else ""
            }

            // /storage/emulated/0[1,2,...]
            if (rawUserId.isNullOrEmpty())
            {
                rv.add(rawEmulatedStorageTarget)
            }
            else
            {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId)
            }

        }

        // Add all secondary storages
        if (!rawSecondaryStoragesStr.isNullOrEmpty())
        {
            // All Secondary SD-CARDs splited into array
            val rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator)
            for(s in rawSecondaryStorages)
            {
                rv.add(s)
            }
        }

        /*DO this later
        if (SDK_INT >= Build.VERSION_CODES.M && checkStoragePermission())
            rv.clear();
         */

        if (SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            val strings = getExtSdCardPathsForActivity(this)

            for (s in strings)
            {
                val f = File(s)
                if (!rv.contains(s) && canListFiles(f))
                    rv.add(s)
            }
        }

        return rv
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
