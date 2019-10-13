package com.wfm.soundcollaborations.Editor.network;

import android.content.Context;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.wfm.soundcollaborations.Editor.utils.FileUtils;

import org.apache.commons.io.FilenameUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohammed on 10/27/17.
 */

public class SoundDownloader
{
    private static SoundDownloader downloader = null;

    private FileDownloadQueueSet mFileDownloadQueueSet;
    private List<BaseDownloadTask> downloadTasks = new ArrayList<>();

    public static SoundDownloader getSoundDownloader(Context context, FileDownloadListener listener)
    {
        if(downloader == null)
        {
            downloader = new SoundDownloader(context, listener);
        }
        return downloader;
    }

    public SoundDownloader(Context context, FileDownloadListener listener)
    {
        FileDownloader.setup(context);
        mFileDownloadQueueSet = new FileDownloadQueueSet(listener);
    }

    public void addSoundUrl(String urlText, int index, long compositionId) {
        try {
            URL url = new URL(urlText);
            String name = (FilenameUtils.getName(url.getPath()));
            downloadTasks.add(FileDownloader.getImpl().create(urlText).setPath(FileUtils.getKlangfangCacheDirectory()+"/"+name).setTag(index));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void download()
    {
        mFileDownloadQueueSet.disableCallbackProgressTimes();
        mFileDownloadQueueSet.setAutoRetryTimes(5);
        mFileDownloadQueueSet.downloadSequentially(this.downloadTasks);
        mFileDownloadQueueSet.start();
    }
}
