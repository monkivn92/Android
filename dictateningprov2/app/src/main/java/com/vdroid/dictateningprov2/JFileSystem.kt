package com.vdroid.dictateningprov2
import com.vdroid.dictateningprov2.utils.FileFolderUtils
import java.io.File

class JFileSystem(val fpath : String)
{
    var path:String
    var type : Int = 0
    var label : String
    var this_item : File

    init
    {
        this.path = fpath
        this.this_item = File(fpath)
        this.label = FileFolderUtils.getStorageName(fpath)

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
