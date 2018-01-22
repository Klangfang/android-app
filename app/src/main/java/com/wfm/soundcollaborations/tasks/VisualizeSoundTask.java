package com.wfm.soundcollaborations.tasks;

import android.os.AsyncTask;

import com.wfm.soundcollaborations.model.audio.AudioDecoder;
import com.wfm.soundcollaborations.model.audio.AudioVisualizer;
import com.wfm.soundcollaborations.model.composition.Sound;
import com.wfm.soundcollaborations.utils.FileUtils;
import com.wfm.soundcollaborations.views.composition.SoundView;

/**
 * Created by mohammed on 11/9/17.
 */

public class VisualizeSoundTask extends AsyncTask<Void, Chunk, Void>
{
    private SoundView soundView;
    private Sound sound;

    private AudioDecoder mAudioDecoder;
    private AudioVisualizer mAudioVisualizer;

    public VisualizeSoundTask(SoundView soundView, Sound sound)
    {
        this.soundView = soundView;
        this.sound = sound;
        this.mAudioDecoder = new AudioDecoder();
        this.mAudioVisualizer = new AudioVisualizer();
    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        String name = sound.getLink().split("/")[sound.getLink().split("/").length - 1];
        String path = FileUtils.getKlangfangCacheDirectory()+"/"+name;

        this.mAudioDecoder.decode(path, new AudioDecoder.Listener() {
            @Override
            public void onAudioDurationDetected(long audioLengthMilli)
            {

            }

            @Override
            public void onSampleRateAndChannelsDetected(int sampleRate, int channels)
            {
                mAudioVisualizer.setSampleRate(sampleRate);
                mAudioVisualizer.setChannelsNumber(channels);
            }

            @Override
            public void onRawChunkDecoded(byte[] chunk) {

            }

            @Override
            public void onShortChunkData(short[] chunk)
            {
                Chunk chunkObj = new Chunk(chunk);
                publishProgress(chunkObj);
            }
        });

        return null;
    }

    @Override
    protected void onProgressUpdate(Chunk... values)
    {
        short[] chunk = values[0].getChunk();
        this.mAudioVisualizer.visualize(chunk, 20, new AudioVisualizer.listener() {
            @Override
            public void valueCaptured(int value) {
                soundView.addWave(value);
            }
        });
    }
}
