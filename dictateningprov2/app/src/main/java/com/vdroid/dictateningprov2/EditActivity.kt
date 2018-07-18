package com.vdroid.dictateningprov2

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_edit.*
import android.app.Activity
import android.util.Log


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

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.file_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId) {
            R.id.select_this -> {
                val resultIntent = Intent()
                resultIntent.putExtra("modified_content", sub_editor.text.toString())
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }




}
