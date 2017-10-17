package com.wangw.m3u8cahceproxy.cache;

import com.wangw.m3u8cahceproxy.CacheProxyException;
import com.wangw.m3u8cahceproxy.CacheUtils;
import com.wangw.m3u8cahceproxy.Config;
import com.wangw.m3u8cahceproxy.L;
import com.wangw.m3u8cahceproxy.cache.m3u8.TsListDownloadTask;
import com.wangw.m3u8cahceproxy.cache.m3u8.TsListDownloadTaskCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jileilei on 2017/10/17.
 */

public class M3u8Download extends BaseDownload implements Runnable,TsListDownloadTaskCallback {

    private final String url;
    private final String name;
    private final Config config;
    private File cacheDir;
    private File m3u8File;
    private String basePath;
    private final ExecutorService mRequestPool = Executors.newFixedThreadPool(5);
    private LinkedList downloadList;

    public M3u8Download(Config config, String url, String name) {
        this.config = CacheUtils.checkNotNull(config, "Config不能为空");
        this.url = CacheUtils.checkNotNull(url, "url不能为空");
        this.name = CacheUtils.checkNotNull(name, "name不能为空");
        this.basePath = FileUtils.getUrlFileBasePath(this.url);
    }

    @Override
    public void run() {
        cacheDir = getCacheDir();
        try {
            m3u8File = createM3u8File();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CacheProxyException e) {
            e.printStackTrace();
        }
        if(!isCached()){
            // 尚未全部缓存。解析m3u8文件，找出未缓存的内容
            try {
                List<String> needDownloadFiles = findNeecCachedFile();
                // TODO 获取到待缓存列表。进行缓存
                for (String needDownloadFile : needDownloadFiles) {
                    TsListDownloadTask task = new TsListDownloadTask(new File(cacheDir.getAbsolutePath(), needDownloadFile), this.basePath + needDownloadFile);
                    task.setCallback(this);
                    downloadList.add(task);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void downloadFinish(String name) {

    }

    @Override
    public void downloadFailed(String name, CacheProxyException e) {

    }

    private List<String> findNeecCachedFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(m3u8File));
        String line;
        List<String> files = new ArrayList<>();
        while((line = reader.readLine()) != null){
            if(!line.startsWith("#")){
                // TODO 检查文件是否已经下载
                File tmpFile = new File(cacheDir.getAbsolutePath(), line);
                if(!tmpFile.exists()) {
                    files.add(line);
                }
            }
        }
        return files;
    }

    /**
     * 创建M3u8文件。若本地存在则直读，否则下载网络版本。
     * @throws IOException
     * @throws CacheProxyException
     * @return
     */
    private File createM3u8File() throws IOException, CacheProxyException {
        File m3u8File = new File(cacheDir.getAbsolutePath(), FileUtils.getFileNameForUrl(this.url));
        L.log(m3u8File.getAbsolutePath());
        boolean exists = m3u8File.exists();
        if(!exists){
            downloadFile(url, m3u8File);
        }
        return m3u8File;
    }

    /**
     * 检测是否已经缓存。
     * 完全缓存的依据是是否存在一个名为.end的文件。
     * @return
     */
    private boolean isCached(){
        File m3u8File = new File(cacheDir.getAbsolutePath(), ".end");
        return m3u8File.exists();
    }

    private File getCacheDir() {
        File dir = new File(config.getCacheRoot(), name);
        if (!dir.exists()){
            boolean flag = dir.mkdir();
            if (!flag)
                dir.mkdirs();
        }
        return dir;
    }

    private void onFailed(Exception e) {
        e.printStackTrace();
    }
}
