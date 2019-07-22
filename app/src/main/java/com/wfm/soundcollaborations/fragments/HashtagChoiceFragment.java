package com.wfm.soundcollaborations.fragments;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.adapter.HashtagChoiceAdapter;
import com.wfm.soundcollaborations.fragments.interfaces.FragmentObserver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Markus Eberts on 24.10.16.
 */
public class HashtagChoiceFragment extends DialogFragment {

    private ListView lvItems;
    private HashtagChoiceAdapter choiceAdapter;

    private FragmentObserver observer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.hashtag_choice, container, false);

        // Toolbar
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.hashtag_choice);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.btn_complete_selection){
                    notifySelection();
                    return true;
                }

                return false;
            }
        });

        lvItems = (ListView) v.findViewById(R.id.lv_items);
        choiceAdapter = new HashtagChoiceAdapter(getContext(), R.layout.hashtag_choice_row);
        lvItems.setAdapter(choiceAdapter);
        lvItems.setTextFilterEnabled(true);

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cbSelection = (CheckBox) view.findViewById(R.id.cb_selection);
                cbSelection.setChecked(!cbSelection.isChecked());
            }
        });

        return  v;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();

        List<String> items = args.getStringArrayList("items");

        if (args.containsKey("selected")){
            List<String> selected = args.getStringArrayList("selected");
            Set<String> selectedSet = new HashSet<>();
            selectedSet.addAll(selected);
            setItems(items, selectedSet);
        } else {
            setItems(items);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
        //getDialog().getWindow().setLayout(params.MATCH_PARENT, height - 200);
    }

    public void setObserver(FragmentObserver observer){
        this.observer = observer;
    }

    public void setItems(List<String> items){
        choiceAdapter.setItems(items);
    }

    public void setItems(List<String> items, Set<String> selected){
        choiceAdapter.setItems(items, selected);
    }

    private void notifySelection(){
        // Return result to the calling activity
        String[] selectedItems = choiceAdapter.
                getSelectedItems().toArray(new String[0]);

        Bundle data = new Bundle();
        data.putStringArray("items", selectedItems);

        observer.fragmentUpdate(FragmentObserver.Event.SELECTED_ITEM, data);
        dismiss();
    }
}
