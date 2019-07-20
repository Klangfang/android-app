package com.wfm.soundcollaborations.common;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wfm.soundcollaborations.Editor.activities.EditorActivity;
import com.wfm.soundcollaborations.Editor.network.DownloadCallback;
import com.wfm.soundcollaborations.Editor.network.NetworkFragment;
import com.wfm.soundcollaborations.webservice.HttpUtils;

public class NetworkActivity extends AppCompatActivity implements DownloadCallback {

    private View view;

    // Keep a reference to the NetworkFragment, which owns the AsyncTask object
    // that is used to execute network ops.
    private NetworkFragment networkFragment;

    private ConnectivityManager connectivityManager;

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private boolean downloading = false;

    private final String COMPOSITION_VIEW_URL = "https://klangfang-service.herokuapp.com/compositions/compositionsOverview?page=0&size=5";
    private final String COMPOSITION_PICK_URL = "https://klangfang-service.herokuapp.com/compositions/1/pick";

    public static final String COMPOSITION_RESPONSE = "PickResponse";

    public void initNetworking(String serviceUrl) {

        if (networkFragment != null) {
            networkFragment.cancelDownload();
        }
        //init attributes needed to consume the compositionservice api
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), serviceUrl);
    }

    public void startDownload(View view) {
        if (!downloading && networkFragment != null) {
            this.view = view;
            // Execute the async download.
            networkFragment.startDownload(view.getContext(), HttpUtils.COMPOSITION_PICK_URL);
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
        // CreateCompositionActivity.this.startActivity(intent);
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
