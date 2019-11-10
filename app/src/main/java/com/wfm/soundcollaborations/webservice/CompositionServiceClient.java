package com.wfm.soundcollaborations.webservice;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wfm.soundcollaborations.webservice.dtos.CompositionOverviewResp;
import com.wfm.soundcollaborations.webservice.dtos.CompositionRequest;
import com.wfm.soundcollaborations.Editor.model.composition.Sound;
import com.wfm.soundcollaborations.webservice.dtos.CompositionResponse;
import com.wfm.soundcollaborations.webservice.dtos.CompositionUpdateRequest;
import com.wfm.soundcollaborations.webservice.dtos.SoundRequest;

import org.json.JSONObject;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompositionServiceClient {

    public static final MediaType JSON
            = MediaType.get("multipart/mixed");

    private RequestQueue queue;

    private CompositionService service;

    private static String BASE_URL = HttpUtils.COMPOSITION_SERVICE_BASE_URL;
    private static String VIEW_URL = HttpUtils.COMPOSITION_VIEW_URL;

    private static final int POST = Request.Method.POST;
    private static final int PUT = Request.Method.PUT;
    private static final int GET = Request.Method.GET;


    public CompositionServiceClient(Context context) {

        // Instantiate the RequestQueue.
        this.queue = Volley.newRequestQueue(context);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://klangfang-service.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Instantiate CompositionService
        service = new Retrofit.Builder()
                .baseUrl("https://klangfang-service.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CompositionService.class);


    }


    public void create(CompositionRequest compositionRequest, Response.Listener<JSONObject> listener) {

        Call<CompositionOverviewResp> compositionOverviewCall = service.createComposition(compositionRequest);
        compositionOverviewCall.enqueue(new Callback<CompositionOverviewResp>() {
            @Override
            public void onResponse(Call<CompositionOverviewResp> call, retrofit2.Response<CompositionOverviewResp> response) {

                CompositionOverviewResp compositionOverviewResp = response.body();

            }

            @Override
            public void onFailure(Call<CompositionOverviewResp> call, Throwable t) {
                //Handle failure
            }
        });

    }


    public void update(Long compositionId, CompositionUpdateRequest compositionUpdateRequest, Response.Listener<CompositionResponse> listener) {

        Call<CompositionResponse> compositionOverviewCall = service.updateComposition(compositionId, compositionUpdateRequest   );
        compositionOverviewCall.enqueue(new Callback<CompositionResponse>() {
            @Override
            public void onResponse(Call<CompositionResponse> call, retrofit2.Response<CompositionResponse> response) {

                CompositionResponse compositionResponse = response.body();
                listener.onResponse(compositionResponse);

            }

            @Override
            public void onFailure(Call<CompositionResponse> call, Throwable t) {
                //Handle failure
            }
        });

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

}
