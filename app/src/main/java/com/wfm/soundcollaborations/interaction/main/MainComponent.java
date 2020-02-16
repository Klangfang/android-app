package com.wfm.soundcollaborations.interaction.main;


import com.wfm.soundcollaborations.interaction.main.fragments.ComposeFragment;

import dagger.Subcomponent;


@Subcomponent
public interface MainComponent {

    @Subcomponent.Factory
    interface Factory {
        MainComponent create();
    }

    void inject(MainActivity mainActivity);

    void inject(ComposeFragment composeFragment);

}
