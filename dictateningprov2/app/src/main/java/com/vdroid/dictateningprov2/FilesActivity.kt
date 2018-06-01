package com.vdroid.dictateningprov2

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
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
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.ff_item.view.*
import java.util.*


class FilesActivity : AppCompatActivity()
{

    //lateinit var mRVviewholder : FFViewHolder
    var mPathList : MutableList<JFileSystem> = mutableListOf()

    var mRVadapter : FFAdapter = FFAdapter(this.mPathList,this)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_files)

        val sent_path = intent.getStringExtra("sent_path")

        if(!sent_path.isNullOrBlank())
        {
            val tmp_file = File(sent_path)

            for(p in tmp_file.list())
            {
                this.mPathList.add(JFileSystem("${sent_path}${File.separator}$p"))
            }

            this.mRVadapter = FFAdapter(this.mPathList,this)
            this.mRVadapter.aPathStack.add("end")
            file_list.layoutManager = LinearLayoutManager(this)
            file_list.adapter = this.mRVadapter
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            this.mRVadapter.backToPreviousItem()
            return true
        }
        else
        {
            // Return
            return super.onKeyDown(keyCode, event)
        }
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
                /*
                3 -> holder.ff_icon?.setImageResource(R.drawable.harddisk)
                4 -> holder.ff_icon?.setImageResource(R.drawable.sd)
                */
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
                        this.aPathStack.push(file_tmp.parent)

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
                    val intent = Intent()
                    intent.action = "Path Broadcast"
                    intent.putExtra("result_path", ff.path)
                    LocalBroadcastManager.getInstance(aContext).sendBroadcast(intent)
                    (aContext as FilesActivity).finish()
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
                Log.e("path", path)
                if(path == "end")
                {
                    (context as FilesActivity).finish()
                }
                else
                {
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
