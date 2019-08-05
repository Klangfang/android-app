package com.wfm.soundcollaborations.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioFocusManager;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.wfm.soundcollaborations.Editor.activities.EditorActivity;
import com.wfm.soundcollaborations.Editor.model.audio.ExoPlayerFactory;
import com.wfm.soundcollaborations.Editor.model.composition.CompositionOverview;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.webservice.CompositionServiceClient;

import java.util.Arrays;
import java.util.List;

/**
 * {@link CompositionOverviewAdapter} is an {@link ArrayAdapter} that can provide the layout for each list item
 * based on a data source, which is a list of {@link CompositionOverview} objects
 **/
public class CompositionOverviewAdapter extends ArrayAdapter<CompositionOverview> {

    public static final String PICK_RESPONSE = "PICK";

    private CompositionServiceClient client;

    private ExoPlayerFactory exoPlayerFactory;


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
        View listItemView;

        // Create new list item, if it does not exist yet
        if (convertView == null) {

            // Create new layout of song_composition.xml and bind it to the listItemView
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            listItemView = layoutInflater.inflate(R.layout.song_composition,
                    parent,false);

            // Find all views just once
            TextView compositionTitleTextView = listItemView.findViewById(R.id.composition_title);
            TextView membersTextView = listItemView.findViewById(R.id.composition_members);
            PlayerControlView playerControlView = listItemView.findViewById(R.id.public_composition_player_view);
            Button participateButton = listItemView.findViewById(R.id.join_button);

            // Create instance of viewHolder for memory effective recycling
            ViewHolder viewHolder = new ViewHolder(
                    compositionTitleTextView,
                    membersTextView,
                    playerControlView,
                    participateButton);

            listItemView.setTag(viewHolder);

        } else {

            // List item exists, so just use convertView
            listItemView = convertView;

        }

        // Check which compositions are being displayed in the viewport (Android magic)
        CompositionOverview currentOverview = getItem(position);

        assert currentOverview != null;

        // Assign string values of {@link CompositionOverview} to the text views of layout
        ViewHolder viewHolder = (ViewHolder) listItemView.getTag();
        viewHolder.mCompositionTitleTextView.setText(currentOverview.title);
        viewHolder.mMembersTextView.setText(currentOverview.numberOfMembers + "/4 Members"); //TODO replace string with resource
        viewHolder.mPlayerControlView.setPlayer(getAudioPlayer(currentOverview.snippetUrl));
        viewHolder.mParticipateButton.setOnClickListener(view -> doRequest(currentOverview.pickUrl, view));

        //TODO Delete this, if not needed anymore
        //TextView compositionTitleTextView = listItemView.findViewById(R.id.composition_title);
        //compositionTitleTextView.setText(currentOverview.title);
        //TextView membersTextView = listItemView.findViewById(R.id.composition_members);
        //membersTextView.setText(currentOverview.numberOfMembers + "/4 Members");
        //Button participateButton = listItemView.findViewById(R.id.join_button);
        //participateButton.setOnClickListener(view -> doRequest(currentOverview.pickUrl, view));
        // Create instance of PlayerControlView and assign it to the correct layout view
        //PlayerControlView playerControlView = listItemView.findViewById(R.id.public_composition_player_view);
        // Pass audioPlayer to the player layout view

        return listItemView;
    }

    /**
     * Internal class that stores the views of a composition
     **/
    static class ViewHolder {

        final TextView mCompositionTitleTextView;
        final TextView mMembersTextView;
        final PlayerControlView mPlayerControlView;
        final Button mParticipateButton;

        ViewHolder(TextView titleTextView, TextView membersTextView,
                   PlayerControlView playerControlView, Button participateButton) {

            mCompositionTitleTextView = titleTextView;
            mMembersTextView = membersTextView;
            mPlayerControlView = playerControlView;
            mParticipateButton = participateButton;

        }

    }


    private Player getAudioPlayer(String snippetUrl) {

        exoPlayerFactory = new ExoPlayerFactory();
        exoPlayerFactory.createExoPlayer(getContext());
        exoPlayerFactory.prepare(Arrays.asList(snippetUrl));

        return exoPlayerFactory.getPlayer();

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
