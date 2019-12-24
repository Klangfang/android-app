package com.wfm.soundcollaborations.Editor.model.audio;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

import static com.google.android.exoplayer2.ExoPlayerFactory.newSimpleInstance;


public class ExoPlayerFactory {

    private static final String APP_NAME = "Klangfang";

    private Context context;

    private SimpleExoPlayer player;

    public void createExoPlayer(Context context) {

        this.context = context;
        player = newSimpleInstance(context);

    }

    public void prepare(List<String> uris) {

        for (String uri : uris) {

            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, APP_NAME));
            // This is the MediaSource representing the media to be played.
            MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(uri));
            // Prepare the player with the source.
            player.prepare(videoSource);

        }

    }

    public SimpleExoPlayer getPlayer() {

        return player;

    }

    void release() {

        player.stop();
        player.release();

    }

    void playOrPause(boolean play) {

        player.setPlayWhenReady(play);

    }

    void seek(long positionsMs) {

        player.seekTo(0, positionsMs);

    }

    void reset() {

        player.seekTo(0);

    }

}
