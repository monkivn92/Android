package com.vdroid.dictateningprov2.utils

import java.io.Serializable

data class VPayLoad(val audioFile : String, val audioPlayingTime : Int, val textContent : String) : Serializable