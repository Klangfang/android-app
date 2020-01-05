package com.wfm.soundcollaborations.editor;


import com.wfm.soundcollaborations.editor.activities.EditorActivity;

import dagger.Subcomponent;


@Subcomponent
public interface EditorComponent {

    @Subcomponent.Factory
    interface Factory {
        EditorComponent create();
    }


    void inject(EditorActivity editorActivity);

}
