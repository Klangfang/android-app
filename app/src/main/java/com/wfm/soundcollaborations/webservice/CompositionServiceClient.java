package com.wfm.soundcollaborations.webservice;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wfm.soundcollaborations.Editor.model.composition.Composition;
import com.wfm.soundcollaborations.Editor.model.composition.CompositionOverview;
import com.wfm.soundcollaborations.Editor.model.composition.Sound;

import org.json.JSONObject;

import java.io.File;

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


    public void release(Long compositionId, List<Sound> recordedSounds, Response.Listener<String> listener) {



    }


    public void create(Composition composition, Response.Listener<JSONObject> listener) {


        List<File> compositionJsonFiles = JsonUtil.toJsonFile("composition", composition);
        File compositionJsonFile = compositionJsonFiles.get(0);
        MultipartBody.Part compositionBody = MultipartBody.Part.createFormData("composition", compositionJsonFile.getName(), RequestBody.create(compositionJsonFile, MediaType.get("application/json")));
        List<MultipartBody.Part> filesBodies = composition.sounds.stream()
                .map(sound -> {

                        File file = new File(sound.filePath);
                        return MultipartBody.Part.createFormData("files[]", file.getName(), RequestBody.create(file, MediaType.parse("audio/*")));
                })
                .collect(Collectors.toList());

        OkHttpClient client = new OkHttpClient();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://klangfang-service.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        CompositionService service = retrofit.create(CompositionService.class);
        Call<CompositionOverview> compositionOverviewCall = service.createComposition(compositionBody, filesBodies.toArray(new MultipartBody.Part[filesBodies.size()]));
        compositionOverviewCall.enqueue(new Callback<CompositionOverview>() {
            @Override
            public void onResponse(Call<CompositionOverview> call, retrofit2.Response<CompositionOverview> response) {

                CompositionOverview compositionOverview = response.body();

            }

            @Override
            public void onFailure(Call<CompositionOverview> call, Throwable t) {
                //Handle failure
            }
        });

    }

}
