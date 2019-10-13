package com.wfm.soundcollaborations.adapter;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.wfm.soundcollaborations.Editor.activities.EditorActivity;
import com.wfm.soundcollaborations.Editor.model.audio.ExoPlayerFactory;
import com.wfm.soundcollaborations.webservice.dtos.CompositionOverviewResp;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.webservice.CompositionServiceClient;

import java.util.Arrays;
import java.util.List;

/**
 * {@link CompositionOverviewAdapter} is an {@link ArrayAdapter} that can provide the layout for each list item
 * based on a data source, which is a list of {@link CompositionOverviewResp} objects
 **/
public class CompositionOverviewAdapter extends ArrayAdapter<CompositionOverviewResp> {

    public static final String PICK_RESPONSE = "PICK";

    private CompositionServiceClient client;

    private ExoPlayerFactory exoPlayerFactory;


    public CompositionOverviewAdapter(Activity context, List<CompositionOverviewResp> compositionOverviewRespons) {
        super(context, 0, compositionOverviewRespons);
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
        CompositionOverviewResp currentOverview = getItem(position);

        assert currentOverview != null;

        // Assign string values of {@link CompositionOverviewResp} to the text views of layout
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
