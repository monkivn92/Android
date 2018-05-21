package com.vdroid.dictateningprov2;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Copyright 2014 Charles-Eugene LOUBAO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Retrieve the list of external storage directories by reading /proc/mounts
 */
class ExternalStorage {

    private static File[] list;

    static {
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream("/proc/mounts"));
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            String content ="";

            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("rw")  && (line.contains("primary") || line.contains("fuse")|| line.contains("emulated") || line.contains("removable"))) {
                    content += line+"\n";
                }
            }
            Log.e("123", content);
            StringTokenizer tokenizer = new StringTokenizer(content);
            List<File> fileList = new ArrayList<File>();

            while (tokenizer.hasMoreTokens()) {
                String path = tokenizer.nextToken();
                File toTest = new File(path);
                if (toTest.exists() && toTest.canRead() && toTest.canWrite() && !toTest.isHidden() && toTest.isDirectory()) {
                    fileList.add(toTest);
                }
            }
            list = fileList.toArray(new File[fileList.size()]);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File[] getExternalStorageDirectories() {
        return list;
    }
}