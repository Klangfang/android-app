package com.wfm.soundcollaborations.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.android.exoplayer2.ui.PlayerControlView;
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
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.song_composition, parent, false);
        }

        CompositionOverview currentOverview = getItem(position);

        //Assign String values of {@link Composition} to the text views of song_composition.xml
        TextView compositionTitleTextView = listItemView.findViewById(R.id.composition_title);
        assert currentOverview != null;
        compositionTitleTextView.setText(currentOverview.title);

        //TODO currentOverview.snippetUrl
        PlayerControlView playerControlView = listItemView.findViewById(R.id.public_composition_player_view);

        TextView membersTextView = listItemView.findViewById(R.id.composition_members);
        membersTextView.setText(String.valueOf(currentOverview.numberOfMembers));

        Button participateButton = listItemView.findViewById(R.id.join_button);
        participateButton.setOnClickListener(view -> doRequest(currentOverview.pickUrl, view));


        return listItemView;
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
