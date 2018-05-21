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