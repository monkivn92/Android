package com.vdroid.dictateningprov2

import android.support.v4.content.ContextCompat.getExternalFilesDirs
import android.os.Build
import android.annotation.TargetApi
import android.content.Context
import android.util.Log
import java.io.File
import java.io.IOException


@TargetApi(Build.VERSION_CODES.KITKAT)
fun getExtSdCardPathsForActivity(context: Context): Array<String>
{
    val paths = ArrayList<String>()

    for (file in context.getExternalFilesDirs("external"))
    {
        if (file != null)
        {
            val index = file!!.absolutePath.lastIndexOf("/Android/data")

            if (index < 0)
            {
                Log.w("getExtSdCardPaths", "Unexpected external file dir: " + file!!.absolutePath)
            }
            else
            {
                var path = file!!.absolutePath.substring(0, index)

                try
                {
                    path = File(path).getCanonicalPath()
                }
                catch (e: IOException)
                {
                    // Keep non-canonical path.
                }

                paths.add(path)
            }
        }
    }

    if (paths.isEmpty()) paths.add("/storage/sdcard1")

    return paths.toTypedArray()
}


fun canListFiles(f: File): Boolean
{
    return f.canRead() && f.isDirectory
}

fun getStorageName(file : String) : String
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

    return name
}

fun isExternalStorage(path : String) : Boolean
{

    if ("/storage/emulated/legacy" == path || "/storage/emulated/0" == path || "/mnt/sdcard" == path)
    {
        return false
    }
    else if ("/storage/sdcard1" == path)
    {
        return true
    }
    return false
}

//For USB OTG
fun getUsbDrive(): File?
{
    var parent = File("/storage")

    try
    {
        for (f in parent.listFiles()!!)
            if (f.exists() && f.name.toLowerCase().contains("usb") && f.canExecute())
                return f
    }
    catch (e: Exception)
    {
    }

    parent = File("/mnt/sdcard/usbStorage")

    if (parent.exists() && parent.canExecute())
        return parent

    parent = File("/mnt/sdcard/usb_storage")

    return if (parent.exists() && parent.canExecute()) parent else null

}

class JFileSystem(val fpath : String)
{
    var path:String
    var type : Int = 0
    var label : String
    var this_item : File

    init
    {
        this.path = fpath
        this.this_item = File(fpath)
        this.label = getStorageName(fpath)
        //Log.e("JFF", "${this.label} : $fpath" )
        //type=3 is for mounted devices
        if(this.this_item.isDirectory)
        {
            this.type = 2
        }
        else
        {
            this.type = 1
        }

    }

    fun isMountedDevice(isExtStorage : Boolean) : Unit
    {
        this.type = if(isExtStorage) 4 else 3
    }

}
