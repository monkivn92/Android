package com.vdroid.dictateningprov2.utils

import android.media.MediaPlayer
import android.widget.Toast
import com.vdroid.dictateningprov2.MainActivity
import com.vdroid.dictateningprov2.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI

class MPManager(private val mainActivity: MainActivity)
{
    private lateinit var mp : MediaPlayer
    private var playgingTime : String = ""
    private var duration : String = ""
    private var isMusicLoaded : Boolean = false

    private var updateJob : Job? = null

    init {
        initMediaPlayer()
    }

    fun initMediaPlayer()
    {
        mp = MediaPlayer()
    }

    fun resetState()
    {
        playgingTime = ""
        duration = ""
        isMusicLoaded = false
    }

    fun play()
    {
        if(!mp.isPlaying)
        {
            if(!isMusicLoaded)
            {
                loadFile()
            }
            mainActivity.mPlayBtn.setImageResource(R.drawable.play)
            mp.start()
            updateUI()
        }
        else
        {
            pause()
        }
    }

    fun pause()
    {
        if(mp.isPlaying)
        {
            mainActivity.mPlayBtn.setImageResource(R.drawable.pause)
            mp.pause()
            stopUpdateUI()
        }
    }

    fun loadFile()
    {
        isMusicLoaded = false

        try {
            mp.setDataSource(mainActivity.filePath)
            mp.prepare()
            isMusicLoaded = true
            mainActivity.mDuration.text = mp.duration.toString()
            mainActivity.mPlayingTime.text = mp.currentPosition.toString()
        }
        catch (ex : Exception)
        {
            Toast.makeText(mainActivity, "Please select a valid music file first", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopUpdateUI()
    {
        updateJob?.cancel()
    }

    fun updateUI()  {

        updateJob = launch{

            repeat(mp.duration){

                withContext(UI){
                    mainActivity.mDuration.text = mp.duration.toString()
                    mainActivity.mPlayingTime.text = mp.currentPosition.toString()
                }
                delay(1000)
            }
        }

    }
}