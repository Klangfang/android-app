package com.wfm.soundcollaborations.sound;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by markus on 09.10.16.
 */
public class SoundPlayer {
    private final static String TAG = SoundPlayer.class.getSimpleName();

    // TODO free MediaPlayer after usage
    private MediaPlayer player;
    private Handler handler;
    private Timer timer;

    private TimeListener timeListener;
    private long interval;
    private boolean trackCompleted;

    public SoundPlayer() {
        this.player = new MediaPlayer();
        this.handler = new Handler();
        this.trackCompleted = false;

        initCompletionListener();
    }


    public void switchTrack(Context context, int res){
        player = MediaPlayer.create(context, res);
        initCompletionListener();
        prepareNewTrack();
    }


    public void switchTrack(String filename) {
        try {
            player.setDataSource(filename);
            player.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Player prepare failed");
        }

        prepareNewTrack();
    }


    public void pause(){
        player.pause();
        cancelTimer();
    }


    public void resume(){
        player.start();
        trackCompleted = false;

        initTimer();
    }


    public void stop() {
        if (player != null) {
            cancelTimer();
            player.stop();
        }
    }


    public void seek(int milliseconds){
        player.seekTo(milliseconds);
    }


    public void seek(float percentage){
        player.seekTo((int) (player.getDuration() * percentage));

    }


    public interface TimeListener{
        void onTimeUpdate(long time, float percentage);
    }


    public void setTimeListener(final TimeListener timeListener, long interval) {
        this.timeListener = timeListener;
        this.interval = interval;

        notifyListener();
    }


    private void prepareNewTrack(){
        notifyListener();
    }


    private void cancelTimer(){
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        notifyListener();
    }


    private void initTimer(){
        if (timer != null){
            timer.cancel();
            timer = null;
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (trackCompleted) { return; }
                        notifyListener();
                    }
                });
            }
        }, 0, interval);
    }


    private void initCompletionListener(){
        this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                trackCompleted = true;
                cancelTimer();

                if (timeListener != null){
                    timeListener.onTimeUpdate(player.getDuration(), 1);
                }
            }
        });
    }


    private void notifyListener(){
        if (timeListener != null) {
            timeListener.onTimeUpdate(player.getCurrentPosition(), (float) player.getCurrentPosition() / player.getDuration());
        }
    }

}
