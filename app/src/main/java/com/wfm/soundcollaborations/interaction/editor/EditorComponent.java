package com.wfm.soundcollaborations.interaction.editor;


import com.wfm.soundcollaborations.interaction.editor.activities.EditorActivity;

import dagger.Subcomponent;


@Subcomponent
public interface EditorComponent {

    @Subcomponent.Factory
    interface Factory {
        EditorComponent create();
    }


    void inject(EditorActivity editorActivity);

}
