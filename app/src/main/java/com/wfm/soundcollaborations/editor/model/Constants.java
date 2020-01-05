package com.wfm.soundcollaborations.editor.model;

/**
 * Created by mohammed on 10/11/17.
 */

public class Constants
{
    public static final int MAX_RECORD_TIME = 30; // in seconds
    public static final String[]
            SUPPORTED_SOUND_FILE_EXTENSIONS = {"mp3", "wav", "3gpp", "3gp", "amr", "aac", "m4a", "ogg"};
    public static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    public static final int BUFFER_SEGMENT_COUNT = 256;
    public static final String AUDIO_STREAMING_AGENT = "klangfang_app_audio_user_agent";
    public static final String SOUND_FILE_MIME = "audio/3gpp";
    public static final int COMPOSITION_LENGTH_IN_MILLI_SECONDS = 1000 * 120;
}
