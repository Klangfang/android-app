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
 * {@link CompositionAdapter} is an {@link ArrayAdapter} that can provide the layout for each 
 * list item based on a data source, which is a list of {@link CompositionOverview} objects
 * Class inserts content in XML file song_composition.xml
 * Class in instanciated in {@link com.wfm.soundcollaborations.fragments.ComposeFragment}
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
     * Method override is used to extend the {@link ArrayAdapter} to be able to
     * show more than one text view.
     **/
    @NonNull
    @Override
    public View getView(
            int position, 
            @Nullable View convertView, 
            @NonNull ViewGroup parent) {
        
        View listItemView = convertView;

        // Get song_composition XML component
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.song_composition,
                    parent, 
                    false);
        }
        
        // Find current scrollPosition (Android magic)
        CompositionOverview currentComposition = getItem(position);

        // If we really have scrolled to a song_composition component...
        assert currentComposition != null;

        // ...Create the possibility to set the title of the song_composition component:
        // Assign string values of {@link CompositionOverview} to the text views of component
        TextView titleTextView = listItemView.findViewById(R.id.composition_title);

        // ...Create the possibility to set member amount of the song_composition component
        TextView membersTextView = listItemView.findViewById(R.id.composition_members);

        //... Set right title
        titleTextView.setText(
                currentComposition.getCompositionTitle());

        //... Set right member amount
        membersTextView.setText(
                currentComposition.getNumberOfMembers());

        // Return component
        return listItemView;
    }
}
