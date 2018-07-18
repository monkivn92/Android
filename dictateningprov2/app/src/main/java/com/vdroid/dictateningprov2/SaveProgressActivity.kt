package com.vdroid.dictateningprov2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.provider.DocumentFile
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.vdroid.dictateningprov2.utils.FileFolderUtils
import com.vdroid.dictateningprov2.utils.VPayLoad
import kotlinx.android.synthetic.main.activity_save_file.*
import kotlinx.android.synthetic.main.ff_item.view.*
import java.io.*
import java.util.*

class SaveProgressActivity : AppCompatActivity()
{

    lateinit var mRVadapter : FFAdapter

    private var editor_content : String = ""
    private var current_playing_time : Int = 0
    private var current_playing_file : String = ""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_file)

        editor_content = intent.getStringExtra("editor_content")
        current_playing_time = intent.getIntExtra("current_playing_time", 0)
        current_playing_file = intent.getStringExtra("current_playing_file")

        val list_storages : ArrayList<JFileSystem> = FileFolderUtils.getAllStorages(this)

        mRVadapter = FFAdapter(list_storages, this)
        this.mRVadapter.aPathStack.add("end")
        file_list_save.layoutManager = LinearLayoutManager(this)
        file_list_save.adapter = mRVadapter
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

                if(file_name.text.isNullOrEmpty())
                {
                    Toast.makeText(this, "Please enter a valid file name", Toast.LENGTH_SHORT).show()
                    return true
                }

                if(File(this.mRVadapter.cur_path).canWrite())
                {
                    if(!current_playing_file.isNullOrBlank() && !editor_content.isNullOrBlank())
                    {
                        val vpayload : VPayLoad = VPayLoad(current_playing_file, current_playing_time, editor_content)
                        try
                        {
                            val fpath = "${this.mRVadapter.cur_path}${File.separator}${file_name.text}"
                            val oos = ObjectOutputStream(FileOutputStream(fpath))
                            oos.writeObject(vpayload)
                            oos.close()
                            Toast.makeText(this, "Save File Successfully", Toast.LENGTH_SHORT).show()
                        }
                        catch (e: IOException)
                        {

                           Toast.makeText(this, "Save File Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else
                    {
                        Toast.makeText(this, "Save content is empty or audio not loaded", Toast.LENGTH_SHORT).show()
                    }

                    return true

                }
                else
                {
                    if(!this.mRVadapter.isAccessingSDCard)
                    {
                        Toast.makeText(this, "This directory is not writable, need grant permission to write SD card", Toast.LENGTH_LONG).show()
                    }
                    else
                    {
                        Toast.makeText(this, "Need to perform some special actions to write a file to SD card", Toast.LENGTH_LONG).show()
                        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), 5656)
                    }

                    super.onOptionsItemSelected(item)
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 5656 && resultCode == Activity.RESULT_OK)
        {
            val treeUri = data?.getData()
            val pickedDir = DocumentFile.fromTreeUri(this, treeUri)
            val file = pickedDir.createFile("//MIME type", file_name.text.toString())
            val outttt = contentResolver.openOutputStream(file.uri)

            if(!current_playing_file.isNullOrBlank() && !editor_content.isNullOrBlank())
            {
                val vpayload : VPayLoad = VPayLoad(current_playing_file, current_playing_time, editor_content)
                try
                {
                    val bos = ByteArrayOutputStream()
                    val oos = ObjectOutputStream(bos)
                    oos.writeObject(vpayload)

                    outttt.write(bos.toByteArray())

                    Toast.makeText(this, "Save File Successfully", Toast.LENGTH_SHORT).show()
                }
                catch (e: IOException)
                {
                    Toast.makeText(this, "Save File Failed", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Save content is empty or audio not loaded", Toast.LENGTH_SHORT).show()
            }

        }
    }

    class FFAdapter(var paths : ArrayList<JFileSystem>, val context: Context) : RecyclerView.Adapter<FFViewHolder>()
    {
        var aPathList : ArrayList<JFileSystem>
        val aContext : Context
        var aPathStack = ArrayDeque<String>()

        var cur_path : String = ""

        var isAccessingSDCard : Boolean = false

        private var selectedPos = RecyclerView.NO_POSITION

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

            holder.itemView.isSelected = this.selectedPos == position
            holder.ff_name.text = ff.label

            if(holder.itemView.isSelected)
            {
                holder.itemView.ff_name.setTypeface(null, Typeface.BOLD_ITALIC)
                holder.itemView.setBackgroundColor(Color.parseColor("#c0c0c0"))
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

                when(ff.type)
                {
                    3 -> isAccessingSDCard = false
                    4 -> isAccessingSDCard = true
                }

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
                        this.aPathList.clear()
                        notifyDataSetChanged()
                        Toast.makeText(aContext, "This folder is empty", Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    notifyItemChanged(selectedPos)
                    selectedPos = holder.adapterPosition
                    notifyItemChanged(selectedPos)
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
                var newPathList : ArrayList<JFileSystem> = ArrayList<JFileSystem>()

                val path = this.aPathStack.pop()

                Log.e("path", path)

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
