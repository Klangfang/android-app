package com.wfm.soundcollaborations.interaction;


import com.wfm.soundcollaborations.interaction.editor.EditorComponent;
import com.wfm.soundcollaborations.interaction.main.MainComponent;
import com.wfm.soundcollaborations.interaction.main.fragments.ComposeFragment;

import dagger.Module;
import dagger.Provides;

@Module(subcomponents = {MainComponent.class, EditorComponent.class})
public class InteractionModule {

    @Provides
    ComposeFragment getComposeFragment() {

        return new ComposeFragment();

    }

}
