package com.wfm.soundcollaborations.Editor.utils;

import android.os.Environment;

import com.wfm.soundcollaborations.Editor.model.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

    public static byte[] getBytes(String filePath) {
        byte[] bytes = new byte[1024];
        try (FileOutputStream stream = new FileOutputStream(filePath)) {
            stream.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace(); //TODO
        } catch (IOException e) {
            e.printStackTrace(); //TODO
        }
        return bytes;
    }
}
