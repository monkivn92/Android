package com.vdroid.dictateningprov2

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_files.*
import kotlinx.android.synthetic.main.ff_item.view.*
import java.io.File
import java.util.*

class StorageActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_files)

        val list_storages : ArrayList<String> = getStorageDirectories()
        var set_of_paths : MutableSet<String> = mutableSetOf<String>()

        list_storages?.let{

            val mPathList : MutableList<JFileSystem> = mutableListOf()

            var mRVadapter : FFAdapter = FFAdapter(mPathList, this)

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
            mRVadapter = FFAdapter(mPathList, this)
            file_list.layoutManager = LinearLayoutManager(this)
            file_list.adapter = mRVadapter

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 456)
        {
            Log.e("Log from Storagectivity", "vbbbbbbb")
        }
    }

    class FFAdapter(var paths : MutableList<JFileSystem>, val context: Context) : RecyclerView.Adapter<FFViewHolder>()
    {
        var aPathList : MutableList<JFileSystem>
        val aContext : Context

        init {
            this.aPathList = paths
            this.aContext = context
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FFViewHolder
        {
            return FFViewHolder(LayoutInflater.from(context).inflate(R.layout.ff_item, parent, false))
        }

        override fun onBindViewHolder(holder: FFViewHolder, position: Int)
        {
            val ff : JFileSystem = this.aPathList[position]
            holder.ff_name.text = ff.label

            when(ff.type)
            {
                3 -> holder.ff_icon?.setImageResource(R.drawable.harddisk)
                4 -> holder.ff_icon?.setImageResource(R.drawable.sd)
            }
            holder.ff_name.setOnClickListener{ view ->
                val intent = Intent(aContext, FilesActivity::class.java)
                intent.putExtra("sent_path", ff.path)
                (aContext as StorageActivity).startActivity(intent)
                aContext.finish()
            }

        }

        // Gets the number of animals in the list
        override fun getItemCount(): Int
        {
            return this.aPathList.size
        }

    }

    class FFViewHolder (view: View) : RecyclerView.ViewHolder(view)
    {
        // Holds the TextView that will add each animal to
        val ff_name : TextView
        val ff_icon : ImageView

        init {
            ff_name = view.ff_name
            ff_icon = view.ff_icon
        }

    }

}
