package com.vdroid.dictateningprov2

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.vdroid.dictateningprov2.utils.FileFolderUtils
import kotlinx.android.synthetic.main.activity_files.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.ff_item.view.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import android.widget.CheckBox



class StorageActivity : AppCompatActivity()
{
    lateinit var mRVadapter : FFAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_files)

        val list_storages : ArrayList<JFileSystem> = FileFolderUtils.getAllStorages(this)

        mRVadapter = FFAdapter(list_storages, this)
        this.mRVadapter.aPathStack.add("end")
        file_list.layoutManager = LinearLayoutManager(this)
        file_list.adapter = mRVadapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.file_menu, menu)
        return true
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId)
        {
            R.id.select_this -> {
                val intent = Intent()
                intent.action = "Path Broadcast"
                intent.putExtra("result_path", this.mRVadapter.cur_path)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class FFAdapter(var paths : ArrayList<JFileSystem>, val context: Context) : RecyclerView.Adapter<FFViewHolder>()
    {
        var aPathList : ArrayList<JFileSystem>
        val aContext : Context
        var aPathStack = ArrayDeque<String>()

        var cur_path : String = ""

        private var lastChecked: JFileSystem? = null
        private var lastCheckedPos = 0
        private var lastHolder : FFViewHolder? = null

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

            if(ff.isSelected)
            {
                //holder.itemView.ff_name.setTypeface(null, Typeface.BOLD_ITALIC)
                holder.itemView.setBackgroundColor(Color.parseColor("#174E8580"))
            }
            else
            {
                holder.itemView.ff_name.setTypeface(null, Typeface.NORMAL)
                holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }

            when(ff.type)
            {
                1 -> holder.ff_icon?.setImageResource(R.drawable.file)
                2 -> holder.ff_icon?.setImageResource(R.drawable.folder)
                3 -> holder.ff_icon?.setImageResource(R.drawable.harddisk)
                4 -> holder.ff_icon?.setImageResource(R.drawable.sd)
            }

            holder.ff_name.setOnClickListener{ view ->

                cur_path = ff.path

                if(ff.type != 1)
                {
                    val file_tmp = File(ff.path)

                    val newPathList : ArrayList<JFileSystem> = ArrayList<JFileSystem>()

                    val listFileTmp :  Array<String> = file_tmp.list()

                    listFileTmp.sort()

                    for(p in listFileTmp)
                    {
                        newPathList.add(JFileSystem("${ff.path}${File.separator}$p"))
                    }

                    if(ff.type == 2)
                    {
                        this.aPathStack.push(file_tmp.parent)
                    }

                    if(newPathList.size > 0)
                    {

                        this.aPathList.clear()
                        this.aPathList.addAll(newPathList)
                        this.aPathList.sortWith(compareBy( { it.type}, {it.label }))
                        notifyDataSetChanged()
                    }
                    else
                    {
                        this.aPathList.clear()
                        notifyDataSetChanged()
                        Toast.makeText(aContext, "This folder is empty", Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {

                    lastChecked?.let {
                        it.isSelected = false
                    }

                    //for textView marquee
                    //lastHolder?.let { it.ff_name.isSelected = false }

                    ///good for performance, but marquee can not run, use notifyDataSetChanged()
                    notifyItemChanged(lastCheckedPos)

                    ff.isSelected = true
                    lastChecked = ff

                    //for textView marquee
                    //holder.ff_name.isSelected = true
                    //lastHolder = holder

                    lastCheckedPos = holder.adapterPosition

                    notifyItemChanged(lastCheckedPos)

                }
            }

            holder.ff_name.setOnLongClickListener { v ->
                Toast.makeText(aContext, "${holder.ff_name.text}", Toast.LENGTH_SHORT).show()
                true
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
                var newPathList : ArrayList<JFileSystem> = ArrayList<JFileSystem>()

                val path = this.aPathStack.pop()

                if(path == "end")
                {
                    newPathList = FileFolderUtils.getAllStorages(this.aContext)
                    this.aPathStack.add("end")
                }
                else
                {
                    val file_tmp = File(path)

                    val listFileTmp :  Array<String> = file_tmp.list()

                    listFileTmp.sort()

                    for(p in listFileTmp)
                    {
                        newPathList.add(JFileSystem("$path${File.separator}$p"))
                    }
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
