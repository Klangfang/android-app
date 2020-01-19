package com.wfm.soundcollaborations;

import com.wfm.soundcollaborations.compose.ComposeComponent;
import com.wfm.soundcollaborations.compose.ComposeModule;
import com.wfm.soundcollaborations.editor.EditorComponent;
import com.wfm.soundcollaborations.editor.EditorModule;
import com.wfm.soundcollaborations.editor.service.CompositionRecoverService;
import com.wfm.soundcollaborations.webservice.CompositionWebserviceModule;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {CompositionWebserviceModule.class, EditorModule.class, ComposeModule.class})
public interface ApplicationComponent {

    EditorComponent.Factory editorComponent();

    ComposeComponent.Factory composeComponent();

    void inject(CompositionRecoverService compositionRecoverService);


}