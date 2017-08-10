package com.wfm.soundcollaborations.misc;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markus Eberts on 15.10.16.
 */
public class PermissionManager {

    public static void requestPermissions(Activity activity, String[] permissions, int request){
        List<String> checkedPermissions = new ArrayList<>();

        for (String permission : permissions){
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                checkedPermissions.add(permission);
            }
        }

        if (!checkedPermissions.isEmpty()) {
            String[] requestPermissions = checkedPermissions.toArray(new String[checkedPermissions.size()]);
            ActivityCompat.requestPermissions(activity, requestPermissions, request);
        }
    }
}