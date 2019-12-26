package com.wfm.soundcollaborations.webservice;


import com.android.volley.Response;
import com.wfm.soundcollaborations.Editor.model.composition.sound.LocalSound;
import com.wfm.soundcollaborations.webservice.dtos.CompositionOverviewResp;
import com.wfm.soundcollaborations.webservice.dtos.CompositionRequest;
import com.wfm.soundcollaborations.webservice.dtos.CompositionResponse;
import com.wfm.soundcollaborations.webservice.dtos.CompositionUpdateRequest;
import com.wfm.soundcollaborations.webservice.dtos.OverviewResponse;

import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompositionServiceClient {

    private CompositionService service;


    public CompositionServiceClient() {

        // Instantiate Composition
        service = new Retrofit.Builder()
                .baseUrl("https://klangfang-service.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CompositionService.class);


    }


    public void create(String compositionTitle,
                       String creatorName,
                       Stream<LocalSound> recordedSounds,
                       Response.Listener<CompositionOverviewResp> listener) {

        service.create(CompositionRequest.build(compositionTitle, creatorName, recordedSounds))
                .enqueue(new Callback<CompositionOverviewResp>() {
                    @Override
                    public void onResponse(@NotNull Call<CompositionOverviewResp> call,
                                           @NotNull retrofit2.Response<CompositionOverviewResp> response) {

                        CompositionOverviewResp compositionOverviewResp = response.body();
                        listener.onResponse(compositionOverviewResp);

                    }

                    @Override
                    public void onFailure(@NotNull Call<CompositionOverviewResp> call, @NotNull Throwable t) {
                        //Handle failure
                    }
                });

    }


    public void open(Long compositionId,
                     Response.Listener<CompositionResponse> listener) {

        service.open(compositionId, CompositionUpdateRequest.build())
                .enqueue(new Callback<CompositionResponse>() {
                    @Override
                    public void onResponse(Call<CompositionResponse> call,
                                           retrofit2.Response<CompositionResponse> response) {

                        CompositionResponse compositionResponse = response.body();
                        listener.onResponse(compositionResponse);

                    }

                    @Override
                    public void onFailure(Call<CompositionResponse> call, Throwable t) {
                        //Handle failure
                    }
                });

    }

    public void join(Long compositionId,
                     Stream<LocalSound> recordedSounds,
                     Response.Listener<CompositionResponse> listener) {

        service.join(compositionId, CompositionUpdateRequest.build(recordedSounds))
                .enqueue(new Callback<CompositionResponse>() {
                    @Override
                    public void onResponse(Call<CompositionResponse> call,
                                           retrofit2.Response<CompositionResponse> response) {

                        CompositionResponse compositionResponse = response.body();
                        listener.onResponse(compositionResponse);

                    }

                    @Override
                    public void onFailure(Call<CompositionResponse> call, Throwable t) {
                        //Handle failure
                    }
                });

    }

    public void cancel(Long compositionId,
                       Response.Listener<CompositionResponse> listener) {

        service.cancel(compositionId, CompositionUpdateRequest.build())
                .enqueue(new Callback<CompositionResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CompositionResponse> call,
                                           @NotNull retrofit2.Response<CompositionResponse> response) {

                        CompositionResponse compositionResponse = response.body();
                        listener.onResponse(compositionResponse);

                    }

                    @Override
                    public void onFailure(@NotNull Call<CompositionResponse> call, @NotNull Throwable t) {
                        //Handle failure
                    }
                });

    }


    public void getOverviews(Response.Listener<OverviewResponse> listener) {

        service.getOverviews()
                .enqueue(new Callback<OverviewResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<OverviewResponse> call,
                                           @NotNull retrofit2.Response<OverviewResponse> response) {

                        OverviewResponse overviewResponse = response.body();
                        listener.onResponse(overviewResponse);

                    }

                    @Override
                    public void onFailure(@NotNull Call<OverviewResponse> call, @NotNull Throwable t) {
                        //Handle failure
                    }
                });

    }

}
