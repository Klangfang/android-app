package com.wfm.soundcollaborations.Editor.network;

import android.content.Context;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.wfm.soundcollaborations.Editor.utils.FileUtils;

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

    private SoundDownloader(Context context, FileDownloadListener listener)
    {
        FileDownloader.setup(context);
        mFileDownloadQueueSet = new FileDownloadQueueSet(listener);
    }

    public void addSoundUrl(String url, int index)
    {
        String name = url.split("/")[url.split("/").length - 1];
        name = name.replace("?compositionId=1", "");
        downloadTasks.add(FileDownloader.getImpl().create(url).setPath(FileUtils.getKlangfangCacheDirectory()+"/"+name).setTag(index));
    }

    public void download()
    {
        mFileDownloadQueueSet.disableCallbackProgressTimes();
        mFileDownloadQueueSet.setAutoRetryTimes(5);
        mFileDownloadQueueSet.downloadSequentially(this.downloadTasks);
        mFileDownloadQueueSet.start();
    }
}
