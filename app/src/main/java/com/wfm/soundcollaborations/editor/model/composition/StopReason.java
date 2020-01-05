package com.wfm.soundcollaborations.editor.model.composition;

public enum StopReason {

    NO_STOP(""),
    COMPLETE_RECORDING("The sound recording is completed."),
    COMPOSITION_END_REACHED("The composition end has been reached."),
    MAXIMUM_RECORDING_TIME_REACHED("The maximum of recording time has been reached."),
    SOUND_RECORD_OVERLAP("The recording sound is overlapping with others."),
    UNKNOWN("Stop due to error.");

    private final String reason;

    StopReason(String reason) {

        this.reason = reason;
    }

    public String getReason() {

        return reason;

    }
}
