package com.wfm.soundcollaborations.model.audio;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.wfm.soundcollaborations.model.Constants;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mohammed on 10/9/17.
 */

public class AudioPlayer
{
    private Context context;
    private SimpleExoPlayer player;
    private ConcatenatingMediaSource sources;

    private boolean isPlaying = false;

    public AudioPlayer(Context context)
    {
        this.context = context;
        init();
    }

    private void init()
    {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(null, null, Constants.BUFFER_SEGMENT_SIZE);
        DefaultLoadControl control = new DefaultLoadControl(
                new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE),
                5 * 60 * 1000, // the size of chunk
                10 * 60 * 1000,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
        TrackSelection.Factory audioTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(audioTrackSelectionFactory);
        RenderersFactory factory = new DefaultRenderersFactory(context);
        player = ExoPlayerFactory.newSimpleInstance(factory, trackSelector, control);
    }

    public void addSounds(String uris[])
    {
        // create a new player to delete olds sounds
        init();

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, context.getPackageName()), bandwidthMeter);
        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // This is the MediaSource representing the media to be played.
        MediaSource audioSources[] = new MediaSource[uris.length];
        for(int i=0; i<uris.length; i++)
        {
            audioSources[i] = new ExtractorMediaSource(Uri.parse(uris[i]),
                    dataSourceFactory, extractorsFactory, null, null);
        }
        this.sources = new ConcatenatingMediaSource(audioSources);
        this.player.prepare(sources);

    }

    public void play()
    {
        isPlaying = true;
        player.setPlayWhenReady(true);
    }

    public void pause()
    {
        isPlaying = false;
        player.setPlayWhenReady(false);
    }

    public boolean isPlaying()
    {
        return this.isPlaying;
    }

    public void seek(int positionsMs)
    {
        player.seekTo(0, positionsMs);
    }
    public void reset()
    {
        player.seekTo(0);
    }

    public void release()
    {
        player.stop();
        player.release();
    }

    public void addBufferedPercentageListener(final TextView view)
    {
        Timer timer = new Timer();
        final Handler handler = new Handler();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run()
                    {
                        view.setText("Buffering: "+player.getBufferedPercentage());
                    }
                });
            }
        }, 0, 1000);
    }
}
