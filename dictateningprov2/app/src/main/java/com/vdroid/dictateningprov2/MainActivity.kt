package com.vdroid.dictateningprov2

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

import kotlinx.android.synthetic.main.activity_main.*

import android.os.Build

import java.util.*

import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri

import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.os.Environment
import android.preference.PreferenceManager
import android.support.v4.content.LocalBroadcastManager
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.*
import android.widget.*

import com.vdroid.dictateningprov2.utils.MPManager
import com.vdroid.dictateningprov2.utils.VPayLoad
import java.io.*
import java.nio.file.Path
import java.nio.file.Paths


class MainActivity : AppCompatActivity()
{
    private val GRANTED: Int = 1
    private val DENIED: Int = 2
    private val BLOCKED_OR_NEVER_ASKED: Int = 3
    private val APP_PERMISSIONS_REQUEST: Int = 44
    private lateinit var mLBReceiver: BroadcastReceiver
    private lateinit var mLBReceiverSave: BroadcastReceiver
    var filePath : String = ""
    var filePathSave : String = ""

    lateinit var mSeekBar : SeekBar
    lateinit var mPlayingTime : TextView
    lateinit var mDuration : TextView
    lateinit var mPlayBtn : ImageButton
    lateinit var mSkipFW : ImageButton
    lateinit var mSkipBW : ImageButton

    lateinit var mpm : MPManager

    lateinit var prefs : SharedPreferences

    lateinit var prefsEditor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        prefs = getSharedPreferences("dict_store", Context.MODE_PRIVATE)
        prefsEditor = prefs.edit()

        mSeekBar= seekbar
        mPlayingTime = playing_time_txt
        mDuration = duration_txt
        mPlayBtn = play_btn
        mSkipFW = skip_fw_btn
        mSkipBW = skip_bw_btn

        //Request permission
        if(isPermissionIsGranted(Manifest.permission.READ_EXTERNAL_STORAGE, this) != GRANTED)
        {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE.toString()))
        }

        //Publish and register broadcast receiver
        val filter = IntentFilter()
        filter.addAction("Path Broadcast")
        mLBReceiver = object : BroadcastReceiver()
        {
            override fun onReceive(context: Context, intent: Intent)
            {
                if (intent.action == "Path Broadcast")
                {
                    filePath = intent.getStringExtra("result_path")
                    mpm.reset()
                    Log.e("LBPath", filePath)
                }
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mLBReceiver, filter)


        //Listen key event when typing input
        input_txt.setOnKeyListener { _, keyCode, event ->
            if ( keyCode == KeyEvent.KEYCODE_ENTER)
            {
                //Toast.makeText(this, input_txt.text, Toast.LENGTH_SHORT).show()
                if(input_txt.text.isNotBlank())
                {
                    text_editor.append("${input_txt.text}\n")
                }
                input_txt.setText("")
                true
            }
            else false

        }

        //Music player
        mpm  = MPManager(this)

        play_btn.setOnClickListener{v ->
            mpm.play()

        }

        skip_fw_btn.setOnClickListener{v ->
            mpm.skipFW()
        }

        skip_bw_btn.setOnClickListener{v ->
            mpm.skipBW()
        }

        mSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
            {
                if(fromUser && mpm.getDuration() != 0)
                {
                    mpm.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?)
            {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?)
            {
            }

        })

        val val_from_last_session : String = prefs.getString("editor_text_autosave", "")

        if(val_from_last_session.isNotBlank())
        {
            text_editor.setText(val_from_last_session)
        }

        text_editor.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?)
            {
                prefsEditor.putString("editor_text_autosave", text_editor.text.toString())
                prefsEditor.apply()

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        text_editor.setOnClickListener { v ->
            input_txt.requestFocus()
            text_editor.clearFocus()
            val intent = Intent(this@MainActivity, EditActivity::class.java)
            intent.putExtra("content", text_editor.text.toString())
            intent.putExtra("pos", text_editor.selectionStart)
            startActivityForResult(intent, 3333)

        }



    }

    override fun onResume()
    {
        super.onResume()
        //hideNavigationBar()
    }

    override fun onStop()
    {
        super.onStop()
        mpm.pause()
    }

    override fun onDestroy()
    {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLBReceiver)

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
            R.id.select_file -> {
                val intent = Intent(this@MainActivity, StorageActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_exit -> {
                finish()
                return true
            }

            R.id.clear_text -> {
                text_editor.setText("")
                prefsEditor.putString("editor_text_last_session", text_editor.text.toString())
                prefsEditor.apply()
                return true
            }

            R.id.save_text -> {
                val intent = Intent(this@MainActivity, SaveFileActivity::class.java)

                intent.putExtra("editor_content", text_editor.text.toString())
                intent.putExtra("current_playing_time", mpm.getCurrentPlayingTime())
                intent.putExtra("current_playing_file", mpm.getCurrentPlayingFile())
                startActivity(intent)
                return true
            }

            R.id.save_progress -> {
                val intent = Intent(this@MainActivity, SaveProgressActivity::class.java)

                intent.putExtra("editor_content", text_editor.text.toString())
                intent.putExtra("current_playing_time", mpm.getCurrentPlayingTime())
                intent.putExtra("current_playing_file", mpm.getCurrentPlayingFile())
                startActivity(intent)
                return true
            }
            R.id.load_progress -> {
                val intent = Intent(this@MainActivity, LoadProgressActivity::class.java)
                startActivityForResult(intent, 6666)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 3333)
        {
            val modified_content = data?.getStringExtra("modified_content")
            if(modified_content != null && modified_content.isNotBlank())
            {
                text_editor.text = SpannableStringBuilder(modified_content)
            }
        }

        if(requestCode == 6666)
        {
            val progress_path = data?.getStringExtra("progress_path")
            if(!progress_path.isNullOrBlank())
            {
                val fin = FileInputStream(progress_path)
                val ois = ObjectInputStream(fin)
                val savedObject : VPayLoad = ois.readObject() as VPayLoad
                savedObject?.let {
                    filePath = it.audioFile
                    text_editor.text = SpannableStringBuilder(it.textContent)
                    mpm.reset()
                    mpm.seekTo(it.audioPlayingTime)
                }
            }
        }
    }


}
