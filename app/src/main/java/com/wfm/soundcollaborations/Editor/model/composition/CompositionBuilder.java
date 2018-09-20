package com.wfm.soundcollaborations.Editor.model.composition;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.wfm.soundcollaborations.Editor.exceptions.NoActiveTrackException;
import com.wfm.soundcollaborations.Editor.exceptions.SoundWillBeOutOfCompositionException;
import com.wfm.soundcollaborations.Editor.network.SoundDownloader;
import com.wfm.soundcollaborations.Editor.tasks.VisualizeSoundTask;
import com.wfm.soundcollaborations.Editor.utils.AudioRecorderStatus;
import com.wfm.soundcollaborations.Editor.views.composition.CompositionView;
import com.wfm.soundcollaborations.Editor.views.composition.SoundView;
import com.wfm.soundcollaborations.Editor.views.composition.TrackView;
import com.wfm.soundcollaborations.Editor.views.composition.TrackWatchView;
import com.wfm.soundcollaborations.Editor.views.composition.listeners.TrackViewOnClickListener;
import com.wfm.soundcollaborations.Editor.views.composition.listeners.TrackWatchViewOnClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mohammed on 10/29/17.
 */

public class CompositionBuilder
{

    private static final String TAG = CompositionBuilder.class.getSimpleName();
    private static final int TRACK_WIDTH = 7200;
    private static final int SOUND_SECOND_WIDTH = 60;
    private static final int TRACK_HEIGHT = 75;

    private CompositionView compositionView;
    private ArrayList<TrackView> downloadedTrackViews;
    private ArrayList<SoundView> downloadedSoundViews;

    private ArrayList<Sound> downloadedSounds;
    private SoundDownloader downloader;

    private int numberOfDownloadedSounds = 0;
    private ArrayList<Track> tracks;

    private TracksTimer mTracksTimer;
    private boolean playing = false;

    private List<Sound> soundsToDelete = new ArrayList<>();

    public CompositionBuilder(CompositionView compositionView, int tracks)
    {
        this.compositionView = compositionView;
        downloadedTrackViews = new ArrayList<>();
        downloadedSoundViews = new ArrayList<>();
        downloadedSounds = new ArrayList<>();
        this.tracks = new ArrayList<>();
        initTracksViews(tracks);
        initTracks(tracks);
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

    public void addSounds(List<Sound> sounds)
    {
        downloadedSounds.addAll(sounds);
        for(int i=0; i<sounds.size(); i++)
        {
            // create sounds view
            SoundView soundView = new SoundView(this.compositionView.getContext());
            Sound sound = sounds.get(i);
            RelativeLayout.LayoutParams soundParams =
                    new RelativeLayout.LayoutParams(getSoundViewWidth(sound.getLength()), TRACK_HEIGHT);
            soundParams.setMargins(getSoundViewMargin(sound.getStartPosition()), 0, 0, 0);
            soundView.setLayoutParams(soundParams);
            soundView.setTrack(sound.getTrack());
            // add soundView to the list
            downloadedSoundViews.add(soundView);
            // add sound view to the track
            downloadedTrackViews.get(sounds.get(i).getTrack()).addSoundView(soundView);
        }
        // we will add everything to the composition view
        build();
        downloadSounds();
    }

    public int getSoundViewWidth(int lengthInMs)
    {
        int width = 0;
        width += (lengthInMs / 1000) * SOUND_SECOND_WIDTH;
        width += (lengthInMs % 1000) * SOUND_SECOND_WIDTH / 1000;
        return width;
    }

    private int getSoundViewMargin(int positionInMs)
    {
        int width = 0;
        width += (positionInMs / 1000) * SOUND_SECOND_WIDTH;
        //TODO Aufschluesseln was das ist!
        width += (positionInMs % 1000) * SOUND_SECOND_WIDTH / 1000;
        return width;
    }

    public int getPositionInMs(int width) {
        // Factor um Breite in Millisekunden zu konvertieren: Hat sich automatisch ergeben
        final double WIDTH_TO_MS_FACTOR = 16.6667;

        int positionInMs = (int) (width * WIDTH_TO_MS_FACTOR);
        return positionInMs;
    }

    private void build()
    {
        for(int i = 0; i< downloadedTrackViews.size(); i++)
        {
            // watches
            TrackWatchView trackWatchView = new TrackWatchView(this.compositionView.getContext());
            trackWatchView.setOnClickListener(new TrackWatchViewOnClickListener(this.compositionView, i));
            LinearLayout.LayoutParams watchParams  = new LinearLayout.LayoutParams(TRACK_HEIGHT, TRACK_HEIGHT);
            watchParams.setMargins(10, 10, 0, 10);
            trackWatchView.setLayoutParams(watchParams);
            // tracks
            TrackView trackView = downloadedTrackViews.get(i);
            trackWatchView.setOnClickListener(new TrackViewOnClickListener(this.compositionView, i));
            LinearLayout.LayoutParams trackParams  = new LinearLayout.LayoutParams(TRACK_WIDTH, TRACK_HEIGHT);
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
                        tracks.get(downloadedSounds.get(index).getTrack()).addSound(downloadedSounds.get(index));
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
        for(int i = 0; i< downloadedSounds.size(); i++)
            this.downloader.addSoundUrl(downloadedSounds.get(i).getLink(), i);

        this.downloader.download();
    }

    private void prepareTracks() {
        for(int i = 0; i< tracks.size(); i++) {
            tracks.get(i).prepare(this.compositionView.getContext());
        }
    }

    public SoundView getRecordSoundView(Context context) throws Exception
    {
        int activeTrack = this.compositionView.getActiveTrack();
        if( activeTrack == -1)
            throw new NoActiveTrackException();

        int pos = this.compositionView.getScrollPosition();
        if((pos + SOUND_SECOND_WIDTH) > TRACK_WIDTH)
            throw new SoundWillBeOutOfCompositionException();

        isThereAnyOverlapping(pos);

        SoundView recordingSoundView = new SoundView(context);
        recordingSoundView.setTrack(activeTrack);
        RelativeLayout.LayoutParams soundParams =
                new RelativeLayout.LayoutParams(0, TRACK_HEIGHT);
        soundParams.setMargins(pos, 0, 0, 0);
        recordingSoundView.setLayoutParams(soundParams);
        recordingSoundView.setYellowBackground();

        downloadedTrackViews.get(activeTrack).addSoundView(recordingSoundView);

        return recordingSoundView;
    }

    public boolean isThereAnyOverlapping(int pos) {
        int margin, width;
        int trackNumber = compositionView.getActiveTrack();
        for (Sound sound : tracks.get(trackNumber).getSounds()) {
            margin = getSoundViewMargin(sound.getStartPosition());
            width = getSoundViewWidth(sound.getLength());

            // check one second forward
            // check if indicator above above track
            if(sound.getTrack() == trackNumber && ((pos <= margin && (pos + SOUND_SECOND_WIDTH) >= margin) || (pos <= (margin+width) && pos >= margin))) {
                return true;
            }
        }
        return false;
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

    public static int getTrackWidth() {
        return TRACK_WIDTH;
    }

    public static int getSoundSecondWidth() {
        return SOUND_SECOND_WIDTH;
    }

    public void addRecordedSound(String soundPath, int length, int trackNumber, int startPositionInWidth, SoundView soundView) {
        // prepare new sound
        Sound sound = new Sound(soundPath, getPositionInMs(length), trackNumber, getPositionInMs(startPositionInWidth), soundPath, soundView);

        // delete oldTrack
        Track track = tracks.get(trackNumber);
        tracks.remove(trackNumber);

        // prepare track and save it
        track.prepareSound(sound, compositionView.getContext());
        tracks.add(trackNumber, track);

        mTracksTimer.updateTrack(trackNumber, track); //TODO noch brauchbar??!
    }


    public Track getActiveTrack() {
        return tracks.get(compositionView.getActiveTrack());
    }

    public void stopTrackRecorder(int soundLength) {
        getActiveTrack().stopTrackRecorder(getPositionInMs(soundLength));
    }

    public boolean isRecorderStoped() {
       boolean isStoped = getActiveTrack().getTrackRecorderStatus().equals(AudioRecorderStatus.STOPED);
       return isStoped;
    }

    public Map<SoundView, Integer> deleteSounds() {
        Map<SoundView, Integer> soundLengths = new HashMap<>();
        Track track = getActiveTrack();

        for (Sound sound : soundsToDelete) {
            SoundView soundView = sound.getSoundView();
            Integer trackNumber = soundView.getTrack();
            tracks.remove(trackNumber);
            track.deleteSound(sound);
            tracks.add(trackNumber, track);
            soundLengths.put(soundView, sound.getLength());
        }

        return soundLengths;
    }

    public void deleteSoundView(Map<SoundView, Integer> soundInfo) {
        soundInfo.forEach((soundView, soundLengthInMs) -> {
            int soundLengthInWidth = getSoundViewWidth(soundLengthInMs);
            compositionView.deleteSoundView(soundView, soundLengthInWidth / 3 * 0.17f);        //TODO diese geheime Zahl genau checken!
        });
    }

    public Sound selectSound(float xPosition) {
        Sound selectedSound = null;
        for (Sound sound : getActiveTrack().getSounds()) {
            int startPos = getSoundViewWidth(sound.getStartPosition());
            int endPos = startPos + getSoundViewWidth(sound.getLength());
            if (startPos <= xPosition && endPos >= xPosition) {
                selectedSound = sound;
                break;
            }
        }

        if (selectedSound != null) {
            soundsToDelete.add(selectedSound);
        }
        return selectedSound;
    }
}
