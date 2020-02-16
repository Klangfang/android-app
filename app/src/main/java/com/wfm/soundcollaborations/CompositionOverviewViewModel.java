package com.wfm.soundcollaborations;

import androidx.lifecycle.ViewModel;

import com.wfm.soundcollaborations.webservice.dtos.CompositionResponse;
import com.wfm.soundcollaborations.webservice.dtos.OverviewResponse;

import java.util.function.Consumer;

import javax.inject.Inject;

public class CompositionOverviewViewModel extends ViewModel {

    private static final String TAG = CompositionOverviewViewModel.class.getSimpleName();


    private final CompositionRepository compositionRepository;


    @Inject
    public CompositionOverviewViewModel(CompositionRepository compositionRepository) {

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
