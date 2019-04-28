package com.wfm.soundcollaborations.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ohoussein.playpause.PlayPauseView;
import com.wfm.soundcollaborations.Classes.Composition;
import com.wfm.soundcollaborations.Editor.model.audio.AudioPlayer;
import com.wfm.soundcollaborations.Editor.model.composition.CompositionBuilder;
import com.wfm.soundcollaborations.Editor.utils.JSONUtils;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.activities.MainActivity;
import com.wfm.soundcollaborations.adapter.CompositionAdapter;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.OnClick;

/**
 * The Compose Fragment provides public compositions that the user can join and an option
 * to create a new composition.
 *
 * It loads and displays {@link Composition} objects.
 * */

public class ComposeFragment extends Fragment {

    private View root;
    private String compositionOverview;
    private AudioPlayer audioPlayer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_compose, container, false);
        initToolbar();

        //JSON Audio Mock Data
        String jsonData = "{"
                + " 'uuid': '3423423-432434-43243241-33-22222',"
                + " 'sounds': ["

                + "   {'length': 30680, 'track': 2, 'start_position': 20000, 'link': "
                + "'https://stereoninjamusic.weebly.com/uploads/4/5/7/5/45756923/the_midnight_ninja.ogg'}"

                + " ]"
                + "}";

        //New array with test data for composition views
        ArrayList<Composition> compositions = new ArrayList<>();
        compositions.add(new Composition("Uni Sounds", "Hamburg, München", "1/4 Mitglieder"));
        compositions.add(new Composition("Quiet Fire", "Hamburg, München", "1/4 Mitglieder"));
        compositions.add(new Composition("Baobab", "Hamburg, München", "1/4 Mitglieder"));
        compositions.add(new Composition("Nom Nom Sounds", "Hamburg, München", "1/4 Mitglieder"));
        compositions.add(new Composition("Blablabla", "Hamburg, München", "1/4 Mitglieder"));

        CompositionAdapter compositionAdapter = new CompositionAdapter(Objects.requireNonNull(getActivity()), compositions);

        ListView listView = root.findViewById(R.id.public_compositions_list);
        listView.setAdapter(compositionAdapter);

        return root;
    }

    /**
     * This method initializes the top app bar and sets a custom
     * title and background color.
     * TODO Use recycler view here instead of list view
     * */
    private void initToolbar() {
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        Toolbar toolbar = mainActivity.getToolbar();
        toolbar.setTitle(R.string.bnm_compose);
        toolbar.setBackgroundColor(getResources().getColor(R.color.navigation));
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_title));
    }


    public void playComposition(View view)
    {
        Toast.makeText(getActivity(), "Play!", Toast.LENGTH_LONG).show();
        ((PlayPauseView) view).toggle();
    }

}
