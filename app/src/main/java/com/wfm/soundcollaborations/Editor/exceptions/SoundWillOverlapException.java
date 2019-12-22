package com.wfm.soundcollaborations.Editor.exceptions;


public class SoundWillOverlapException extends Throwable {

    public SoundWillOverlapException() {

        super("Recording will overlap with other sounds!");

    }
}
