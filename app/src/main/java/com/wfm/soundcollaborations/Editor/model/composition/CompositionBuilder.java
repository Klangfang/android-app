package com.wfm.soundcollaborations.Editor.model.composition;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.wfm.soundcollaborations.Editor.exceptions.NoActiveTrackException;
import com.wfm.soundcollaborations.Editor.exceptions.SoundRecordingTimeException;
import com.wfm.soundcollaborations.Editor.exceptions.SoundWillBeOutOfCompositionException;
import com.wfm.soundcollaborations.Editor.exceptions.SoundWillOverlapException;
import com.wfm.soundcollaborations.Editor.network.SoundDownloader;
import com.wfm.soundcollaborations.Editor.tasks.VisualizeSoundTask;
import com.wfm.soundcollaborations.Editor.utils.AudioRecorderStatus;
import com.wfm.soundcollaborations.Editor.views.composition.CompositionView;
import com.wfm.soundcollaborations.Editor.views.composition.SoundView;
import com.wfm.soundcollaborations.Editor.views.composition.TrackView;
import com.wfm.soundcollaborations.Editor.views.composition.TrackWatchView;
import com.wfm.soundcollaborations.Editor.views.composition.listeners.TrackViewOnClickListener;
import com.wfm.soundcollaborations.Editor.views.composition.listeners.TrackWatchViewOnClickListener;
import com.wfm.soundcollaborations.webservice.CompositionServiceClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementiert die Businesslogik des CompositionOverview Builders (Wird in der EditorActivity verwendet)
 */

public class CompositionBuilder
{

    private static final String TAG = CompositionBuilder.class.getSimpleName();
    private static final int TRACK_WIDTH_IN_MS = 7200;
    private static final int SOUND_SECOND_WIDTH = 60;
    private static final int TRACK_HEIGHT = 75;

    private CompositionView compositionView;
    private ArrayList<TrackView> downloadedTrackViews;
    private ArrayList<SoundView> downloadedSoundViews;

    private ArrayList<Sound> downloadedSounds;
    private SoundDownloader downloader;

    private int numberOfDownloadedSounds = 0;
    private ArrayList<Track> tracks;
    private List<Sound> recordedSounds = new ArrayList<>();

    private TracksTimer mTracksTimer;
    private boolean playing = false;

    private List<SoundView> soundsToDelete = new ArrayList<>();

    private CompositionServiceClient client;

    private Composition composition;

    public CompositionBuilder(CompositionView compositionView, int tracks)
    {
        this.compositionView = compositionView;
        downloadedTrackViews = new ArrayList<>();
        downloadedSoundViews = new ArrayList<>();
        downloadedSounds = new ArrayList<>();
        this.tracks = new ArrayList<>();
        initTracksViews(tracks);
        initTracks(tracks);
        client = new CompositionServiceClient(getCompositionView().getContext());
    }

    private void initTracksViews(int tracks)
    {
        for(int i=0; i<tracks; i++)
        {
            TrackView trackView = new TrackView(this.compositionView.getContext());
            downloadedTrackViews.add(trackView);
        }
    }

    private void initTracks(int tracks)
    {
        for(int i=0; i<tracks; i++)
        {
            Track track = new Track();
            this.tracks.add(track);
        }
        mTracksTimer = new TracksTimer(this.tracks, this.compositionView);
    }

    public void addSounds(Composition composition) {
        for (Sound sound : composition.sounds) {
            SoundView soundView = new SoundView(this.compositionView.getContext());
            RelativeLayout.LayoutParams soundParams =
                    new RelativeLayout.LayoutParams(getValueInDP(sound.getDuration()), TRACK_HEIGHT);
            soundParams.setMargins(getValueInDP(sound.getStartPosition()), 0, 0, 0);
            soundView.setLayoutParams(soundParams);
            soundView.setTrackNumber(sound.getTrackNumber());
            // add soundView to the list
            downloadedSoundViews.add(soundView);
            // add sound view to the track
            downloadedTrackViews.get(sound.getTrackNumber()).addSoundView(soundView);

            downloadedSounds.add(sound);

            this.composition = composition;
        }
        // we will add everything to the composition view
        build();
        downloadSounds();
    }

    public int getValueInDP(long valueInMs)
    {
        int integerValueInMs = Long.valueOf(valueInMs).intValue();
        int valueInDP = 0;
        valueInDP = (integerValueInMs / 1000) * SOUND_SECOND_WIDTH;
        valueInDP += (integerValueInMs % 1000) * SOUND_SECOND_WIDTH / 1000;
        return valueInDP;
    }

    public int getPositionInMs(int width) {
        // Factor um Breite in Millisekunden zu konvertieren: Hat sich automatisch ergeben
        final double WIDTH_TO_MS_FACTOR = 16.6667;

        int positionInMs = (int) (width * WIDTH_TO_MS_FACTOR);
        return positionInMs;
    }

    public void build()
    {
        for(int i = 0; i< downloadedTrackViews.size(); i++)
        {
            // watches
            TrackWatchView trackWatchView = new TrackWatchView(this.compositionView.getContext());
            trackWatchView.setOnClickListener(new TrackWatchViewOnClickListener(this.compositionView, i));
            LinearLayout.LayoutParams watchParams  = new LinearLayout.LayoutParams(TRACK_HEIGHT, TRACK_HEIGHT);
            watchParams.setMargins(10, 10, 0, 10);
            trackWatchView.setLayoutParams(watchParams);
            // sounds
            TrackView trackView = downloadedTrackViews.get(i);
            trackWatchView.setOnClickListener(new TrackViewOnClickListener(this.compositionView, i));
            LinearLayout.LayoutParams trackParams  = new LinearLayout.LayoutParams(TRACK_WIDTH_IN_MS, TRACK_HEIGHT);
            trackParams.setMargins(0, 10, 0, 10);
            trackView.setLayoutParams(trackParams);
            this.compositionView.addTrackView(trackWatchView, trackView);

        }
        // default one is the first
        this.compositionView.activateTrack(0);

        // subscribe to scroll event
        this.compositionView.setOnScrollChanged(new CompositionView.OnScrollChanged() {
            @Override
            public void onNewScrollPosition(int position)
            {
                if(!playing)
                {
                    int milliseconds = (int)(position * 16.6666);
                    seek(milliseconds);
                    Log.d(TAG, "Milliseconds => "+milliseconds+" position => "+ position);
                }

            }
        });
    }


    /*
        Downloading Sounds
     */
    private void downloadSounds()
    {
        prepareTracks();// Initialisierung von AudioPlayer!
        this.downloader = SoundDownloader.getSoundDownloader(this.compositionView.getContext(),
                new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    protected void completed(BaseDownloadTask task)
                    {
                        task.reuse();
                        int index = (int) task.getTag();
                        VisualizeSoundTask soundTask = new VisualizeSoundTask(downloadedSoundViews.get(index), downloadedSounds.get(index));
                        soundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        tracks.get(downloadedSounds.get(index).getTrackNumber()).addSound(downloadedSounds.get(index));
                        numberOfDownloadedSounds++;
                        if(numberOfDownloadedSounds == downloadedSounds.size()) {
                            prepareTracks();
                        }
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {

                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }
                });
        for(int i = 0; i< downloadedSounds.size(); i++) {
            this.downloader.addSoundUrl(downloadedSounds.get(i).getFilePath(), i);
        }

        this.downloader.download();
    }

    private void prepareTracks() {
        for(int i = 0; i< tracks.size(); i++) {
            tracks.get(i).prepare(this.compositionView.getContext());
        }
    }

    public SoundView getRecordSoundView(Context context) throws Exception
    {
        SoundView recordingSoundView;
        int activeTrack = this.compositionView.getActiveTrack();
        if( activeTrack == -1) {
            throw new NoActiveTrackException();
        }

        //TODO checkLimits();

        recordingSoundView = new SoundView(context);
        recordingSoundView.setTrackNumber(activeTrack);
        RelativeLayout.LayoutParams soundParams =
                new RelativeLayout.LayoutParams(0, TRACK_HEIGHT);
        soundParams.setMargins(this.compositionView.getScrollPosition(), 0, 0, 0);
        recordingSoundView.setLayoutParams(soundParams);
        recordingSoundView.setDefaultSoundColor();

        downloadedTrackViews.get(activeTrack).addSoundView(recordingSoundView);

        return recordingSoundView;
    }

    public void checkLimits(SoundView soundView, Integer soundLengthInWidth, Integer startPositionInWidth) throws SoundWillOverlapException, SoundWillBeOutOfCompositionException, SoundRecordingTimeException {
        if (getActiveTrack().getTrackRecorderStatus().equals(AudioRecorderStatus.STOPPED)) {
            prepareRecordedSound(soundView, soundLengthInWidth, startPositionInWidth);
            throw new SoundRecordingTimeException(compositionView.getContext());
        }
        // Check Sound out of composition
        int cursorPositionInDP = this.compositionView.getScrollPosition();
        if((cursorPositionInDP + SOUND_SECOND_WIDTH) > TRACK_WIDTH_IN_MS) {
            // Stop recorder
            prepareRecordedSound(soundView, soundLengthInWidth, startPositionInWidth);
            throw new SoundWillBeOutOfCompositionException(compositionView.getContext());
        }

        // check sound overlapping
        long startPositionInDP, lengthInDP;
        int trackNumber = compositionView.getActiveTrack();
        for (Sound sound : tracks.get(trackNumber).getSounds()) {
            startPositionInDP = getValueInDP(sound.getStartPosition());
            lengthInDP = getValueInDP(sound.getDuration());
            long endPositionInDP = startPositionInDP + lengthInDP;
            int distanceToStartPos = cursorPositionInDP + 20 ;
            int distanceToEndPos = cursorPositionInDP + 20 ;
            if (distanceToStartPos > startPositionInDP && distanceToEndPos < endPositionInDP) {
                //stopTrackRecorder(sound.getDuration());
                prepareRecordedSound(soundView, soundLengthInWidth, startPositionInWidth);
                throw new SoundWillOverlapException(compositionView.getContext());
            }
        }
    }

    public void play() {

        if (!playing) {
            mTracksTimer.play();
            updateStatus();
        } else {
            mTracksTimer.pause();
            updateStatus();
        }
    }

    private void updateStatus() {

        playing = !playing;
    }

    public boolean getPlayStatus() {

        return playing;
    }

    public void seek(int positionInMillis)
    {
        mTracksTimer.seek(positionInMillis);
    }

    public CompositionView getCompositionView()
    {
        return this.compositionView;
    }

    public static int getTrackWidthInMs() {
        return TRACK_WIDTH_IN_MS;
    }

    public static int getSoundSecondWidth() {
        return SOUND_SECOND_WIDTH;
    }

    public void addRecordedSound(Sound sound, int trackNumber) {

        // delete oldTrack
        Track track = tracks.get(trackNumber);
        tracks.remove(trackNumber);

        // prepare track and save it
        track.prepareSound(sound, compositionView.getContext());
        tracks.add(trackNumber, track);

        mTracksTimer.updateTrack(trackNumber, track); //TODO noch brauchbar??!

        sound.trackNumber = trackNumber;
        recordedSounds.add(sound);

    }


    public Track getActiveTrack() {
        return tracks.get(compositionView.getActiveTrack());
    }

    public void stopTrackRecorder() {
        getActiveTrack().stopTrackRecorder();
    }

    public void deleteSounds() {
        Track track = getActiveTrack();

        for (SoundView soundView : soundsToDelete) {
            // delete sound file
            Integer trackNumber = soundView.getTrackNumber();
            Sound sound = soundView.getSound();
            tracks.remove(trackNumber);
            track.deleteSound(sound);
            tracks.add(trackNumber, track);

            // delete sound view / track watch view
            compositionView.deleteSoundView(soundView, soundView.getSoundLength() / 3 * 0.17f);        //TODO diese geheime Zahl genau checken!
        }
        soundsToDelete.clear();
    }

    public void selectSound(SoundView soundView) {
        soundsToDelete.add(soundView);
    }

    public boolean isSelectedSound(SoundView soundView) {
        for (SoundView sView : soundsToDelete) {
            if (sView.equals(soundView)) {
                return true;
            }
        }
        return false;
    }

   /* public boolean isAllDeselected() {
        return soundsToDelete.isEmpty();
    }*/

    public boolean deselectSound(SoundView soundView) {
        soundsToDelete.remove(soundView);
        return soundsToDelete.isEmpty();
    }

    public void prepareRecordedSound(SoundView soundView, Integer soundLengthInWidth, Integer startPositionInWidth) {

        stopTrackRecorder();
        Track activeTrack = getActiveTrack();
        String filePath = activeTrack.getFilePath();
        Integer trackNumber = soundView.getTrackNumber();
        if (filePath != null && soundLengthInWidth!=null && startPositionInWidth != null) {
            Sound sound = new Sound(trackNumber, getPositionInMs(startPositionInWidth), activeTrack.getDuration(), filePath);
            soundView.setSound(sound);
            addRecordedSound(sound, trackNumber);
        }

    }


    public void release() {

        Response.Listener<String> listener = response -> showInfo(response);
        client.release(composition.id, recordedSounds, listener);

    }


    public void create() {

        Composition composition = new Composition();
        composition.sounds = recordedSounds;
        composition.creatorName = "Klangfang";
        composition.title = "GET_TITLE";
        composition.sounds = recordedSounds;
        Response.Listener<JSONObject> listener = response -> showInfo(response);
        client.create(composition, listener);

    }


    private <T> void showInfo(T response) {
        Toast.makeText(getCompositionView().getContext(), "Composition is released!", Toast.LENGTH_LONG).show();
    }


}
