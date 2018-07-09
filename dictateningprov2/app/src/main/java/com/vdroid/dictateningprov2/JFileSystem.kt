package com.vdroid.dictateningprov2
import com.vdroid.dictateningprov2.utils.FileFolderUtils
import java.io.File

class JFileSystem(val fpath : String)
{
    var path:String
    var type : Int = 0
    var label : String
    var this_item : File
    val STRING_LIMIT : Int = 40
    var isSelected : Boolean = false

    init
    {
        this.path = fpath
        this.this_item = File(fpath)
        val temp_label : String = FileFolderUtils.getStorageName(fpath)

        if(temp_label.length > STRING_LIMIT)
        {
            this.label = temp_label.substring(0,25) + "..."
        }
        else
        {
            this.label = temp_label
        }

        if(this.this_item.isDirectory)
        {
            this.type = 2
        }
        else
        {
            this.type = 1
        }

    }


    fun isMountedDevice(isExtStorage : Boolean) : Unit
    {
        this.type = if(isExtStorage) 4 else 3
    }

}
