package com.wfm.soundcollaborations.Editor.model.audio;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;


import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class AudioPlayer {

    private Context context;
    private ExoPlayerFactory exoPlayerFactory;
    //private ConcatenatingMediaSource sources;

    private boolean isPlaying = false;

    public AudioPlayer(Context context) {

        this.context = context;
        init();

    }

    private void init() {

        exoPlayerFactory = new ExoPlayerFactory();
        exoPlayerFactory.createExoPlayer(context);

    }

    public void addSounds(List<String> uris) {

        exoPlayerFactory.prepare(uris);

    }

    public void play() {

        isPlaying = true;
        exoPlayerFactory.play(true);

    }

    public void pause() {

        isPlaying = false;
        exoPlayerFactory.pause(false);

    }

    public boolean isPlaying() {

        return isPlaying;

    }

    public void seek(long positionsMs) {

        exoPlayerFactory.seek(positionsMs);

    }

    public void reset() {

        exoPlayerFactory.reset();

    }

    public void release() {

        exoPlayerFactory.release();

    }

    public void addBufferedPercentageListener(final TextView view) {

        Timer timer = new Timer();
        final Handler handler = new Handler();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run()
                    {
                        view.setText("Buffering: " + exoPlayerFactory.getBufferedPercentage());
                    }
                });
            }
        }, 0, 1000);

    }
}
