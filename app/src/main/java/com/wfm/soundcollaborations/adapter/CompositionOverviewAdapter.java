package com.wfm.soundcollaborations.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.webservice.CompositionServiceClient;
import com.wfm.soundcollaborations.webservice.dtos.CompositionOverviewResp;

import java.util.Arrays;
import java.util.List;

/**
 * The CompositionOverviewAdapter helps to render out a list of data.
 * It is an extended {@link ArrayAdapter} that can provide the layout for each list item.
 * The rendered list consists of {@link CompositionOverviewResp} objects.
 **/
public class CompositionOverviewAdapter extends RecyclerView.Adapter<CompositionOverviewAdapter.ViewHolder> {

    private List<CompositionOverviewResp> compositionOverviews;
    public static final String PICK_RESPONSE = "PICK";
    private CompositionServiceClient client;
    private ExoPlayerFactory exoPlayerFactory;
    private Context context;


    public CompositionOverviewAdapter(Activity context, List<CompositionOverviewResp> compositionOverviews) {
        this.context = context;
        this.compositionOverviews = compositionOverviews;
        client = new CompositionServiceClient(context.getApplicationContext());
    }

    /**
     * This method is called when a view is created in the first place to make sure there are views to bind
     * */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Create new layout of song_composition.xml and bind it to the listItemView
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.song_composition,
                        parent,false)
        );

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Check which compositions are being displayed in the viewport (Android magic)
        CompositionOverviewResp currentOverview = compositionOverviews.get(position);

        // Assign string values of {@link CompositionOverview} to the text views of layout
        holder.mCompositionTitleTextView.setText(currentOverview.title);
        holder.mMembersTextView.setText(currentOverview.numberOfMembers + "/4 Members"); //TODO replace string with resource
        holder.mPlayerControlView.setPlayer(getAudioPlayer(currentOverview.snippetUrl));
        holder.mJoinButton.setOnClickListener(view -> doRequest(currentOverview.pickUrl, view));

    }

    @Override
    public int getItemCount() {
        return this.compositionOverviews.size();
    }

    /**
     * Internal class that stores the views of a composition
     **/
    static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView mCompositionTitleTextView;
        final TextView mMembersTextView;
        final PlayerControlView mPlayerControlView;
        final Button mJoinButton;

        ViewHolder(@NonNull View itemView) {

            super(itemView);

            mCompositionTitleTextView = itemView.findViewById(R.id.composition_title);
            mMembersTextView = itemView.findViewById(R.id.composition_members);
            mPlayerControlView = itemView.findViewById(R.id.public_composition_player_view);
            mJoinButton = itemView.findViewById(R.id.join_button);

        }

    }


    private Player getAudioPlayer(String snippetUrl) {

        exoPlayerFactory = new ExoPlayerFactory();
        exoPlayerFactory.createExoPlayer(context);
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
