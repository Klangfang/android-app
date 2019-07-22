package com.wfm.soundcollaborations.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.database.FriendEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Markus Eberts on 22.10.16.
 */
public class FriendChoiceAdapter extends ArrayAdapter<FriendEntity> implements Filterable{

    private List<FriendEntity> allFriends = new ArrayList<>();
    private List<FriendEntity> filteredFriends = new ArrayList<>();
    private Set<FriendEntity> selectedFriends = new HashSet<>();

    private FriendFilter filter = new FriendFilter();

    private class FriendFilter extends Filter{
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            String searchText = constraint.toString().toLowerCase();

            Filter.FilterResults filterResults = new Filter.FilterResults();
            ArrayList<FriendEntity> filteredList = new ArrayList<>();

            for(int i = 0; i < allFriends.size(); i++){
                FriendEntity entity = allFriends.get(i);

                if (entity.getName().toLowerCase().contains(searchText) ||
                        entity.getContactName().toLowerCase().contains(searchText)){
                    filteredList.add(entity);
                }
            }

            filterResults.values = filteredList;
            filterResults.count = filteredList.size();

            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence contraint, Filter.FilterResults results) {
            filteredFriends = (ArrayList<FriendEntity>) results.values;
            notifyDataSetChanged();
        }
    }


    public FriendChoiceAdapter(Context context, int resource) {
        super(context, resource);
    }


    public FriendChoiceAdapter(Context context, int resource, List<FriendEntity> items) {
        super(context, resource, items);

        allFriends = items;
        filteredFriends = items;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.friend_choice_row, null);
        }

        final FriendEntity friend = getItem(position);

        if (friend != null) {
            ((TextView) v.findViewById(R.id.tv_name)).setText(friend.getName());

            // UI initialization
            if (friend.getContactName() != null) {
                ((TextView) v.findViewById(R.id.tv_contact_name)).setText(friend.getContactName());
            } else {
                v.findViewById(R.id.tv_separator).setVisibility(View.GONE);
                v.findViewById(R.id.tv_contact_name).setVisibility(View.GONE);
            }

            CheckBox cbFriendSelection = (CheckBox) v.findViewById(R.id.cb_selection);
            // Listen on checked status changes. Add the ids of selected friends to a list for
            // later retrieval
            cbFriendSelection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        selectedFriends.add(friend);
                    } else {
                        selectedFriends.remove(friend);
                    }
                }
            });

            // TODO Find a better way...
            if (selectedFriends.contains(friend)){
                cbFriendSelection.setChecked(true);
            } else {
                cbFriendSelection.setChecked(false);
            }
        }

        return v;
    }


    public Set<FriendEntity> getSelectedItems(){
        return selectedFriends;
    }


    public void setItems(List<FriendEntity> items) {
        this.allFriends = items;
        this.filteredFriends = items;
        super.clear();
        super.addAll(items);
    }


    @Override
    public Filter getFilter() {
        return filter;
    }


    @Override
    public int getCount() {
        return filteredFriends.size();
    }


    @Override
    public FriendEntity getItem(int position) {
        return filteredFriends.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }
}
