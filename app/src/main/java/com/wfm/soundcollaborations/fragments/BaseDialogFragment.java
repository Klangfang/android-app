package com.wfm.soundcollaborations.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ViewGroup;
import android.view.Window;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.database.DatabaseHelper;
import com.wfm.soundcollaborations.database.FriendEntity;
import com.wfm.soundcollaborations.database.SoundEntity;
import com.wfm.soundcollaborations.database.TagEntity;
import com.wfm.soundcollaborations.fragments.interfaces.FragmentObserver;
import com.wfm.soundcollaborations.helper.Utility;

import java.sql.SQLException;

/**
 * Created by Markus Eberts on 23.10.16.
 */
public class BaseDialogFragment extends DialogFragment{
    // Fragment observer
    protected FragmentObserver observer;

    // Database
    private DatabaseHelper databaseHelper;
    protected Dao<SoundEntity, Long> soundDao;
    protected Dao<TagEntity, Long> tagDao;
    protected Dao<FriendEntity, Long> friendDao;


    private void initDatabaseHelper() throws SQLException {
        databaseHelper = OpenHelperManager.getHelper(getActivity(),
                DatabaseHelper.class);

        soundDao = databaseHelper.getSoundDao();
        tagDao = databaseHelper.getTagDao();
        friendDao = databaseHelper.getFriendDao();
    }

    /**
     * Adjust layout
     */
    private void adjustLayout(){
        Window window = getDialog().getWindow();
        int width = getResources().getDisplayMetrics().widthPixels
                - (int) Utility.convertDpToPixel(getResources(), 30);
        window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(R.drawable.fragment_round_edges);
    }


    /**
     * Initialization
     */
    private void init(){
        try {
            initDatabaseHelper();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            observer = (FragmentObserver) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement FragmentObserver");
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        init();
        adjustLayout();
    }
}
