package com.wfm.soundcollaborations.Editor.model.composition;

/**
 * Created by mohammed on 10/27/17.
 */

public class Sound
{
    private String uri = "";
    private String link;
    private int length;
    private int track;
    private int startPosition;

    public Sound(String link, int length, int track, int startPosition)
    {
        this.link = link;
        this.length = length;
        this.track = track;
        this.startPosition = startPosition;
    }

    public int getLength()
    {
        return this.length;
    }

    public String getLink()
    {
        return this.link;
    }

    public int getTrack()
    {
        return this.track;
    }

    public int getStartPosition()
    {
        return this.startPosition;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public String getUri() throws NullPointerException
    {
        if(this.uri.isEmpty())
            throw new NullPointerException("No Uri found for the sound file!");
        return this.uri;
    }
}
