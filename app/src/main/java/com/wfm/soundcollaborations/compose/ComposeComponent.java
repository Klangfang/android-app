package com.wfm.soundcollaborations.compose;


import com.wfm.soundcollaborations.fragments.ComposeFragment;

import dagger.Subcomponent;


@Subcomponent
public interface ComposeComponent {

    @Subcomponent.Factory
    interface Factory {
        ComposeComponent create();
    }


    void inject(ComposeFragment composeFragment);

}
