package com.wfm.soundcollaborations.interaction.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wfm.soundcollaborations.KlangfangApp;
import com.wfm.soundcollaborations.KlangfangSnackbar;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.interaction.editor.activities.CreateCompositionActivity;
import com.wfm.soundcollaborations.interaction.editor.activities.EditorActivity;
import com.wfm.soundcollaborations.interaction.main.fragments.ComposeFragment;
import com.wfm.soundcollaborations.interaction.main.fragments.ExploreFragment;
import com.wfm.soundcollaborations.webservice.JsonUtil;
import com.wfm.soundcollaborations.webservice.dtos.CompositionResponse;

import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    public static final String PICK_RESPONSE = "PICK";


    @BindView(R.id.base_toolbar)
    Toolbar base_toolbar;

    @BindView(R.id.bnv_bottom_navigation)
    BottomNavigationView bottomNavigationView;

    public static final int UPDATE_CODE = 0;

    public MainComponent mainComponent;

    @Inject
    ComposeFragment composeFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initToolbar();
        initBottomNavigationView();

        showResultMessage(getIntent());

        mainComponent = ((KlangfangApp) getApplicationContext())
                .appComponent
                .mainComponent()
                .create();

        mainComponent.inject(this);

        prepareComposeFragment();

    }


    @Override
    public void onBackPressed() {

        // no history here
        finish();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_CODE && resultCode == RESULT_OK) {

            showResultMessage(data);

            prepareComposeFragment();

        }

    }


    private void initToolbar() {

        setSupportActionBar(base_toolbar);

    }


    private void initBottomNavigationView() {

        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.bnm_explore:
                            startExploreFragment();
                            break;
                        case R.id.bnm_compose:
                            prepareComposeFragment();
                            break;
                        case R.id.bnm_record:
                            break;
                    }
                    return true;
                });

    }


    private void startExploreFragment(){

        ExploreFragment exploreFragment = new ExploreFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_content, exploreFragment);
        transaction.commit();

    }


    private void prepareComposeFragment() {

        composeFragment.setCallbacks(this::startEditorActivity,
                this::startCreateCompositionActivity);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_content, composeFragment);
        transaction.commit();

    }


    private void startEditorActivity(CompositionResponse response) {

        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra(PICK_RESPONSE, JsonUtil.toJson(response));
        startActivityForResult(intent, UPDATE_CODE);

    }


    private void startCreateCompositionActivity(View view) {

        Intent intent = new Intent(view.getContext(), CreateCompositionActivity.class);
        view.getContext().startActivity(intent);

    }


    private void showResultMessage(Intent data) {

        String messageText = data.getStringExtra(EditorActivity.MESSAGE_TEXT);
        if (StringUtils.isNoneBlank(messageText)) {
            KlangfangSnackbar.longShow(base_toolbar, messageText);
        }

    }

    public Toolbar getToolbar() {

        return base_toolbar;

    }

}