package com.wfm.soundcollaborations.activities;

import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.wfm.soundcollaborations.R;

/**
 * Todo create default config snackback xml like
 */
public final class KlangfangSnackbar {


    public static void shortShow(View view, String text) {

        show(view, text, Snackbar.LENGTH_SHORT);

    }


    public static void longShow(View view, String text) {

        show(view, text, Snackbar.LENGTH_LONG);

    }


    private static void show(View view, String text, int duration) {

        Snackbar snackbar = Snackbar.make(view, text, duration);
        TextView textView = snackbar.getView().findViewById(R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(view.getContext(), R.color.highlight));
        snackbar.show();

    }

}
