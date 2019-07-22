package com.wfm.soundcollaborations.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.activities.SaveSoundActivity;
import com.wfm.soundcollaborations.database.SoundEntity;
import com.wfm.soundcollaborations.fragments.MySoundsFragment;
import com.wfm.soundcollaborations.views.PlayerSmallView;

import java.util.List;

/**
 * Created by Markus Eberts on 13.10.16.
 */
public class SoundListAdapter extends ArrayAdapter<SoundEntity> {

    MySoundsFragment fragment;

    public SoundListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public SoundListAdapter(Context context, int resource, List<SoundEntity> items) {
        super(context, resource, items);
    }

    public SoundListAdapter(MySoundsFragment fragment, int resource, List<SoundEntity> items) {
        super(fragment.getContext(), resource, items);
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.sound_row, null);
        }

        final SoundEntity sound = getItem(position);

        if (sound != null) {
            PlayerSmallView player = (PlayerSmallView) v.findViewById(R.id.sound_player);
            player.play(sound.getId());

            // Edit sound
            ImageButton btnEdit = (ImageButton) v.findViewById(R.id.ib_edit_sound);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), SaveSoundActivity.class);
                    intent.putExtra("soundID", sound.getId());
                    getContext().startActivity(intent);
                }
            });

//            // Delete sound
//            ImageButton soundEdit = (ImageButton) v.findViewById(R.id.ib_delete_sound);
//            soundEdit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    fragment.deleteSound(sound.getId());
//                }
//            });
        }

        return v;
    }

}