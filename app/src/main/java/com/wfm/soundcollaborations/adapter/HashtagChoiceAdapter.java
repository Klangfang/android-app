package com.wfm.soundcollaborations.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.wfm.soundcollaborations.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Markus Eberts on 22.10.16.
 */
public class HashtagChoiceAdapter extends ArrayAdapter<String> {

    private List<String> all = new ArrayList<>();
    private Set<String> selected = new HashSet<>();

    public HashtagChoiceAdapter(Context context, int resource) {
        super(context, resource);
    }


    public HashtagChoiceAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        all = items;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.hashtag_choice_row, null);
        }

        final String item = getItem(position);

        if (item != null) {
            final TextView tvName = (TextView) v.findViewById(R.id.tv_name);
            tvName.setText(item);

            CheckBox cbSelection = (CheckBox) v.findViewById(R.id.cb_selection);

            // Listen on checked status changes. Add the selected strings to a list for
            // later retrieval
            cbSelection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        selected.add(item);
                        tvName.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow_light));
                    } else {
                        selected.remove(item);
                        tvName.setTextColor(ContextCompat.getColor(getContext(), R.color.grey_light));
                    }
                }
            });

            if (selected.contains(item)){
                cbSelection.setChecked(true);
            } else {
                cbSelection.setChecked(false);
            }
        }

        return v;
    }


    public Set<String> getSelectedItems(){
        return selected;
    }


    public void setItems(List<String> items) {
        this.all = items;
        super.clear();
        super.addAll(items);
    }
    public void setItems(List<String> items, Set<String> selected) {
        this.all = items;
        this.selected = selected;
        super.clear();
        super.addAll(items);
    }
}
