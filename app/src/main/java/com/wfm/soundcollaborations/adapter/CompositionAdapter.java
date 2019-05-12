package com.wfm.soundcollaborations.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.wfm.soundcollaborations.Classes.CompositionOverview;
import com.wfm.soundcollaborations.R;

import java.util.ArrayList;

/**
 * {@link CompositionAdapter} is an {@link ArrayAdapter} that can provide the layout for each list item
 * based on a data source, which is a list of {@link CompositionOverview} objects
 **/
public class CompositionAdapter extends ArrayAdapter<CompositionOverview> {

    public CompositionAdapter(Activity context, ArrayList<CompositionOverview> compositions) {
        super(context, 0, compositions);
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

        CompositionOverview currentComposition = getItem(position);

        //Assign String values of {@link CompositionOverview} to the text views of song_composition.xml
        TextView compositionTitleTextView = listItemView.findViewById(R.id.composition_title);
        assert currentComposition != null;
        compositionTitleTextView.setText(currentComposition.getCompositionTitle());

        TextView locationsTextView = listItemView.findViewById(R.id.locations_text_view);
        locationsTextView.setText(currentComposition.getCompositionLocations());

        TextView membersTextView = listItemView.findViewById(R.id.members_text_view);
        membersTextView.setText(currentComposition.getNumberOfMembers());

        //Button playButton = listItemView.findViewById(R.id.btn_play_public_composition);
        // TODO What happens with playButton?

        return listItemView;
    }
}
