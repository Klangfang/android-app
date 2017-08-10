package com.wfm.soundcollaborations.helper;

import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;

import java.util.List;

/**
 * Created by Markus Eberts on 23.10.16.
 */
public final class Utility {

    private Utility(){}

    /**
     * Joins multiple strings together with a special string as the separator
     *
     * @param strings The strings
     * @param separator Separates the individual strings
     * @return Joined string
     *
     */
    public static String join(List<String> strings, String separator){
        if (strings.isEmpty()){
            return "";
        }

        String joinedString = "";
        for (String string: strings){
            joinedString += string + separator;
        }
        return joinedString.substring(0, joinedString.length()-separator.length());
    }


    /**
     * Adds multiple objects to an object array
     *
     * @return Object array
     *
     */
    public static Object[] asArray(Object... args){
        Object[] objects = new Object[args.length];
        for (int i = 0; i < args.length; i++){
            objects[i] = args[i];
        }
        return objects;
    }


    /**
     * Converts dp to pixel in dependence of the devices resolution
     * @param dp
     * @return dp in pixel
     */
    public static float convertDpToPixel(Resources res, float dp) {
        DisplayMetrics metrics = res.getDisplayMetrics();
        float px = dp * metrics.density;
        return px;
    }


    public static int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }


    public static float scale(float v, float valueRangeStart, float valueRangeEnd,
                              float destRangeStart, float destRangeEnd){
        float result = (destRangeEnd - destRangeStart) * (v - valueRangeStart);
        result = result / (valueRangeEnd - valueRangeStart) + destRangeStart;

        return result;
    }
}
