package com.wfm.soundcollaborations;

import com.wfm.soundcollaborations.interaction.InteractionModule;
import com.wfm.soundcollaborations.interaction.editor.EditorComponent;
import com.wfm.soundcollaborations.interaction.editor.service.CompositionRecoverService;
import com.wfm.soundcollaborations.interaction.main.MainComponent;
import com.wfm.soundcollaborations.webservice.CompositionWebserviceModule;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {CompositionWebserviceModule.class, InteractionModule.class})
public interface ApplicationComponent {

    EditorComponent.Factory editorComponent();

    MainComponent.Factory mainComponent();

    void inject(CompositionRecoverService compositionRecoverService);


}