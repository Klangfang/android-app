package com.wfm.soundcollaborations.Editor.exceptions;


import android.content.Context;
import android.widget.Toast;

/**
 * Created by mohammed on 11/4/17.
 */

public class SoundWillOverlapException extends Exception {

    public SoundWillOverlapException(Context context) {
        Toast.makeText(context, "Recording will overlap with other sounds!", Toast.LENGTH_LONG).show();
    }
}
