package com.vdroid.dictateningprov2.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import com.vdroid.dictateningprov2.JFileSystem

import java.io.File
import java.io.IOException
import java.util.ArrayList

class FileFolderUtils
{
    companion object
    {
        fun getStorageDirectories(context : Context) : ArrayList<String>
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

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                val strings = getExtSdCardPathsForActivity(context)

                for (s in strings)
                {
                    val f = File(s)
                    if (!rv.contains(s) && canListFiles(f))
                        rv.add(s)
                }
            }

            return rv
        }

        fun getAllStorages(context : Context) : ArrayList<JFileSystem>
        {
            val list_storages : ArrayList<String> = getStorageDirectories(context)
            var set_of_paths : MutableSet<String> = mutableSetOf<String>()

            val mPathList : ArrayList<JFileSystem> = ArrayList<JFileSystem>()

            for(s in list_storages)
            {
                set_of_paths.add(File(s).canonicalPath)
            }

            for (ss in set_of_paths)
            {
                var ff = JFileSystem(ss)
                ff.isMountedDevice(isExternalStorage(ss))
                mPathList.add(ff)
            }

            return mPathList
        }

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


    }
}