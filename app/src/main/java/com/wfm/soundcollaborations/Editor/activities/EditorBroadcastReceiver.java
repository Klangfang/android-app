package com.wfm.soundcollaborations.Editor.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wfm.soundcollaborations.Editor.model.composition.CompositionService;
import com.wfm.soundcollaborations.Editor.model.composition.sound.RemoteSound;
import com.wfm.soundcollaborations.activities.MainActivity;

/**
 * Broadcast message receiver handles receiving messages sent from composition service
 */
public class EditorBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String messageType = bundle.getString(CompositionService.MESSAGE_TYPE);
            assert messageType != null;
            switch (messageType) {
                case CompositionService.START_MAIN_ACTIVITY:

                    receiveStartMainActivity(context, bundle);

                    break;
                case CompositionService.ADD_REMOTE_SOUND_VIEW:

                    receiveAddRemoveSoundView(context, bundle);

                    break;
                case CompositionService.COMPLETE_LOCAL_SOUND:

                    receiveCompleteLocalSound(context);

                    break;
                case CompositionService.UPDATE_TRACK_WATCHES:

                    receiveUpdateTrackWatches(context, bundle);

                    break;
                default:

                    receiveUpdateSoundView(context, bundle);

                    break;
            }
        }

    }


    private void receiveStartMainActivity(Context context, Bundle bundle) {

        EditorActivity editorActivity = (EditorActivity) context;
        editorActivity.finish();

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtras(bundle);

        context.startActivity(intent);

    }


    private void receiveAddRemoveSoundView(Context context, Bundle bundle) {

        EditorActivity editorActivity = (EditorActivity) context;

        int trackNumber = bundle.getInt(CompositionService.TRACK_NUMBER);
        int startPosition = bundle.getInt(CompositionService.START_POSITION);
        int duration = bundle.getInt(CompositionService.DURATION);
        String filePath = bundle.getString(CompositionService.FILE_PATH);
        RemoteSound remoteSound = new RemoteSound.Builder()
                .trackNumber(trackNumber)
                .startPosition(startPosition)
                .duration(duration)
                .filePath(filePath)
                .build();
        editorActivity.addRemoteSoundView(remoteSound);

    }


    private void receiveCompleteLocalSound(Context context) {

        EditorActivity editorActivity = (EditorActivity) context;

        editorActivity.completeLocalSound();

    }


    private void receiveUpdateTrackWatches(Context context, Bundle bundle) {

        EditorActivity editorActivity = (EditorActivity) context;

        int trackNumber = bundle.getInt(CompositionService.TRACK_NUMBER);
        int soundWidths = bundle.getInt(CompositionService.SOUND_WIDTHS);

        editorActivity.updateTrackWatches(trackNumber, soundWidths);


    }


    private void receiveUpdateSoundView(Context context, Bundle bundle) {

        EditorActivity editorActivity = (EditorActivity) context;

        int maxAmplitude = bundle.getInt(CompositionService.MAX_AMPLITUDE);
        editorActivity.updateSoundView(maxAmplitude);

    }

}
