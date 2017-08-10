package com.wfm.soundcollaborations.sound;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Created by Markus Eberts on 03.01.17.
 */
public class SoundVisualizer {
    private final static String TAG = SoundVisualizer.class.getSimpleName();

    private Visualizer visualizer;
    private MediaPlayer player;

    public SoundVisualizer(){
        this.player = new MediaPlayer();
    }

    public void visualize(String filename){
        try {
            player.setDataSource(filename);
            player.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Player prepare failed");
        }

        int rate = Visualizer.getMaxCaptureRate();
        float intensity = 0;

        visualizer = new Visualizer(player.getAudioSessionId());
        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                float intensity = ((float) waveform[0] + 128f) / 256;
                Log.e(TAG, "Intensity: " + intensity);
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {

            }
        },rate , true, false);

        visualizer.setEnabled(true);
        player.start();



        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                visualizer.setEnabled(false);
                player.stop();
                player.release();
            }
        });
    }
}
