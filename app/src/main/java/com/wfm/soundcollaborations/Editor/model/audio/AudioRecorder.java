package com.wfm.soundcollaborations.Editor.model.audio;

import android.media.MediaRecorder;
import android.util.Log;

import com.wfm.soundcollaborations.Editor.exceptions.RecordTimeOutExceededException;
import com.wfm.soundcollaborations.Editor.model.Constants;
import com.wfm.soundcollaborations.Editor.utils.AudioRecorderStatus;
import com.wfm.soundcollaborations.Editor.utils.FileUtils;

import java.io.IOException;
import java.util.UUID;


public class AudioRecorder implements MediaRecorder.OnInfoListener
{
    private final static String TAG = AudioRecorder.class.getSimpleName();

    private static final int MAX_DURATION = Constants.MAX_RECORD_TIME * 1000;
    private static final String SOUND_FILE_BASE_PATH = FileUtils.getKlangfangCacheDirectory() + "/";
    private static final String SOUND_FILE_EXTENSION = ".3gp";

    private MediaRecorder mMediaRecorder;

    private AudioRecorderStatus status = AudioRecorderStatus.EMPTY;

    private String filePath;

    private int startTime;
    private int duration;

    private int recordedTime = 0;

    public AudioRecorder() {}


    public void start(int startTime) throws RecordTimeOutExceededException {
        // create new sound file path
        if (status.equals(AudioRecorderStatus.EMPTY)) {
            status = AudioRecorderStatus.RECORDING;

            String uniqueFileName = UUID.randomUUID().toString().replace("-", "");
            filePath = SOUND_FILE_BASE_PATH + uniqueFileName + SOUND_FILE_EXTENSION;

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
            this.startTime = startTime;
        } else if (status.equals(AudioRecorderStatus.RECORDING) && recordedTime >= MAX_DURATION) {
            status = AudioRecorderStatus.STOPPED;
            Log.d(TAG, "Max Duration Reached");
            throw new RecordTimeOutExceededException("Record has reached maximum time.");
        }
    }


    public void stop(int stopTime) {

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
            duration = Long.valueOf(stopTime - startTime).intValue();

            //TODO due to asynchronous stop and no guarantee to immediately stop recording,
            // before play and before publish (the view is already consistent)

            // total recorded time for the track
            this.recordedTime = this.recordedTime + duration;
        } catch (Exception ex) {
            FileUtils.deleteFile(filePath);
            Log.e(TAG, "Recorded file has been deleted!");
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
            Log.d(TAG, "Max Duration Reached");
            status = AudioRecorderStatus.STOPPED;
        }
    }
}
