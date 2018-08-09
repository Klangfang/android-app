package com.wfm.soundcollaborations.Editor.model.audio;

import android.media.MediaRecorder;
import android.util.Log;

import com.wfm.soundcollaborations.Editor.model.Constants;
import com.wfm.soundcollaborations.Editor.utils.AudioRecorderStatus;
import com.wfm.soundcollaborations.Editor.utils.DateUtils;
import com.wfm.soundcollaborations.Editor.utils.FileUtils;

import java.io.IOException;


/**
 * Created by markus on 09.10.16.
 * Edited by Mohammed on 06.10.17
 */
public class AudioRecorder implements MediaRecorder.OnInfoListener
{
    private final static String TAG = AudioRecorder.class.getSimpleName();

    private static final int MAX_DURATION = Constants.MAX_RECORD_TIME * 1000;

    private MediaRecorder mMediaRecorder;
    private final String SOUND_FILE_BASE_URI_DIR = FileUtils.getKlangfangCacheDirectory() + "/";
    private final String SOUND_FILE_EXTENSION = ".3gp";
    private String filePath;
    private AudioRecorderStatus status = AudioRecorderStatus.EMPTY;
    private int recordedTime;

    public AudioRecorder() {}


    public void start()
    {
        // create new sound file path
        if (status.equals(AudioRecorderStatus.EMPTY)) {
            status = AudioRecorderStatus.RECORDING;
            if (this.filePath != null)
                FileUtils.deleteFile(this.filePath);

            this.filePath = SOUND_FILE_BASE_URI_DIR + "SOUND_" +
                    DateUtils.getCurrentDate("yyyyMMdd_HHmmss") + SOUND_FILE_EXTENSION;
            this.mMediaRecorder = null;
        }

        if(mMediaRecorder == null)
        {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mMediaRecorder.setOutputFile(this.filePath);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setMaxDuration(recordedTime >= MAX_DURATION ? 0 : MAX_DURATION - recordedTime);
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

    // Stop and set max time is reached
    public void stop() {
        stop(MAX_DURATION);
    }

    public void stop(int recordedTime)
    {
        this.recordedTime = recordedTime;

        try
        {
            if(mMediaRecorder != null)
            {

                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
                this.filePath = null;
                if (status.equals(AudioRecorderStatus.RECORDING)) {
                    status = AudioRecorderStatus.EMPTY;
                }
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
           // stop(MAX_DURATION); //TODO Fuehrt dazu, dass die Aufnahme verschwindet....
            status = AudioRecorderStatus.STOPED;
        }
    }
}
