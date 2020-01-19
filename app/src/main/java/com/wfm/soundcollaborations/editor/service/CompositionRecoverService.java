package com.wfm.soundcollaborations.editor.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.wfm.soundcollaborations.ApplicationComponent;
import com.wfm.soundcollaborations.CompositionRepository;
import com.wfm.soundcollaborations.KlangfangApp;

import javax.inject.Inject;

public class CompositionRecoverService extends Service {

    private static final String TAG = CompositionRecoverService.class.getSimpleName();

    private final IBinder mBinder = new CompositionRecoverServiceBinder();

    private long compositionId;

    ApplicationComponent applicationComponent;

    @Inject
    CompositionRepository compositionRepository;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        applicationComponent = ((KlangfangApp) getApplicationContext()).appComponent;

        applicationComponent.inject(this);

        return mBinder;

    }


    public class CompositionRecoverServiceBinder extends Binder {

        public CompositionRecoverService getService() {

            return CompositionRecoverService.this;

        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "Service Started");

        compositionId = intent.getLongExtra("COMPOSITION_ID", 0);

        return START_NOT_STICKY;

    }


    @Override
    public void onDestroy() {

        super.onDestroy();

        Log.d(TAG, "Service destroyed");

    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {

        super.onTaskRemoved(rootIntent);

        compositionRepository.cancel(compositionId, this::showInfo);

        this.stopSelf();

        Log.d(TAG, "Service killed");

    }


    public void showInfo(String text) {

        Log.i(TAG, text);

    }

}
