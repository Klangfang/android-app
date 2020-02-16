package com.wfm.soundcollaborations.interaction.editor.model.audio;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;


public final class ExoPlayerFactory {

    private static final String APP_NAME = "Klangfang";

    private final DefaultDataSourceFactory dataSourceFactory;
    private SimpleExoPlayer player;


    private ExoPlayerFactory(Context context) {

        player = new SimpleExoPlayer.Builder(context).build();

        // Produces DataSource instances through which media data is loaded.
        dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, APP_NAME));

    }


    public static ExoPlayerFactory build(Context context) {

        return new ExoPlayerFactory(context);

    }


    public void prepare(List<String> uris) {

        for (String uri : uris) {

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

        player.setPlayWhenReady(false);
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
