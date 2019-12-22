package com.wfm.soundcollaborations.Editor.model.audio;

import android.media.MediaRecorder;
import android.util.Log;

import com.wfm.soundcollaborations.Editor.exceptions.RecordTimeOutExceededException;
import com.wfm.soundcollaborations.Editor.model.Constants;
import com.wfm.soundcollaborations.Editor.utils.AudioRecorderStatus;
import com.wfm.soundcollaborations.Editor.utils.FileUtils;

import java.io.IOException;
import java.util.UUID;


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
    private int recordedTime = 0;
    private int startTime;
    private int duration;

    public AudioRecorder() {}


    public void start() throws RecordTimeOutExceededException {
        // create new sound file path
        if (status.equals(AudioRecorderStatus.EMPTY)) {
            status = AudioRecorderStatus.RECORDING;

            String uniqueFileName = UUID.randomUUID().toString().replace("-", "");
            filePath = SOUND_FILE_BASE_URI_DIR + uniqueFileName + SOUND_FILE_EXTENSION;

            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            int newMaxDuration = 0;
            int restTime = MAX_DURATION - recordedTime;
            if (restTime <= 0) {
                recordedTime = MAX_DURATION;
            } else {
                newMaxDuration = restTime;
            }
            mMediaRecorder.setMaxDuration(newMaxDuration);
            mMediaRecorder.setOnInfoListener(this);

            try {
                mMediaRecorder.prepare();
            } catch (IOException e) {
                Log.e(TAG, "preparePlayer() failed");
            }

            mMediaRecorder.start();
            startTime = Long.valueOf(System.currentTimeMillis()).intValue();
        } else if (status.equals(AudioRecorderStatus.RECORDING) && recordedTime >= MAX_DURATION) {
            status = AudioRecorderStatus.STOPPED;
            throw new RecordTimeOutExceededException("Record has reached maximum time.");
        }
    }

    // Stop and set max time is reached
   /* public void stop() {
        stop();
    }*/

    public void stop() {

        try {
            if (mMediaRecorder != null) {

                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
                if (status.equals(AudioRecorderStatus.RECORDING)) {
                    status = AudioRecorderStatus.EMPTY;
                }
            }

            // sound length
            duration = Long.valueOf(System.currentTimeMillis() - startTime).intValue();

            // total recorded time for the track
            this.recordedTime = this.recordedTime + duration;
        } catch (Exception ex) {
            FileUtils.deleteFile(filePath);
            Log.d(TAG, "Recorded file has been deleted!");
        }
    }

    // Increases the time limit after deleting sound
    public void increaseTime(long deletedTime) {
        recordedTime -= deletedTime;
        status = AudioRecorderStatus.EMPTY;
    }

    public int getMaxAmplitude()
    {
        if(mMediaRecorder != null)
            return mMediaRecorder.getMaxAmplitude();
        return 0;
    }

    public String getRecordedFilePath()
    {
        return filePath;
    }

    public AudioRecorderStatus getStatus() {
        return status;
    }

    public Integer getDuration() {
        return duration;
}

    @Override
    public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {
        if (i == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
        {
            Log.e(TAG, "Max Duration Reached");
           // stop(MAX_DURATION); //TODO Fuehrt dazu, dass die Aufnahme verschwindet....
            status = AudioRecorderStatus.STOPPED;
        }
    }
}
