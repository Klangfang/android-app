package com.wfm.soundcollaborations.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.wfm.soundcollaborations.Editor.activities.EditorActivity;
import com.wfm.soundcollaborations.Editor.model.composition.CompositionOverview;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.webservice.CompositionServiceClient;

import java.util.List;

/**
 * {@link CompositionOverviewAdapter} is an {@link ArrayAdapter} that can provide the layout for each list item
 * based on a data source, which is a list of {@link CompositionOverview} objects
 **/
public class CompositionOverviewAdapter extends ArrayAdapter<CompositionOverview> {

    private CompositionServiceClient client;
    public static final String PICK_RESPONSE = "PICK";

    public CompositionOverviewAdapter(Activity context, List<CompositionOverview> compositionOverviews) {
        super(context, 0, compositionOverviews);
        client = new CompositionServiceClient(context.getApplicationContext());
    }

    /**
     * The method override is used to extend the {@link ArrayAdapter} to be able to show more than one text view.
     **/
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // ListItemView will be the generic view that will be recycled to save memory
        View listItemView = convertView;
        // Assign layout of song_composition.xml to the listItemView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.song_composition,
                    parent,
                    false);
        }

        // Check which compositions are being displayed in the viewport (Android magic)
        CompositionOverview currentOverview = getItem(position);

        assert currentOverview != null;

        // Assign string values of {@link CompositionOverview} to the text views of layout
        TextView compositionTitleTextView = listItemView.findViewById(R.id.composition_title);
        compositionTitleTextView.setText(currentOverview.title);

        TextView membersTextView = listItemView.findViewById(R.id.composition_members);
        membersTextView.setText(currentOverview.numberOfMembers + "/4 Members"); // TODO replace string with resource

        Button participateButton = listItemView.findViewById(R.id.join_button);
        participateButton.setOnClickListener(view -> doRequest(currentOverview.pickUrl, view));

        // Create instance of PlayerControlView and assign it to the correct layout view
        PlayerControlView playerControlView = listItemView.findViewById(R.id.public_composition_player_view);

        // Pass audioPlayer to the player layout view
        playerControlView.setPlayer(getAudioPlayer(currentOverview.snippetUrl));


        return listItemView;
    }


    private Player getAudioPlayer(String snippetUrl) {

        // Default Setup for creating an Instance SimpleExoPlayer
        DefaultLoadControl control = new DefaultLoadControl();
        TrackSelector trackSelector = new DefaultTrackSelector();
        RenderersFactory factory = new DefaultRenderersFactory(getContext());

        SimpleExoPlayer audioPlayer = ExoPlayerFactory.newSimpleInstance(factory, trackSelector, control);
        // Parse the String, so that we can use the URI
        Uri uri = Uri.parse(snippetUrl);

        // Create a new MediaSource
        DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory("Klangfang");
        ExtractorMediaSource.Factory mediaSourceFactory = new ExtractorMediaSource.Factory(defaultHttpDataSourceFactory);
        MediaSource mediaSource = mediaSourceFactory.createMediaSource(uri); //pass the uri to the method

        /* Prepare the player, passing it the mediaSource
         * No need to do anything else, because ExoPlayer recognizes User input like play and pause automatically!
         **/
        audioPlayer.prepare(mediaSource);

        return audioPlayer;

    }

    private void doRequest(String url, View view) {
        Response.Listener<String> listener = response -> startEditorActivity(view, response);
        client.pick(url, listener);
    }


    private void startEditorActivity(View view, String response) {
        Intent intent = new Intent(view.getContext(), EditorActivity.class);
        intent.putExtra(PICK_RESPONSE, response);
        view.getContext().startActivity(intent);
    }
}
