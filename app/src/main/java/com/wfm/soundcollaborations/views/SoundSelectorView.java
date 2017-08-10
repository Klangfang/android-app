package com.wfm.soundcollaborations.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.database.DatabaseHelper;
import com.wfm.soundcollaborations.database.SoundEntity;
import com.wfm.soundcollaborations.database.TagEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

/**
 * Created by Markus Eberts on 19.10.16.
 */
public class SoundSelectorView extends LinearLayout {
    private final static String TAG = SoundSelectorView.class.getSimpleName();

    private final float scale = 1.5f;

    private SoundEntity selectedSound;

    // UI
    private TextView tvSoundName;

    // Database
    private DatabaseHelper databaseHelper;
    private Dao<SoundEntity, Long> soundDao;
    private Dao<TagEntity, Long> tagDao;

    public SoundSelectorView(Context context) {
        super(context);
        createLayout();
        try {
            initDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SoundSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createLayout();
        try {
            initDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (width > (int)((scale * height) + 0.5)) {
            width = (int)((scale * height) + 0.5);
        } else {
            height = (int)((width / scale) + 0.5);
        }

        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    private void createLayout(){
        View.inflate(getContext(), R.layout.sound_selector, this);
        tvSoundName = (TextView) findViewById(R.id.tv_sound_name);

        final Random randomGen = new Random();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.sound_choice);

                ListView lvSounds = (ListView) dialog.findViewById(R.id.lv_sounds);

                final List<SoundEntity> sounds;

                try {
                    sounds = soundDao.queryForAll();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return;
                }

                String[] soundNames = new String[sounds.size()];
                for (int i = 0; i < sounds.size(); i++){
                    soundNames[i] = sounds.get(i).getName();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_list_item_1, soundNames);
                lvSounds.setAdapter(adapter);

                lvSounds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedSound = sounds.get(position);
                        tvSoundName.setText(selectedSound.getName());
                        setBackgroundColor(Color.rgb(randomGen.nextInt(256), randomGen.nextInt(256), randomGen.nextInt(256)));
                        dialog.cancel();
                    }
                });

                dialog.setCancelable(true);
                dialog.setTitle("Wähle einen Sound");
                dialog.show();
            }
        });
    }

    public void setColor(int color){
        setBackgroundColor(color);
    }

    private void initDB() throws SQLException {
        databaseHelper = OpenHelperManager.getHelper(getContext(),
                DatabaseHelper.class);

        soundDao = databaseHelper.getSoundDao();
        tagDao = databaseHelper.getTagDao();
    }
}
