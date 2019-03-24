package com.wfm.soundcollaborations.webservice;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteCallbackList;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.wfm.soundcollaborations.Editor.model.composition.Sound;
import com.wfm.soundcollaborations.Editor.model.composition.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.app.DownloadManager.STATUS_RUNNING;

public class CompositionService extends IntentService {


    public CompositionService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

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
}
