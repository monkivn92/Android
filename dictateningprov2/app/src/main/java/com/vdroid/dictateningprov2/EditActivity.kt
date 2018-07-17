package com.vdroid.dictateningprov2

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val content = intent.getStringExtra("content")
        val pos = intent.getIntExtra("pos", 0)

        sub_editor.text = SpannableStringBuilder(content)
        sub_editor.setSelection(pos)

    }
}
