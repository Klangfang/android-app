package com.wfm.soundcollaborations.Editor.exceptions;


import com.wfm.soundcollaborations.Editor.model.composition.StopReason;

public class SoundRecordingTimeException extends Throwable {

    public SoundRecordingTimeException() {

        super(StopReason.MAXIMUM_RECORDING_TIME_REACHED.name());
    }

}
