package com.wfm.soundcollaborations.interaction.editor.utils;

public final class DPUtils {

    public static final int TRACK_MAX_LENGTH_IN_DP = 7200;
    private static final int SOUND_MAX_LENGTH_IN_S = 60;
    public static final int TRACK_MAX_HEIGHT = 75;


    public static int getValueInDP(long valueInMs) {

        int integerValueInMs = Long.valueOf(valueInMs).intValue();
        int valueInDP = (integerValueInMs / 1000) * SOUND_MAX_LENGTH_IN_S;
        valueInDP += (integerValueInMs % 1000) * SOUND_MAX_LENGTH_IN_S / 1000;
        return valueInDP;

    }


    public static boolean soundHasReachedMaxLength(int cursorPositionInDP) {

        return cursorPositionInDP >= TRACK_MAX_LENGTH_IN_DP;

    }


    public static boolean overlapConstraintsViolation(int cursorPositionInDP, int startPositionInMS, int durationInMS) {

        long startPositionInDP = getValueInDP(startPositionInMS);
        long endPositionInDP = startPositionInDP + getValueInDP(durationInMS);

        boolean checkStartPosConstraint = cursorPositionInDP < startPositionInDP;
        boolean checkEndPosConstraint = cursorPositionInDP > endPositionInDP;

        return !(checkStartPosConstraint || checkEndPosConstraint);


    }


    public static int getPositionInMs(int width) {

        // Factor um Breite in Millisekunden zu konvertieren: Hat sich automatisch ergeben
        final double WIDTH_TO_MS_FACTOR = 16.6667;

        return  (int) (width * WIDTH_TO_MS_FACTOR);

    }

}
