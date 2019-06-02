package com.wfm.soundcollaborations.webservice;


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
import java.util.Set;

/**
 * Http Utility methods that handle the interactive with the rest webservice api
 */
public class HttpUtils {


    public static final String COMPOSITION_VIEW_URL = "https://klangfang-service.herokuapp.com/compositions/compositionsOverview?page=0&size=5";
    public static final String COMPOSITION_PICK_URL = "https://klangfang-service.herokuapp.com/compositions/1/pick";
    public static final String COMPOSITION_RELEASE_URL = "https://klangfang-service.herokuapp.com/compositions/1/release";

    private static SyncHttpClient client = new SyncHttpClient();


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

    public static OverviewResponse getCompositionOverview(String jsonResponse) {

        Set<CompositionOverview> compositionOverviews = new HashSet<>();
        try {
            JSONObject compositionOverview = new JSONObject(jsonResponse);
            String title = compositionOverview.getString("title");
            int numberOfParticipation = compositionOverview.getInt("numberOfMembers");
            String snippetUrl = compositionOverview.getString("snippetUrl");
            String pickUrl = compositionOverview.getString("pickUrl");
            compositionOverviews.add(new CompositionOverview(title, numberOfParticipation, snippetUrl, pickUrl));

            return new OverviewResponse(compositionOverviews, null);
        } catch (JSONException e) {
            return null;
        }
    }
}
