package com.wangw.m3u8cahceproxy.cache.m3u8;

import com.wangw.m3u8cahceproxy.CacheProxyException;

/**
 * Created by jileilei on 2017/10/17.
 */

public interface TsListDownloadTaskCallback {
    void downloadFinish(String name);

    void downloadFailed(String name,CacheProxyException e);
}
