package com.wfm.soundcollaborations.Editor.model.composition;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.wfm.soundcollaborations.Editor.model.composition.sound.LocalSound;
import com.wfm.soundcollaborations.Editor.model.composition.sound.RemoteSound;
import com.wfm.soundcollaborations.Editor.model.composition.sound.Sound;
import com.wfm.soundcollaborations.Editor.utils.AudioRecorderStatus;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.webservice.CompositionWebserviceClient;
import com.wfm.soundcollaborations.webservice.JsonUtil;
import com.wfm.soundcollaborations.webservice.dtos.CompositionResponse;
import com.wfm.soundcollaborations.webservice.dtos.SoundResponse;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.wfm.soundcollaborations.Editor.utils.DPUtils.getPositionInMs;
import static com.wfm.soundcollaborations.Editor.utils.DPUtils.overlapConstraintsViolation;
import static com.wfm.soundcollaborations.Editor.utils.DPUtils.soundHasReachedMaxLength;

public class CompositionService extends Service {

    private static final String TAG = CompositionService.class.getSimpleName();

    public static final String MESSAGE_TYPE = "MESSAGE_TYPE";
    public static final String SHOW_MESSAGE = "SHOW_MESSAGE";
    public static final String MESSAGE_TEXT = "MESSAGE_TEXT";
    public static final String START_MAIN_ACTIVITY = "START_MAIN_ACTIVITY";
    public static final String UPDATE_TRACK_WATCHES = "UPDATE_TRACK_WATCHES";
    public static final String TRACK_NUMBER = "TRACK_NUMBER";
    public static final String SOUND_WIDTHS = "SOUND_WIDTHS";
    public static final String MAX_AMPLITUDE = "MAX_AMPLITUDE";
    public static final String COMPLETE_LOCAL_SOUND = "COMPLETE_LOCAL_SOUND";
    public static final String ADD_REMOTE_SOUND_VIEW = "ADD_REMOTE_SOUND_VIEW";
    public static final String START_POSITION = "START_POSITION";
    public static final String DURATION = "DURATION";
    public static final String FILE_PATH = "FILE_PATH";
    public static final String NOTIFICATION = "com.wfm.soundcollaborations.Editor.model.composition";

    private Composition composition;

    private final IBinder mBinder = new CompositionServiceBinder();

    private int startPositionInDP = 0;

    private RecordTaskScheduler recordTaskScheduler;
    private PlayTaskScheduler playTaskScheduler;

    private CompositionWebserviceClient csClient;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        playTaskScheduler = new PlayTaskScheduler();
        recordTaskScheduler = new RecordTaskScheduler();
        csClient = new CompositionWebserviceClient();

        return mBinder;

    }


    public class CompositionServiceBinder extends Binder {

        public CompositionService getService() {

            return CompositionService.this;

        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "Service Started");

        String compositionResponseJson = intent.getStringExtra("CompositionResponse");

        if (StringUtils.isNotBlank(compositionResponseJson)) {

            try {

                CompositionResponse compositionResponse = JsonUtil.fromJson(compositionResponseJson, CompositionResponse.class);
                if (compositionResponse != null) {

                    composition = new Composition.CompositionConfigurer()
                            .compositionId(compositionResponse.id)
                            .title(compositionResponse.title)
                            .collaboration()
                            .build();

                    for (SoundResponse sndResp : compositionResponse.sounds) {

                        Integer trackNumber = sndResp.trackNumber;
                        Track track = composition.getTrack(trackNumber);

                        RemoteSound sound = new RemoteSound.Builder()
                                .trackNumber(sndResp.trackNumber)
                                .startPosition(sndResp.startPosition)
                                .duration(sndResp.duration)
                                .filePath(sndResp.url)
                                .build();

                        track.addRemoteSound(getApplicationContext(), sound);
                        composition.updateTrack(trackNumber, track);

                        Intent soundViewIntent = new Intent(NOTIFICATION);
                        soundViewIntent.putExtra(MESSAGE_TYPE, ADD_REMOTE_SOUND_VIEW);
                        soundViewIntent.putExtra(TRACK_NUMBER, sound.trackIndex);
                        soundViewIntent.putExtra(START_POSITION, sound.startPosition);
                        soundViewIntent.putExtra(DURATION, sound.duration);
                        soundViewIntent.putExtra(FILE_PATH, sound.filePath);
                        sendBroadcast(soundViewIntent);

                    }

                }

            } catch (Throwable t) {

                Log.e(TAG, t.getMessage());
            }


        } else {

            String compositionTitle = intent.getStringExtra(String.valueOf(R.id.composition_title_textfield));
            composition = new Composition.CompositionConfigurer()
                    .title(compositionTitle)
                    .build();
        }

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {

        super.onDestroy();

        Log.d(TAG, "Service destroyed");

    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Log.d(TAG, "Service killed");
        super.onTaskRemoved(rootIntent);
        try {

            requestCancel();

        } catch (Throwable t) {
            Log.e(TAG, t.getMessage());
        }
        //stop service
        this.stopSelf();
    }


    public void preDestroy() {

        stopRecording();

        stopPlaying();

    }


    private void completeLocalSoundBroadcast() {

        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(MESSAGE_TYPE, COMPLETE_LOCAL_SOUND);
        sendBroadcast(intent);

    }


    public void requestCreate() {

        preDestroy();

        csClient.create(composition.getTitle(),
                "KLANGFANG",
                composition.getLocalSounds(),
                compositionOverviewResp -> startMainActivity(CompositionRequestType.CREATE));

        composition.cancel();

    }


    public void requestJoin() {

        preDestroy();

        csClient.join(composition.getCompositionId(),
                composition.getLocalSounds(),
                compositionResponse -> startMainActivity(CompositionRequestType.JOIN));

        composition.cancel();

    }


    public void requestCancel() {

        if (composition.isNotCanceled()) {

            preDestroy();

            if (composition.isCollaboration()) {
                csClient.cancel(composition.getCompositionId(),
                        compositionResponse -> startMainActivity(CompositionRequestType.CANCEL));
            }

            composition.cancel();

        }

    }


    private void startMainActivity(CompositionRequestType compositionRequestType) {

        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(MESSAGE_TYPE, START_MAIN_ACTIVITY);
        intent.putExtra(MESSAGE_TEXT, compositionRequestType.getText());
        sendBroadcast(intent);

    }


    public void stopPlaying() {

        if (isPlaying()) {

            playTaskScheduler.stop();
            composition.getTracks().forEach(Track::preDestroy);

        }

    }


    public boolean isPlaying() {

        return playTaskScheduler.isPlaying();

    }


    public boolean isRecording() {

        return recordTaskScheduler.isRecording();

    }


    public void startRecording(int activeTrackIndex, int startPositionInDP, Runnable fallback) {


        if (composition.isOpened()) {

            this.startPositionInDP = startPositionInDP;

            //start track recorder
            Track track = composition.getTrack(activeTrackIndex);
            track.startTrackRecorder(getPositionInMs(startPositionInDP));
            composition.updateTrack(activeTrackIndex, track);

            recordTaskScheduler.record(fallback);

        }

    }


    public void completeLocalSound(int activeTrackIndex, int scrollPositionInDP, String uuid) {

        Track track = composition.getTrack(activeTrackIndex);
        track.stopTrackRecorder(getPositionInMs(scrollPositionInDP));
        Integer duration = track.getDuration();
        String filePath = track.getFilePath();

        if (filePath != null) {

            LocalSound sound = new LocalSound.Builder()
                    .uuid(uuid)
                    .trackNumber(activeTrackIndex)
                    .startPosition(getPositionInMs(startPositionInDP))
                    .duration(duration)
                    .filePath(filePath)
                    .build();
            track.addLocalSound(getApplicationContext(), sound);

        }

        composition.updateTrack(activeTrackIndex, track);

    }


    public boolean canRecord(int activeTrackIndex, int scrollPositionInDP) {

        return composition.isNotExhausted() && checkStop(activeTrackIndex, scrollPositionInDP).equals(StopReason.NO_STOP);

    }


    StopReason checkStop(int activeTrackIndex, int scrollPositionInDP) {

        Track track = composition.getTrack(activeTrackIndex);

        if (track.getTrackRecorderStatus().equals(AudioRecorderStatus.STOPPED)) {

            stopRecording();

            composition.exhausted();

            return StopReason.MAXIMUM_RECORDING_TIME_REACHED;

        }

        if (soundHasReachedMaxLength(scrollPositionInDP)) {

            stopRecording();

            return StopReason.COMPOSITION_END_REACHED;

        }

        // check overlap constraints
        int cp = scrollPositionInDP;
        Function<Sound, Integer> sp = Sound::getStartPosition;
        Function<Sound, Integer> d = Sound::getDuration;
        Predicate<? super Sound> predicate = s -> overlapConstraintsViolation(cp, sp.apply(s), d.apply(s));
        boolean overlapConstraintsViolation = track.getSounds().parallelStream()
                .anyMatch(predicate);
        if (overlapConstraintsViolation) {

            stopRecording();

            return StopReason.SOUND_RECORD_OVERLAP;

        }

        return StopReason.NO_STOP;

    }


    public void deleteSounds(List<String> soundUUIDs) {

        composition.getTracks().forEach(t -> {

            int soundWidths = t.increaseTime(soundUUIDs);

            Intent intent = new Intent(NOTIFICATION);
            intent.putExtra(MESSAGE_TYPE, UPDATE_TRACK_WATCHES);
            intent.putExtra(TRACK_NUMBER, t.getTrackNumber());
            intent.putExtra(SOUND_WIDTHS, soundWidths);
            sendBroadcast(intent);

            t.deleteSounds(soundUUIDs);

        });

    }


    public void stopRecording() {

        if (isRecording()) {

            recordTaskScheduler.stop();

            completeLocalSoundBroadcast();

        }

    }


    public StopReason simulateRecording(int activeTrackIndex, int scrollPositionInDP) {


        if (isRecording()) {

            StopReason stopReason = checkStop(activeTrackIndex, scrollPositionInDP);

            if (stopReason.equals(StopReason.NO_STOP)) {

                int maxAmplitude = composition.getMaxAmplitude(activeTrackIndex);

                Intent intent = new Intent(NOTIFICATION);
                intent.putExtra(MESSAGE_TYPE, "NOT_YET_DEFINED");
                intent.putExtra(MAX_AMPLITUDE, maxAmplitude);
                sendBroadcast(intent);


            }

            return stopReason;
        }

        return StopReason.NO_STOP;

    }


    public void playOrPause(boolean pressPlay, Runnable fallback1, Runnable fallback2) {

        playTaskScheduler.playOrPause(pressPlay, composition.getTracks(), fallback1, fallback2);

    }


    public void seek(int positionInMillis) {

        if (!composition.isPlaying()) {

            composition.getTracks().forEach(t -> t.seek(positionInMillis));

            playTaskScheduler.seek(positionInMillis);

        }

    }


}
