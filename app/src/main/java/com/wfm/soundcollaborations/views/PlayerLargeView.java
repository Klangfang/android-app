package com.wfm.soundcollaborations.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.sound.SoundPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Markus Eberts on 10.10.16.
 */
public class PlayerLargeView extends LinearLayout {
    private final static String TAG = PlayerLargeView.class.getSimpleName();

    @BindView(R.id.tv_sound_name)
    TextView tvSoundName;
    @BindView(R.id.sb_player_timeline)
    SeekBar sbTimeline;
    @BindView(R.id.tv_player_creation_date)
    TextView tvCreationDate;
    @BindView(R.id.tv_player_time)
    TextView tvTime;
    @BindView(R.id.ib_player_state)
    ImageButton ibState;
    @BindView(R.id.visualization)
    SoundVisualizationView visualization;

    private boolean playing;
    private SoundPlayer soundPlayer;

    private String soundFileUri;

    public PlayerLargeView(Context context)
    {
        super(context);
        View.inflate(getContext(), R.layout.player_large, this);
        ButterKnife.bind(this);
    }

    public PlayerLargeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(getContext(), R.layout.player_large, this);
        ButterKnife.bind(this);
    }

    public void play(String soundFileUri)
    {
        this.soundFileUri = soundFileUri;
        initPlayer();
    }



    private void initPlayer()
    {

    }

}
