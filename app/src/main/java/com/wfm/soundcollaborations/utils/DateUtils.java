package com.wfm.soundcollaborations.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mohammed on 10/9/17.
 */

public class DateUtils
{
    public static String getCurrentDate(String format)
    {
        DateFormat dateFormat = new SimpleDateFormat(format, Locale.GERMANY);
        Date today = new Date();
        return dateFormat.format(today);
    }
}
