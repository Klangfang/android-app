package com.wfm.soundcollaborations;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wfm.soundcollaborations.editor.activities.EditorActivity;
import com.wfm.soundcollaborations.fragments.ComposeFragment;
import com.wfm.soundcollaborations.fragments.ExploreFragment;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.base_toolbar)
    Toolbar base_toolbar;

    @BindView(R.id.bnv_bottom_navigation)
    BottomNavigationView bottomNavigationView;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initToolbar();
        initBottomNavigationView();
        startComposeFragment();

        Bundle bundle = getIntent().getExtras();
        if (Objects.nonNull(bundle)) {
            String messageText = bundle.getString(EditorActivity.MESSAGE_TEXT);
            if (StringUtils.isNoneBlank(messageText)) {
                KlangfangSnackbar.longShow(base_toolbar, messageText);
            }

            getIntent().removeExtra(EditorActivity.MESSAGE_TEXT);

        }

    }


    public void onBackPressed() {

        // no history here
        finish();

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
                            startComposeFragment();
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


    private void startComposeFragment(){

        ComposeFragment composeFragment = new ComposeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_content, composeFragment);
        transaction.commit();

    }


    public Toolbar getToolbar() {

        return base_toolbar;

    }

}
