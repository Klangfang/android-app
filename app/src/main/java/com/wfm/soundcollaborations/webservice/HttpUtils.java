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
import com.wfm.soundcollaborations.Editor.model.composition.CompositionResponse;
import com.wfm.soundcollaborations.Editor.model.composition.OverviewResponse;
import com.wfm.soundcollaborations.Editor.model.composition.Sound;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    public static OverviewResponse getCompositionOverviews(String jsonResponse) {
        Set<CompositionOverview> compositionOverviews = new HashSet<>();
        try {
            JSONObject response = new JSONObject(jsonResponse);
            JSONArray overviews = response.getJSONArray("overviews");
            for (int i = 0; i < overviews.length(); ++i) {
                final JSONObject compositionOverview = overviews.getJSONObject(i);
                String title = compositionOverview.getString("title");
                int numberOfParticipation = compositionOverview.getInt("numberOfMembers");
                String snippetUrl = compositionOverview.getString("snippetUrl");
                String pickUrl = compositionOverview.getString("pickUrl");
                compositionOverviews.add(new CompositionOverview(title, numberOfParticipation, snippetUrl, pickUrl));
            }
            return new OverviewResponse(compositionOverviews, response.getString("nextPage"));
        } catch (JSONException e) {
            return null;
        }
    }

    public static CompositionResponse getComposition(String jsonResponse) {
        //jsonResponse = jsonResponse.replaceAll("\n", "");
        Composition composition = null;
        try {
            JSONObject response = new JSONObject(jsonResponse);
            JSONObject compositionResponse = response.getJSONObject("composition");
            String compositionTitle = compositionResponse.getString("title");
            String compositionCreatorName = compositionResponse.getString("creatorName");
            String creationDate = compositionResponse.getString("creationDate");
            String status = compositionResponse.getString("status");
            int numberOfParticipants = compositionResponse.getInt("numberOfMembers");
            JSONArray jsonSounds = compositionResponse.getJSONArray("sounds");
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

            return new CompositionResponse(composition, response.getString("pickUrl"));

        } catch (JSONException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
