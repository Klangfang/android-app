package com.wfm.soundcollaborations.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.wfm.soundcollaborations.Classes.CompositionOverview;
import com.wfm.soundcollaborations.R;

import java.util.ArrayList;
import java.util.Set;

/**
 * {@link CompositionAdapter} is an {@link ArrayAdapter} that can provide the layout
 *      for a list of {@link CompositionOverview} objects.
 *
 * It is instantiated in {@link com.wfm.soundcollaborations.fragments.ComposeFragment} and ...
 *      - uses the layout song_composition.xml to display the data.
 *      - is passed title, number of members and a sound URI.
 *      - creates new instances of a {@link SimpleExoPlayer} for each {@link CompositionOverview}
 *          that the user listens to.
 **/
public class CompositionAdapter extends ArrayAdapter<CompositionOverview> {
    // Link with Composition Overview
    public CompositionAdapter(
            Activity context,
            ArrayList<CompositionOverview> compositions) {
        super(
                context,
                0,
                compositions);
    }

    /**
     * The default {@link ArrayAdapter} can just display one textView
     * Here we override it, so it can do more.
     **/
    @NonNull
    @Override
    public View getView(
            int position,
            @Nullable View convertView,
            @NonNull ViewGroup parent) {

        // ListItemView will be the generic view that will be recycled to save memory
        View listItemView = convertView;

        // Assign layout of song_composition.xml to the listItemView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.song_composition,
                    parent,
                    false
            );
        }

        // Check which compositions are being displayed in the viewport (Android magic)
        CompositionOverview currentComposition = getItem(position);

        // Make sure there is a current composition
        assert currentComposition != null;

        // Assign string values of {@link CompositionOverview} to the text views of layout
        TextView titleTextView = listItemView.findViewById(R.id.composition_title);
        TextView membersTextView = listItemView.findViewById(R.id.composition_members);

        // Set title of currently visible compositions
        titleTextView.setText(
                currentComposition.mTitle
        );



        // Set member amount of currently visible compositions
        membersTextView.setText(
                currentComposition.mNumberOfMembers + "/4 Members" // TODO replace string with resource
        );

        // Create instance of PlayerControlView and assign it to the correct layout view
        PlayerControlView playerControlView = listItemView.findViewById(R.id.public_composition_player_view);

        // Default Setup for creating an Instance SimpleExoPlayer
        DefaultLoadControl control = new DefaultLoadControl();
        TrackSelector trackSelector = new DefaultTrackSelector();
        RenderersFactory factory = new DefaultRenderersFactory(getContext());
        SimpleExoPlayer audioPlayer = ExoPlayerFactory.newSimpleInstance(
                factory,
                trackSelector,
                control
        );

        // Pass audioPlayer to the player layout view
        playerControlView.setPlayer(audioPlayer);

        // Parse the String, so that we can use the URI
        Uri uri = Uri.parse(
                currentComposition.mSoundUri
        );

        // Create a new MediaSource
        MediaSource mediaSource = new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("Klangfang")).
                createMediaSource(uri); //pass the uri to the method

        /* Prepare the player, passing it the mediaSource
         * No need to do anything else, because ExoPlayer recognizes User input like play and pause automatically!
         **/
        audioPlayer.prepare(mediaSource);

        // Return dynamically created compositions
        return listItemView;
    }
}
