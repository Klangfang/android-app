package com.wfm.soundcollaborations.Editor.exceptions;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by mohammed on 11/4/17.
 */

public class SoundWillBeOutOfCompositionException extends Exception {

    public SoundWillBeOutOfCompositionException(Context context) {
        Toast.makeText(context, "Recording will be out of composition!", Toast.LENGTH_LONG).show();
    }
}
