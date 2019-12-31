package com.wfm.soundcollaborations.Editor.model.composition;

import com.wfm.soundcollaborations.Editor.model.composition.sound.LocalSound;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

class Composition {

    private static final String TAG = Composition.class.getSimpleName();

    private final Long compositionId;
    private String title;
    private CompositionStatus status = CompositionStatus.READY;
    private List<Track> tracks;

    public static class CompositionConfigurer {

        private Long compositionId;
        private String title;
        private List<Track> tracks = new ArrayList<>();


        CompositionConfigurer() {

            for (int i = 0; i < 4; i++) {

                Track track = new Track(i);
                tracks.add(track);

            }

        }


        CompositionConfigurer title(String title) {

            this.title = title;
            return this;

        }


        CompositionConfigurer compositionId(long compositionId) {

            this.compositionId = compositionId;
            return this;

        }


        public Composition build() {

            return new Composition(compositionId, title, tracks);

        }

    }


    private Composition(Long compositionId,
                        String title,
                        List<Track> tracks) {

        this.compositionId = compositionId;
        this.title = title;
        this.tracks = tracks;

    }


    int getMaxAmplitude(int activeTrackIndex) {

        return tracks.get(activeTrackIndex).getMaxAmplitude();

    }


    Stream<LocalSound> getLocalSounds() {

        return tracks.stream()
                .map(Track::getLocalSounds)
                .flatMap(Function.identity());

    }


    boolean isPlaying() {

        return tracks.stream()
                .anyMatch(Track::isPlaying);

    }


    List<Track> getTracks() {

        return tracks;

    }


    Track getTrack(int trackNumber) {

        return tracks.get(trackNumber);

    }


    Track updateTrack(int trackNumber, Track updatedTrack) {

        return tracks.set(trackNumber, updatedTrack);

    }


    boolean isReady() {

        return status.equals(CompositionStatus.READY);

    }


    void exhausted() {

        this.status = CompositionStatus.EXHAUSTED;

    }


    Long getCompositionId() {

        return compositionId;

    }


    String getTitle() {

        return title;

    }

}
