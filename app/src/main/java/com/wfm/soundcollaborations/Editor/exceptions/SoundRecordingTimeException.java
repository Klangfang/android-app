package com.wfm.soundcollaborations.Editor.exceptions;


public class SoundRecordingTimeException extends Throwable {

    public SoundRecordingTimeException() {

        super("30 second of recording is reached!");
    }

}
