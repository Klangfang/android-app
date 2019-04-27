package com.wfm.soundcollaborations.webservice;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteCallbackList;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.wfm.soundcollaborations.Editor.model.composition.CompositionBuilder;
import com.wfm.soundcollaborations.Editor.model.composition.Sound;
import com.wfm.soundcollaborations.Editor.model.composition.Track;
import com.wfm.soundcollaborations.Editor.network.DownloadCallback;
import com.wfm.soundcollaborations.Editor.network.NetworkFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import cz.msebera.android.httpclient.Header;

import static android.app.DownloadManager.STATUS_RUNNING;

/**
 * TODO not used, delete!
 * Implementation of AsyncTask designed to fetch data from the composition service via network.
 */
public class CompositionService implements DownloadCallback {

    // Keep a reference to the NetworkFragment, which owns the AsyncTask object
    // that is used to execute network ops.
    private NetworkFragment networkFragment;

    private ConnectivityManager connectivityManager;

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private boolean downloading = false;

    private String URL = "http://localhost:5000/compositions/compositionsOverview?page=0&size=5";

    private CompositionBuilder compositionBuilder;

    public CompositionService(FragmentManager fragmentManager, ConnectivityManager connectivityManager, CompositionBuilder compositionBuilder) {
        networkFragment = NetworkFragment.getInstance(fragmentManager, URL);
        this.connectivityManager = connectivityManager;
        this.compositionBuilder = compositionBuilder;
    }

    protected void wip(@Nullable Intent intent) {

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String command = intent.getStringExtra("command");
        Bundle bundle = new Bundle();

        if (command.equals("query")) {
            //TODO
            HttpUtils.get(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    //Log.d("asd", "---------------- this is response : " + response);
                    // Pull out the first event on the public timeline
                    JSONArray tracksJson = null;
                    try {
                        tracksJson = response.getJSONArray("tracks");
                    List<Track> tracks = new ArrayList<>();
                    for(int i=0; i<tracksJson.length(); i++) {
                        JSONArray soundArray = tracksJson.getJSONArray(i);
                        Track track = new Track();
                        for(int j=0; j<tracksJson.length(); j++) {
                            JSONObject soundObj = soundArray.getJSONObject(j);
                            Sound sound = new Sound(
                                    "http://localhost:5000/compositions/" + soundObj.getString("filename") + "?compositionId=1",
                                    soundObj.getInt("durationInMs"),
                                    soundObj.getInt("startPositionInMs"));
                            track.addSound(sound);
                        }
                        tracks.add(track);
                    }

                    bundle.putParcelableArray("result", (Parcelable[]) tracks.toArray());
                    receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    public void startDownload() {
        if (!downloading && networkFragment != null) {
            // Execute the async download.
            networkFragment.startDownload();
            downloading = true;
        }
    }

    @Override
    public void updateFromDownload(Object result) {
        // Update your UI here based on result of download.
        compositionBuilder.addSounds(null);
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
            case Progress.ERROR:
                //TODO
                break;
            case Progress.CONNECT_SUCCESS:
                //TODO
                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:
                //TODO
                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                //TODO
                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:
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
