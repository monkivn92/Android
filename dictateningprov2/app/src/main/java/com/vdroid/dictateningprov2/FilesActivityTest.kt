package com.vdroid.dictateningprov2

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_files.*
import java.io.File
import java.time.Duration
import android.support.annotation.NonNull
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.ff_item.view.*
import java.util.*


class FilesActivityTest : AppCompatActivity()
{

    //lateinit var mRVviewholder : FFViewHolder
    var mPathList : MutableList<JFileSystem> = mutableListOf()

    var mRVadapter : FFAdapter = FFAdapter(this.mPathList,this)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_files)

        val list_storages : ArrayList<String> = getStorageDirectories()
        var set_of_paths : MutableSet<String> = mutableSetOf<String>()

        list_storages?.let{
            for(s in list_storages)
            {
                //Log.e("Path", File(s).canonicalPath)
                set_of_paths.add(File(s).canonicalPath)
            }

            for (ss in set_of_paths)
            {
                var ff = JFileSystem(ss)
                ff.isMountedDevice(isExternalStorage(ss))
                this.mPathList.add(ff)
            }
            this.mRVadapter = FFAdapter(this.mPathList,this)
            file_list.layoutManager = LinearLayoutManager(this)
            file_list.adapter = this.mRVadapter

        }

        btn_ff_up.setOnClickListener{view ->
            this@FilesActivityTest.mRVadapter.backToPreviousItem()
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


    class FFAdapter(var paths : MutableList<JFileSystem>, val context: Context) : RecyclerView.Adapter<FFViewHolder>()
    {
        var aPathList : MutableList<JFileSystem>
        val aContext : Context
        var aPathStack = ArrayDeque<String>()

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
                1 -> holder.ff_icon?.setImageResource(R.drawable.file)
                2 -> holder.ff_icon?.setImageResource(R.drawable.folder)
                3 -> holder.ff_icon?.setImageResource(R.drawable.harddisk)
                4 -> holder.ff_icon?.setImageResource(R.drawable.sd)
            }
            holder.ff_name.setOnClickListener{ view ->

                if(ff.type != 1)
                {
                    val file_tmp = File(ff.path)

                    val newPathList : MutableList<JFileSystem> = mutableListOf()

                    val listFileTmp :  Array<String> = file_tmp.list()
                    listFileTmp.sort()

                    for(p in listFileTmp)
                    {
                        newPathList.add(JFileSystem("${ff.path}${File.separator}$p"))
                    }

                    if(newPathList.size > 0)
                    {
                        if(ff.type == 3 || ff.type == 4)
                        {
                            this.aPathStack.push("storages")
                        }
                        else
                        {
                            this.aPathStack.push(file_tmp.parent)
                        }

                        for (p in this.aPathStack)
                        {
                            Log.e("PathStack", p)
                        }

                        this.aPathList.clear()
                        this.aPathList.addAll(newPathList)
                        notifyDataSetChanged()
                    }
                    else
                    {
                        Toast.makeText(aContext, "This folder is empty", Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    Toast.makeText(aContext, "I want this file", Toast.LENGTH_SHORT).show()
                }

            }

        }

        // Gets the number of animals in the list
        override fun getItemCount(): Int
        {
            return this.aPathList.size
        }

        fun backToPreviousItem()
        {
            if(this.aPathStack.size > 0)
            {
                val path = this.aPathStack.pop()

                val file_tmp = File(path)

                val newPathList : MutableList<JFileSystem> = mutableListOf()

                val listFileTmp :  Array<String> = file_tmp.list()

                listFileTmp.sort()

                for(p in listFileTmp)
                {
                    newPathList.add(JFileSystem("$path${File.separator}$p"))
                }

                if(newPathList.size > 0)
                {
                    this.aPathList.clear()
                    this.aPathList.addAll(newPathList)
                    notifyDataSetChanged()
                }
            }
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
