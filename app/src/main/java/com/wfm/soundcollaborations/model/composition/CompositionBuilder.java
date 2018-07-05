package com.wfm.soundcollaborations.model.composition;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.wfm.soundcollaborations.activities.EditorActivity;
import com.wfm.soundcollaborations.exceptions.NoActiveTrackException;
import com.wfm.soundcollaborations.exceptions.SoundWillBeOutOfCompositionException;
import com.wfm.soundcollaborations.exceptions.SoundWillOverlapException;
import com.wfm.soundcollaborations.network.SoundDownloader;
import com.wfm.soundcollaborations.tasks.VisualizeSoundTask;
import com.wfm.soundcollaborations.utils.FileUtils;
import com.wfm.soundcollaborations.views.composition.CompositionView;
import com.wfm.soundcollaborations.views.composition.SoundView;
import com.wfm.soundcollaborations.views.composition.TrackView;
import com.wfm.soundcollaborations.views.composition.TrackWatchView;
import com.wfm.soundcollaborations.views.composition.listeners.TrackViewOnClickListener;
import com.wfm.soundcollaborations.views.composition.listeners.TrackWatchViewOnClickListener;

import java.util.ArrayList;
import java.util.List;

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
    private ArrayList<TrackView> tracksViews;
    private ArrayList<SoundView> soundsViews;

    private ArrayList<Sound> msounds;
    private SoundDownloader downloader;

    private int numberOfDownloadedSounds = 0;
    private ArrayList<Track> mTracks;

    private TracksTimer mTracksTimer;
    private boolean playing = false;

    public CompositionBuilder(CompositionView compositionView, int tracks)
    {
        this.compositionView = compositionView;
        tracksViews = new ArrayList<>();
        soundsViews = new ArrayList<>();
        msounds = new ArrayList<>();
        mTracks = new ArrayList<>();
        initTracksViews(tracks);
        initTracks(tracks);
    }

    private void initTracksViews(int tracks)
    {
        for(int i=0; i<tracks; i++)
        {
            TrackView trackView = new TrackView(this.compositionView.getContext());
            tracksViews.add(trackView);
        }
    }

    private void initTracks(int tracks)
    {
        for(int i=0; i<tracks; i++)
        {
            Track track = new Track();
            mTracks.add(track);
        }
        mTracksTimer = new TracksTimer(mTracks, this.compositionView);
    }

    public void addSounds(List<Sound> sounds)
    {
        msounds.addAll(sounds);
        for(int i=0; i<sounds.size(); i++)
        {
            // create sounds view
            SoundView soundView = new SoundView(this.compositionView.getContext());
            RelativeLayout.LayoutParams soundParams =
                    new RelativeLayout.LayoutParams(getSoundViewWidth(sounds.get(i).getLength()), TRACK_HEIGHT);
            soundParams.setMargins(getSoundViewMargin(sounds.get(i).getStartPosition()), 0, 0, 0);
            soundView.setLayoutParams(soundParams);
            soundView.setTrack(sounds.get(i).getTrack());
            // add soundView to the list
            soundsViews.add(soundView);
            // add sound view to the track
            tracksViews.get(sounds.get(i).getTrack()).addSoundView(soundView);
        }
        // we will add everything to the composition view
        build();
        downloadSounds();
    }

    private int getSoundViewWidth(int lengthInMs)
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

    private int getStartPositionInMs(int width) {
        // Factor um Breite in Millisekunden zu konvertieren: Hat sich automatisch ergeben
        final double WIDTH_TO_MS_FACTOR = 16.6667;

        int startPositionInMs = (int) (width * WIDTH_TO_MS_FACTOR);
        return startPositionInMs;
    }

    private void build()
    {
        for(int i=0; i<tracksViews.size(); i++)
        {
            // watches
            TrackWatchView trackWatchView = new TrackWatchView(this.compositionView.getContext());
            trackWatchView.setOnClickListener(new TrackWatchViewOnClickListener(this.compositionView, i));
            LinearLayout.LayoutParams watchParams  = new LinearLayout.LayoutParams(TRACK_HEIGHT, TRACK_HEIGHT);
            watchParams.setMargins(10, 10, 0, 10);
            trackWatchView.setLayoutParams(watchParams);
            // tracks
            TrackView trackView = tracksViews.get(i);
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
                        VisualizeSoundTask soundTask = new VisualizeSoundTask(soundsViews.get(index), msounds.get(index));
                        soundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        String name = msounds.get(index).getLink().split("/")[msounds.get(index).getLink().split("/")
                                .length - 1];
                        msounds.get(index).setUri(FileUtils.getKlangfangCacheDirectory()+"/"+name);
                        mTracks.get(msounds.get(index).getTrack()).addSound(msounds.get(index));
                        numberOfDownloadedSounds++;
                        if(numberOfDownloadedSounds == msounds.size()) {
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
        for(int i=0; i<msounds.size(); i++)
            this.downloader.addSoundUrl(msounds.get(i).getLink(), i);

        this.downloader.download();
    }

    private void prepareTracks()
    {
        for(int i=0; i<mTracks.size(); i++)
            mTracks.get(i).prepare(this.compositionView.getContext());
    }

    public SoundView record(Context context) throws Exception
    {
        int activeTrack = this.compositionView.getActiveTrack();
        if( activeTrack == -1)
            throw new NoActiveTrackException();

        int pos = this.compositionView.getScrollPosition();
        if((pos + SOUND_SECOND_WIDTH) > TRACK_WIDTH)
            throw new SoundWillBeOutOfCompositionException();

        isThereAnyOverlapping(pos, activeTrack);

        SoundView recordingSoundView = new SoundView(context);
        recordingSoundView.setTrack(activeTrack);
        RelativeLayout.LayoutParams soundParams =
                new RelativeLayout.LayoutParams(0, TRACK_HEIGHT);
        soundParams.setMargins(pos, 0, 0, 0);
        recordingSoundView.setLayoutParams(soundParams);
        recordingSoundView.setYellowBackground();

        tracksViews.get(activeTrack).addSoundView(recordingSoundView);

        return recordingSoundView;
    }

    public boolean isThereAnyOverlapping(int pos, int activeTrack) //throws SoundWillOverlapException
    {
        int margin, width;
        boolean overlap;
        //for(int i=0; i<soundsViews.size(); i++)
        for (Sound sound : msounds)
        {
            overlap = false;
            margin = getSoundViewMargin(sound.getStartPosition());
            width = getSoundViewWidth(sound.getLength());

            // check one second forward
            // check if indicator above above track
            if( (pos <= margin && (pos + SOUND_SECOND_WIDTH) >= margin) || (pos <= (margin+width) && pos >= margin))
            {
                overlap = true;
            }

            if(overlap && sound.getTrack() == activeTrack) {
                //throw new SoundWillOverlapException();
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

    public void addRecordedSound(String soundPath, int length, int trackNumber, int startPositionInWidth) {
        Sound sound = new Sound(soundPath, length, trackNumber, getStartPositionInMs(startPositionInWidth));
        sound.setUri(soundPath);

        /*Track track = mTracks.get(trackNumber);
        mTracks.remove(trackNumber);

        track.prepareSound(sound);
        mTracks.add(track);*/

        mTracksTimer.updateTrack(trackNumber, sound);

    }

    public Track getTrack(int trackNumber) {
        return mTracks.get(trackNumber);
    }
}
