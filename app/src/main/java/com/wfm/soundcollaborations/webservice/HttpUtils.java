package com.wfm.soundcollaborations.webservice;

import android.util.JsonReader;
import android.util.JsonWriter;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.wfm.soundcollaborations.Editor.model.composition.Composition;
import com.wfm.soundcollaborations.Editor.model.composition.CompositionOverview;
import com.wfm.soundcollaborations.Editor.model.composition.Sound;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Http Utility methods that handle the interactive with the rest webservice api
 */
public class HttpUtils {

    //private static final String BASE_URL = "https://klangfang-service.herokuapp.com/compositions/compositionsOverview?page=0&size=5";

    private static final String BASE_URL = "http://localhost/compositions/1/pick";
    private static SyncHttpClient client = new SyncHttpClient();

    public static void get(JsonHttpResponseHandler responseHandler) {
        client.get(BASE_URL, responseHandler);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static List<CompositionOverview> getCompositionOverviews(String jsonResponse) {
        List<CompositionOverview> compositionOverviews = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject embedded = jsonObject.getJSONObject("_embedded");
            JSONArray compositionOverviewDtoes = embedded.getJSONArray("compositionOverviewDtoes");
            for (int i = 0; i < compositionOverviewDtoes.length(); ++i) {
                final JSONObject compositionOverview = compositionOverviewDtoes.getJSONObject(i);
                String title = compositionOverview.getString("title");
                int numberOfParticipation = compositionOverview.getInt("numberOfMembers");
                String soundFilePath = compositionOverview.getString("snippet");
                JSONObject links = compositionOverview.getJSONObject("_links");
                String pickUrl = links.getJSONObject("pick").getString("href");
                compositionOverviews.add(new CompositionOverview(title, numberOfParticipation, soundFilePath, pickUrl));
            }
        } catch (JSONException e) {
        }
        return compositionOverviews;
    }

    public static Composition getComposition(String jsonResponse) {
        //jsonResponse = jsonResponse.replaceAll("\n", "");
        Composition composition = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            String compositionTitle = jsonObject.getString("title");
            String compositionCreatorName = jsonObject.getString("creatorName");
            String creationDate = jsonObject.getString("creationDate");
            String status = jsonObject.getString("status");
            int numberOfParticipants = jsonObject.getInt("numberOfMembers");
            JSONArray jsonSounds = jsonObject.getJSONArray("sounds");
            List<Sound> sounds = new ArrayList<>();
            for (int j = 0; j < jsonSounds.length(); j++) {
                JSONObject jsonSound = jsonSounds.getJSONObject(j);
                Integer trackNumber = jsonSound.getInt("trackNumber");
                String soundTitle = jsonSound.getString("title");
                Integer startPosition = jsonSound.getInt("startPosition");
                Integer duration = jsonSound.getInt("duration");
                String filePath = jsonSound.getString("filePath");
                String SoundCreatorName = jsonSound.getString("creatorName");
                Sound sound = new Sound(trackNumber, soundTitle, startPosition, duration, SoundCreatorName, filePath);
                sounds.add(sound);
            }
            composition = new Composition(compositionTitle, compositionCreatorName, sounds, creationDate, status,
                    numberOfParticipants);
        } catch (JSONException e) {
            System.err.println(e.getMessage());
        }
        return composition;
    }
}
