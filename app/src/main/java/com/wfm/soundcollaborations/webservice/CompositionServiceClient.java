package com.wfm.soundcollaborations.webservice;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.wfm.soundcollaborations.webservice.dtos.CompositionOverviewResp;
import com.wfm.soundcollaborations.webservice.dtos.CompositionRequest;
import com.wfm.soundcollaborations.webservice.dtos.CompositionResponse;
import com.wfm.soundcollaborations.webservice.dtos.CompositionUpdateRequest;
import com.wfm.soundcollaborations.webservice.dtos.OverviewResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompositionServiceClient {

    private CompositionService service;

    private static String BASE_URL = HttpUtils.COMPOSITION_SERVICE_BASE_URL;
    private static String VIEW_URL = HttpUtils.COMPOSITION_VIEW_URL;

    private static final int POST = Request.Method.POST;
    private static final int PUT = Request.Method.PUT;
    private static final int GET = Request.Method.GET;


    public CompositionServiceClient(Context context) {

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


    public void create(CompositionRequest compositionRequest, Response.Listener<CompositionOverviewResp> listener) {

        Call<CompositionOverviewResp> compositionOverviewCall = service.createComposition(compositionRequest);
        compositionOverviewCall.enqueue(new Callback<CompositionOverviewResp>() {
            @Override
            public void onResponse(Call<CompositionOverviewResp> call, retrofit2.Response<CompositionOverviewResp> response) {

                CompositionOverviewResp compositionOverviewResp = response.body();
                listener.onResponse(compositionOverviewResp);

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


    public void getOverviews(Response.Listener<OverviewResponse> listener) {

        Call<OverviewResponse> compositionOverviewCall = service.getOverviews();
        compositionOverviewCall.enqueue(new Callback<OverviewResponse>() {
            @Override
            public void onResponse(Call<OverviewResponse> call, retrofit2.Response<OverviewResponse> response) {

                OverviewResponse overviewResponse = response.body();
                listener.onResponse(overviewResponse);

            }

            @Override
            public void onFailure(Call<OverviewResponse> call, Throwable t) {
                //Handle failure
            }
        });

    }

}
