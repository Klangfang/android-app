package com.wfm.soundcollaborations.interaction.editor.model.composition;

import com.wfm.soundcollaborations.interaction.editor.model.composition.sound.LocalSound;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

class Composition {

    private static final String TAG = Composition.class.getSimpleName();

    private final Long compositionId;
    private String title;
    private boolean collaboration;
    private CompositionStatus status = CompositionStatus.OPENED;
    private List<Track> tracks;


    public static class CompositionConfigurer {

        private Long compositionId;
        private String title;
        private boolean collaboration;
        private List<Track> tracks = new ArrayList<>();


        CompositionConfigurer() {

            for (int i = 0; i < 4; i++) {

                Track track = new Track(i);
                tracks.add(track);

            }

        }


        CompositionConfigurer collaboration() {

            this.collaboration = true;
            return this;

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

            return new Composition(compositionId, title, collaboration, tracks);

        }

    }


    private Composition(Long compositionId,
                        String title,
                        boolean collaboration,
                        List<Track> tracks) {

        this.compositionId = compositionId;
        this.title = title;
        this.collaboration = collaboration;
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


    boolean isOpened() {

        return status.equals(CompositionStatus.OPENED);

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


    boolean isCollaboration() {

        return collaboration;

    }


    boolean isNotCanceled() {

        return !status.equals(CompositionStatus.CANCELED);

    }


    void cancel() {

        status = CompositionStatus.CANCELED;

    }


    boolean isNotExhausted() {

        return !status.equals(CompositionStatus.EXHAUSTED);
    }

}
