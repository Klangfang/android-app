package com.wfm.soundcollaborations.model.audio;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.wfm.soundcollaborations.exceptions.RecordTimeOutExceededException;
import com.wfm.soundcollaborations.model.Constants;
import com.wfm.soundcollaborations.utils.AudioRecorderStatus;
import com.wfm.soundcollaborations.utils.DateUtils;
import com.wfm.soundcollaborations.utils.FileUtils;

import java.io.IOException;


/**
 * Created by markus on 09.10.16.
 * Edited by Mohammed on 06.10.17
 */
public class AudioRecorder implements MediaRecorder.OnInfoListener
{
    private final static String TAG = AudioRecorder.class.getSimpleName();

    private static final int MAX_DURATION = Constants.MAX_RECORD_TIME  * 1000;

    private MediaRecorder mMediaRecorder;
    private final String SOUND_FILE_BASE_URI_DIR = FileUtils.getKlangfangCacheDirectory() + "/";
    private final String SOUND_FILE_EXTENSION = ".3gp";
    private String filePath;
    private AudioRecorderStatus status = AudioRecorderStatus.EMPTY;

    public void create()
    {
        if(this.filePath != null)
            FileUtils.deleteFile(this.filePath);

        this.filePath = SOUND_FILE_BASE_URI_DIR + "SOUND_" +
                DateUtils.getCurrentDate("yyyyMMdd_HHmmss")+ SOUND_FILE_EXTENSION;
        this.mMediaRecorder = null;
    }

    public void start()
    {
        if(mMediaRecorder == null)
        {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mMediaRecorder.setOutputFile(this.filePath);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setMaxDuration(MAX_DURATION);
            mMediaRecorder.setOnInfoListener(this);

            try
            {
                mMediaRecorder.prepare();
            }
            catch (IOException e)
            {
                Log.e(TAG, "prepare() failed");
            }

            mMediaRecorder.start();
            status = AudioRecorderStatus.RECORDING;
        }
    }

    public void stop()
    {
        try
        {
            if(mMediaRecorder != null)
            {

                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        }
        catch (Exception ex)
        {
            FileUtils.deleteFile(this.filePath);
            Log.d(TAG, "Recorded file has been deleted!");
        }

    }

    public int getMaxAmplitude()
    {
        if(mMediaRecorder != null)
            return mMediaRecorder.getMaxAmplitude();
        return 0;
    }

    public String getRecordedFilePath()
    {
        return this.filePath;
    }

    public AudioRecorderStatus getStatus() {
        return status;
    }

    @Override
    public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {
        if (i == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
        {
            Log.e(TAG, "Max Duration Reached");
            //stop(); //Fuehrt dazu, dass die Aufnahme verschwindet....
            status = AudioRecorderStatus.STOPED;
        }
    }
}
