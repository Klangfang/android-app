package com.wfm.soundcollaborations;


import com.wfm.soundcollaborations.interaction.editor.model.composition.CompositionRequestType;
import com.wfm.soundcollaborations.interaction.editor.model.composition.sound.LocalSound;
import com.wfm.soundcollaborations.webservice.dtos.CompositionDemo;
import com.wfm.soundcollaborations.webservice.dtos.CompositionResponse;
import com.wfm.soundcollaborations.webservice.dtos.OverviewResponse;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class CompositionRepository {


    private final Map<Long, CompositionResponse> demoCompositions;
    private final OverviewResponse demoCompositionsOverview;


    @Inject
    public CompositionRepository() {

        demoCompositions = CompositionDemo.compositions();
        demoCompositionsOverview = CompositionDemo.overviewResponse();

    }


    public void create(String compositionTitle,
                       String creatorName,
                       Stream<LocalSound> recordedSounds,
                       Consumer<String> consumer) {

        consumer.accept(CompositionRequestType.CREATE.getText());

    }


    public void open(Long compositionId,
                     Consumer<CompositionResponse> consumer) {

        consumer.accept(demoCompositions.get(compositionId));

    }

    public void join(Long compositionId,
                     Stream<LocalSound> recordedSounds,
                     Consumer<String> consumer) {

        consumer.accept(CompositionRequestType.JOIN.getText());

    }

    public void cancel(Long compositionId,
                       Consumer<String> consumer) {

        consumer.accept(CompositionRequestType.CANCEL.getText());

    }


    public void getOverviews(Consumer<OverviewResponse> consumer) {

        consumer.accept(demoCompositionsOverview);

    }

}