package com.wangw.m3u8cahceproxy.cache.m3u8;

import com.wangw.m3u8cahceproxy.CacheProxyException;
import com.wangw.m3u8cahceproxy.L;
import com.wangw.m3u8cahceproxy.cache.BaseDownload;

import java.io.File;

/**
 * Created by jileilei on 2017/10/17.
 */

public class TsListDownloadTask extends BaseDownload implements Runnable {

    private File saveFile;
    private String url;
    private TsListDownloadTaskCallback callback;

    public TsListDownloadTask(File saveFile, String url) {
        this.saveFile = saveFile;
        this.url = url;
    }

    public void setCallback(TsListDownloadTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        L.log(url);
        try {
            downloadFile(url, saveFile);
            if(callback != null){
                callback.downloadFinish(url);
            }
        } catch (CacheProxyException e) {
            callback.downloadFailed(url, e);
        }
    }
}
