package com.wangw.m3u8cahceproxy.cache;

import android.text.TextUtils;

import com.wangw.m3u8cahceproxy.CacheUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangw on 2017/2/28.
 */

public class FileUtils {

    /**
     * 文件是否存在
     * @param file
     * @return
     */
    public static boolean exists(File file){
        if (file != null){
            return file.exists();
        }
        return false;
    }

    /**
     * 从URL中获取文件名称
     * @param url
     * @return
     */
    public static String getFileNameForUrl(String url){
        if (TextUtils.isEmpty(url))
            return "";
        int index = url.lastIndexOf("/");
        if (index != -1){
            String name = url.substring(index+1,url.length());
            index = name.lastIndexOf("?");
            if (index != -1){
                name = name.substring(0,index);
            }
            return name;
        }
        return url;
    }

    public static String getUrlFileBasePath(String url){
        if (TextUtils.isEmpty(url))
            return "";
        int index = url.indexOf("?");
        if (index != -1){
            url = url.substring(0, index);
            return getFileNameForUrl(url);
        }else{
            index = url.lastIndexOf("/");
            url = url.substring(0, index+1);
            return url;
        }
    }

    public static void saveFile(InputStream inputStream,File outputFile) throws IOException {
        InputStream ips = new BufferedInputStream(inputStream);
//        int length = connection.getContentLength();
        File newTempFile = new File(outputFile.getAbsoluteFile() + ".d3");
        FileOutputStream ops = new FileOutputStream(newTempFile);
        byte[] buffer = new byte[CacheUtils.DEFAULT_BUFFER_SIZE];
        int length;
        while ((length = ips.read(buffer)) != -1){
            ops.write(buffer,0,length);
        }
        ops.flush();
        newTempFile.renameTo(outputFile);
        CacheUtils.close(ips);
        CacheUtils.close(ops);
    }

}
