package com.wfm.soundcollaborations.Editor.exceptions;

import android.content.Context;
import android.widget.Toast;

public class SoundRecordingTimeException extends Exception {
    public SoundRecordingTimeException(Context context) {
        Toast.makeText(context, "30 second of recording is reached!", Toast.LENGTH_LONG).show();
    }
}
