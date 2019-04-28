package com.wfm.soundcollaborations.Editor.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wfm.soundcollaborations.Editor.network.DownloadCallback;
import com.wfm.soundcollaborations.Editor.network.NetworkFragment;
import com.wfm.soundcollaborations.R;

import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class CreateCompositionActivity extends AppCompatActivity implements DownloadCallback {

    public static final String COMPOSITION_RESPONSE = "CompositionResponse";

    private View view;

    // Keep a reference to the NetworkFragment, which owns the AsyncTask object
    // that is used to execute network ops.
    private NetworkFragment networkFragment;

    private ConnectivityManager connectivityManager;

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private boolean downloading = false;

    private String URL = "https://klangfang-service.herokuapp.com/compositions/19/pick";

    // If Activity starts, following onCreate function will be executed.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_composition);

        setSupportActionBar(findViewById(R.id.create_composition_toolbar));

        //init attributes needed to consume the compositionservice api
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), URL);

        // When user taps confirm button
        // Capture button from layout to add functionality
        Button testButtonId = findViewById(R.id.testButtonId);
        testButtonId.setOnClickListener(view -> startDownload(view));

    }
    // Add Menu to Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_composition_menu, menu);
        return true;
    }

    public void startDownload(View view) {
        if (!downloading && networkFragment != null) {
            this.view = view;
            // Execute the async download.
            networkFragment.startDownload(view.getContext());
            downloading = true;
        }
    }

    @Override
    public void updateFromDownload(Object result) {
        // Start Editor Activity based on result of download.

        // First parameter: context
        // Second Parameter: Activity we want to start after tapping confirm item
        Intent intent = new Intent(view.getContext(), EditorActivity.class);
        intent.putExtra(COMPOSITION_RESPONSE, result.toString());
        // Start an instance of the DisplayMessageActivity specified by the Intent
        view.getContext().startActivity(intent);
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode) {
            // You can add UI behavior for progress updates here.
            case DownloadCallback.Progress.ERROR:
                //TODO
                break;
            case DownloadCallback.Progress.CONNECT_SUCCESS:
                //TODO
                break;
            case DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS:
                //TODO
                break;
            case DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                //TODO
                break;
            case DownloadCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS:
                //TODO
                break;
        }
    }

    @Override
    public void finishDownloading() {
        downloading = false;
        if (networkFragment != null) {
            networkFragment.cancelDownload();
        }
    }
}
