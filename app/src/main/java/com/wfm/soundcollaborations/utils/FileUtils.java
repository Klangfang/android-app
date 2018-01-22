package com.wfm.soundcollaborations.utils;

import android.os.Environment;
import android.util.Log;

import com.wfm.soundcollaborations.model.Constants;

import java.io.File;

/**
 * Created by mohammed on 10/9/17.
 */

public class FileUtils
{
    public static boolean deleteFile(String fileUri)
    {
        File file = new File(fileUri);
        return file.delete();
    }

    public static boolean exists(String fileUri)
    {
        File file = new File(fileUri);
        return file.exists();
    }

    public static boolean isSoundFileSupported(String fileUri)
    {
        for(String ext: Constants.SUPPORTED_SOUND_FILE_EXTENSIONS)
            if(fileUri.endsWith(ext))
                return true;
        return false;
    }

    public static String getFileExtension(String fileUri)
    {
        String[] parts = fileUri.split("\\.");
        return parts[parts.length - 1];
    }

    public static String getKlangfangBaseDirectory()
    {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/klangfang";
        File directory = new File(path);
        if(directory.exists())
            return path;
        directory.mkdirs();
        return path;
    }

    public static String getKlangfangCacheDirectory()
    {
        String path = getKlangfangBaseDirectory()+"/cache";
        File directory = new File(path);
        if(directory.exists())
            return path;
        directory.mkdirs();
        return path;
    }
}
