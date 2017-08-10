package com.wfm.soundcollaborations.activities;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.support.v7.widget.SwitchCompat;
import android.widget.TextView;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.database.SoundEntity;
import com.wfm.soundcollaborations.database.TagEntity;
import com.wfm.soundcollaborations.fragments.HashtagChoiceFragment;
import com.wfm.soundcollaborations.fragments.interfaces.FragmentObserver;
import com.wfm.soundcollaborations.helper.Utility;
import com.wfm.soundcollaborations.views.PlayerLargeView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Markus Eberts on 09.10.16.
 */
public class SoundEditorActivity extends BaseActivity implements FragmentObserver{
    private final static String TAG = SoundEditorActivity.class.getSimpleName();

    // UI
    private EditText etSoundName;
    private SwitchCompat swDate;
    private SwitchCompat swGeolocation;
    private SwitchCompat swProfileSound;
    private RelativeLayout rlHashtags;
    private PlayerLargeView soundPlayerView;
    private TextView tvHashtags;
    private Button btnDelete;

    private SoundEntity soundEntity;
    private String fileURI;
    private long soundID;

    private Set<String> selectedHashtags;

    private List<Integer> amplitudes;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sound_editor);

        fileURI = getIntent().getExtras().getString("fileURI", null);
        soundID = getIntent().getExtras().getLong("soundID", -1);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.grey_light), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        selectedHashtags = new HashSet<>();
        amplitudes = new ArrayList<>();

        // UI
        etSoundName = (EditText) findViewById(R.id.et_sound_name);
        swDate = (SwitchCompat) findViewById(R.id.sw_date);
        swGeolocation = (SwitchCompat) findViewById(R.id.sw_geolocation);
        swProfileSound = (SwitchCompat) findViewById(R.id.sw_account_sound);
        rlHashtags = (RelativeLayout) findViewById(R.id.rl_hashtags);
        tvHashtags = (TextView) findViewById(R.id.tv_hashtags);
        soundPlayerView = (PlayerLargeView) findViewById(R.id.sound_player);
        btnDelete = (Button) findViewById(R.id.btn_delete);


        if (soundID != -1){
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        soundDao.deleteById(soundID);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return;
                    }

                    if (preferences.getLong(MainActivity.KEY_ACCOUNT_SOUND, -1) == soundEntity.getId()){
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove(MainActivity.KEY_ACCOUNT_SOUND);
                        editor.commit();
                    }

                    finish();
                }
            });
        } else {
            btnDelete.setVisibility(View.GONE);
        }

        rlHashtags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashtagChoiceFragment fragment = new HashtagChoiceFragment();
                fragment.setObserver(SoundEditorActivity.this);

                ArrayList<String> hashtags = new ArrayList<>();
                Collections.addAll(hashtags, getResources().getStringArray(R.array.hashtags));

                Bundle args = new Bundle();
                args.putStringArrayList("items", hashtags);

                if (!selectedHashtags.isEmpty()){
                    ArrayList<String> selectedHashtagsList = new ArrayList<>();
                    selectedHashtagsList.addAll(selectedHashtags);
                    args.putStringArrayList("selected", selectedHashtagsList);
                }

                fragment.setArguments(args);
                fragment.show(getSupportFragmentManager(), "hashtag choice");
            }
        });

        if (preferences.contains(MainActivity.KEY_ACCOUNT_SOUND)){
            long accountSoundID = preferences.getLong(MainActivity.KEY_ACCOUNT_SOUND, -1);

            if (soundID == accountSoundID){
                swProfileSound.setChecked(true);
            }
        }

        if (fileURI != null ) {
            soundPlayerView.play(fileURI);

            int[] as = getIntent().getExtras().getIntArray("amplitudes");
            for (int a : as){
                amplitudes.add(a);
            }

            soundPlayerView.visualize(amplitudes);
        } else {
            soundPlayerView.play(soundID);

            List<TagEntity> tagEntries;
            try {
                soundEntity = soundDao.queryForId(soundID);
                tagEntries = tagDao.queryForEq("sound_id", soundID);
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }

            fileURI = soundEntity.getFileURI();

            toolbar.setTitle(soundEntity.getName());
            etSoundName.setText(soundEntity.getName());
            swDate.setChecked(soundEntity.getCreationDate() != null);
            swGeolocation.setChecked(soundEntity.getLatitude() != 0 && soundEntity.getLongitude() != 0); // TODO fix
            swDate.setEnabled(false);
            swGeolocation.setEnabled(false);

            for (TagEntity tagEntity : tagEntries){
                selectedHashtags.add(tagEntity.getName());
            }


            updateHashtags();
        }
    }

    private void updateHashtags(){
        String hashtagString = "# ";
        List<String> selectedHashtagsList = new ArrayList<>();
        selectedHashtagsList.addAll(selectedHashtags);
        Collections.sort(selectedHashtagsList);
        hashtagString += Utility.join(selectedHashtagsList, ", ");
        tvHashtags.setText(hashtagString);
    }

    private void saveSound(){
        if (soundEntity == null){
            soundEntity = new SoundEntity(etSoundName.getText().toString(), fileURI);
        } else {
            soundEntity.setName(etSoundName.getText().toString());
        }

        if (!amplitudes.isEmpty()) {
            soundEntity.setAmplitudes((ArrayList<Integer>) amplitudes);
        }

        try {
            updateSoundEntry(soundEntity, selectedHashtags,
                    swDate.isChecked(), swGeolocation.isChecked());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (swProfileSound.isChecked()){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(MainActivity.KEY_ACCOUNT_SOUND, soundEntity.getId());
            editor.commit();
        } else if (preferences.getLong(MainActivity.KEY_ACCOUNT_SOUND, -1) == soundEntity.getId()){
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(MainActivity.KEY_ACCOUNT_SOUND);
            editor.commit();
        }

        finish();
    }

    private void updateSoundEntry(SoundEntity sound, Set<String> tags, boolean saveDate, boolean saveGeolocation) throws SQLException {
        Log.i(TAG, "Update soundEntity entry");

        if (saveDate){
            sound.setCreationDate(new Date(System.currentTimeMillis()));
        }

        if (saveGeolocation){
            // TODO Get and save geo location
        }

        soundDao.createOrUpdate(sound);

        if (soundID != -1) {
            List<TagEntity> currentEntries = tagDao.queryForEq("sound_id", soundID);
            tagDao.delete(currentEntries);
        }

        for (String tag : tags){
            tagDao.create(new TagEntity(tag, sound));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sound_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.btn_save_sound:
                saveSound();

        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void fragmentUpdate(Event event, Bundle args) {
        String[] hashtags = args.getStringArray("items");
        selectedHashtags.clear();
        Collections.addAll(selectedHashtags, hashtags);
        updateHashtags();
    }
}
