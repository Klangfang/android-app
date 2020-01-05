package com.wfm.soundcollaborations.compose.model;

import androidx.lifecycle.ViewModel;

import com.wfm.soundcollaborations.CompositionRepository;
import com.wfm.soundcollaborations.webservice.dtos.CompositionResponse;
import com.wfm.soundcollaborations.webservice.dtos.OverviewResponse;

import java.util.function.Consumer;

import javax.inject.Inject;

public class ComposeViewModel extends ViewModel {

    private static final String TAG = ComposeViewModel.class.getSimpleName();


    private final CompositionRepository compositionRepository;


    @Inject
    public ComposeViewModel(CompositionRepository compositionRepository) {

        this.compositionRepository = compositionRepository;

    }


    @Override
    protected void onCleared() {

        super.onCleared();

    }


    public void loadOverviews(Consumer<OverviewResponse> callback) {

        compositionRepository.getOverviews(callback);

    }


    public void open(Long compositionId, Consumer<CompositionResponse> consumer) {


        compositionRepository.open(compositionId, consumer);

    }

}
