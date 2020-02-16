package com.wfm.soundcollaborations.interaction.editor.tasks;

import android.os.AsyncTask;

import com.wfm.soundcollaborations.interaction.editor.model.audio.AudioDecoder;
import com.wfm.soundcollaborations.interaction.editor.model.audio.AudioVisualizer;
import com.wfm.soundcollaborations.interaction.editor.views.composition.SoundView;


public class VisualizeSoundTask extends AsyncTask<Void, Chunk, Void> {

    private SoundView soundView;
    private String path;

    private AudioDecoder mAudioDecoder;
    private AudioVisualizer mAudioVisualizer;

    public VisualizeSoundTask(SoundView soundView, String path) {

        this.soundView = soundView;
        this.path = path;
        this.mAudioDecoder = new AudioDecoder();
        this.mAudioVisualizer = new AudioVisualizer();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        this.mAudioDecoder.decode(path, new AudioDecoder.Listener() {
            @Override
            public void onAudioDurationDetected(long audioLengthMilli) {

            }

            @Override
            public void onSampleRateAndChannelsDetected(int sampleRate, int channels) {

                mAudioVisualizer.setSampleRate(sampleRate);
                mAudioVisualizer.setChannelsNumber(channels);
            }

            @Override
            public void onRawChunkDecoded(byte[] chunk) {

            }

            @Override
            public void onShortChunkData(short[] chunk) {

                Chunk chunkObj = new Chunk(chunk);
                publishProgress(chunkObj);
            }
        });

        return null;
    }

    @Override
    protected void onProgressUpdate(Chunk... values) {

        short[] chunk = values[0].getChunk();
        this.mAudioVisualizer.visualize(chunk, 20, value -> soundView.addWave(value));
    }
}
