package com.wfm.soundcollaborations.Editor.utils;

public final class DPUtils {

    public static final int TRACK_WIDTH_IN_MS = 7200;
    public static final int SOUND_SECOND_WIDTH = 60;
    public static final int TRACK_HEIGHT = 75;


    public static int getValueInDP(long valueInMs) {

        int integerValueInMs = Long.valueOf(valueInMs).intValue();
        int valueInDP = (integerValueInMs / 1000) * SOUND_SECOND_WIDTH;
        valueInDP += (integerValueInMs % 1000) * SOUND_SECOND_WIDTH / 1000;
        return valueInDP;

    }


    public static boolean hasReachedLimit(int cursorPositionInDP) {

        return (cursorPositionInDP + SOUND_SECOND_WIDTH) > TRACK_WIDTH_IN_MS;

    }

    public static int getPositionInMs(int width) {

        // Factor um Breite in Millisekunden zu konvertieren: Hat sich automatisch ergeben
        final double WIDTH_TO_MS_FACTOR = 16.6667;

        return  (int) (width * WIDTH_TO_MS_FACTOR);

    }

}
