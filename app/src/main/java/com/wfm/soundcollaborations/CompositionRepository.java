package com.wfm.soundcollaborations;


import com.wfm.soundcollaborations.interaction.editor.model.composition.CompositionRequestType;
import com.wfm.soundcollaborations.interaction.editor.model.composition.sound.LocalSound;
import com.wfm.soundcollaborations.webservice.CompositionRetrofitService;
import com.wfm.soundcollaborations.webservice.dtos.CompositionOverviewResp;
import com.wfm.soundcollaborations.webservice.dtos.CompositionRequest;
import com.wfm.soundcollaborations.webservice.dtos.CompositionResponse;
import com.wfm.soundcollaborations.webservice.dtos.CompositionUpdateRequest;
import com.wfm.soundcollaborations.webservice.dtos.OverviewResponse;

import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@Singleton
public class CompositionRepository {

    private final CompositionRetrofitService service;


    @Inject
    public CompositionRepository(CompositionRetrofitService service) {

        this.service = service;

    }


    public void create(String compositionTitle,
                       String creatorName,
                       Stream<LocalSound> recordedSounds,
                       Consumer<String> consumer) {

        service.create(CompositionRequest.build(compositionTitle, creatorName, recordedSounds))
                .enqueue(new Callback<CompositionOverviewResp>() {
                    @Override
                    public void onResponse(Call<CompositionOverviewResp> call,
                                           retrofit2.Response<CompositionOverviewResp> response) {

                        CompositionOverviewResp compositionOverviewResp = response.body();
                        consumer.accept(CompositionRequestType.CREATE.getText());

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
                     Consumer<String> consumer) {

        service.join(compositionId, CompositionUpdateRequest.build(recordedSounds))
                .enqueue(new Callback<CompositionResponse>() {
                    @Override
                    public void onResponse(Call<CompositionResponse> call,
                                           Response<CompositionResponse> response) {

                        CompositionResponse compositionResponse = response.body();
                        consumer.accept(CompositionRequestType.JOIN.getText());

                    }

                    @Override
                    public void onFailure(Call<CompositionResponse> call, Throwable t) {
                        //Handle failure
                    }
                });

    }

    public void cancel(Long compositionId,
                       Consumer<String> consumer) {

        service.cancel(compositionId, CompositionUpdateRequest.build())
                .enqueue(new Callback<CompositionResponse>() {
                    @Override
                    public void onResponse(Call<CompositionResponse> call,
                                           Response<CompositionResponse> response) {

                        CompositionResponse compositionResponse = response.body();
                        consumer.accept(CompositionRequestType.CANCEL.getText());

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
