package com.wfm.soundcollaborations.webservice;


import com.wfm.soundcollaborations.Editor.model.composition.sound.LocalSound;
import com.wfm.soundcollaborations.webservice.dtos.CompositionOverviewResp;
import com.wfm.soundcollaborations.webservice.dtos.CompositionRequest;
import com.wfm.soundcollaborations.webservice.dtos.CompositionResponse;
import com.wfm.soundcollaborations.webservice.dtos.CompositionUpdateRequest;
import com.wfm.soundcollaborations.webservice.dtos.OverviewResponse;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompositionWebserviceClient {

    private final static long HTTP_TIMEOUT = 10_000;
    private CompositionWebservice service;


    public CompositionWebserviceClient() {

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .callTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();

        service = new Retrofit.Builder()
                .client(httpClient)
                .baseUrl("https://klangfang-service.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CompositionWebservice.class);

    }


    public void create(String compositionTitle,
                       String creatorName,
                       Stream<LocalSound> recordedSounds,
                       Consumer<CompositionOverviewResp> consumer) {

        service.create(CompositionRequest.build(compositionTitle, creatorName, recordedSounds))
                .enqueue(new Callback<CompositionOverviewResp>() {
                    @Override
                    public void onResponse(Call<CompositionOverviewResp> call,
                                           retrofit2.Response<CompositionOverviewResp> response) {

                        CompositionOverviewResp compositionOverviewResp = response.body();
                        consumer.accept(compositionOverviewResp);

                    }

                    @Override
                    public void onFailure(Call<CompositionOverviewResp> call, Throwable t) {
                        //Handle failure
                    }
                });

    }


    public void open(Long compositionId,
                     Consumer<CompositionResponse> consumer) {

        service.open(compositionId, CompositionUpdateRequest.build())
                .enqueue(new Callback<CompositionResponse>() {
                    @Override
                    public void onResponse(Call<CompositionResponse> call,
                                           Response<CompositionResponse> response) {

                        CompositionResponse compositionResponse = response.body();
                        consumer.accept(compositionResponse);

                    }

                    @Override
                    public void onFailure(Call<CompositionResponse> call, Throwable t) {
                        //Handle failure
                    }
                });

    }

    public void join(Long compositionId,
                     Stream<LocalSound> recordedSounds,
                     Consumer<CompositionResponse> consumer) {

        service.join(compositionId, CompositionUpdateRequest.build(recordedSounds))
                .enqueue(new Callback<CompositionResponse>() {
                    @Override
                    public void onResponse(Call<CompositionResponse> call,
                                           Response<CompositionResponse> response) {

                        CompositionResponse compositionResponse = response.body();
                        consumer.accept(compositionResponse);

                    }

                    @Override
                    public void onFailure(Call<CompositionResponse> call, Throwable t) {
                        //Handle failure
                    }
                });

    }

    public void cancel(Long compositionId,
                       Consumer<CompositionResponse> consumer) {

        service.cancel(compositionId, CompositionUpdateRequest.build())
                .enqueue(new Callback<CompositionResponse>() {
                    @Override
                    public void onResponse(Call<CompositionResponse> call,
                                           Response<CompositionResponse> response) {

                        CompositionResponse compositionResponse = response.body();
                        consumer.accept(compositionResponse);

                    }

                    @Override
                    public void onFailure(Call<CompositionResponse> call, Throwable t) {
                        consumer.accept(null);
                    }
                });

    }


    public void getOverviews(Consumer<OverviewResponse> consumer) {

        service.getOverviews()
                .enqueue(new Callback<OverviewResponse>() {
                    @Override
                    public void onResponse(Call<OverviewResponse> call,
                                           Response<OverviewResponse> response) {

                        OverviewResponse overviewResponse = response.body();
                        consumer.accept(overviewResponse);

                    }

                    @Override
                    public void onFailure(Call<OverviewResponse> call, Throwable t) {
                        //Handle failure
                    }
                });

    }

}
