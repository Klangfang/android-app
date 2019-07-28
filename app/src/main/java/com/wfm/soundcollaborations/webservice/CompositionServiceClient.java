package com.wfm.soundcollaborations.webservice;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wfm.soundcollaborations.Editor.model.composition.Composition;
import com.wfm.soundcollaborations.Editor.model.composition.Sound;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CompositionServiceClient {

    private RequestQueue queue;

    private static String BASE_URL = HttpUtils.COMPOSITION_SERVICE_BASE_URL;
    private static String PICK_URL = HttpUtils.COMPOSITION_PICK_URL;
    private static String RELEASE_URL = HttpUtils.COMPOSITION_RELEASE_URL;
    private static String VIEW_URL = HttpUtils.COMPOSITION_VIEW_URL;

    private static final int POST = Request.Method.POST;
    private static final int PUT = Request.Method.PUT;
    private static final int GET = Request.Method.GET;


    public CompositionServiceClient(Context context) {

        // Instantiate the RequestQueue.
        this.queue = Volley.newRequestQueue(context);

    }


    public void pick(String url, Response.Listener<String> listener) {

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(PUT, url,
                listener, error -> {
            //"That didn't work!"
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


    public void release(Long id) {

        String url = BASE_URL + id + RELEASE_URL;
        StringRequest stringRequest = new StringRequest(PUT, url, null, error -> {});
        queue.add(stringRequest);

    }


    public void getOverviews(Response.Listener<String> listener) {

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(GET, VIEW_URL,
                listener, error -> {
            //"That didn't work!"
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


    public void release(Long compositionId, List<Sound> recordedSounds, Response.Listener<JSONArray> listener) {

        try {
            String url = BASE_URL + compositionId + RELEASE_URL;
            String json = JsonUtil.toJson(recordedSounds);
            JSONArray jsonArray = new JSONArray(json);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(PUT, url, jsonArray, listener, error -> {});
            queue.add(jsonArrayRequest);
        } catch (JSONException e) {
            System.err.println(e);
        }


    }


    public void create(Composition composition, Response.Listener<JSONObject> listener) {

        try {
            String url = BASE_URL;
            String json = JsonUtil.toJson(composition);
            JSONObject jsonObject = new JSONObject(json);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(POST, url, jsonObject, listener, error -> {});
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            System.err.println(e);
        }
    }

}
