package com.wfm.soundcollaborations.Editor.tasks;

/**
 * Created by mohammed on 11/9/17.
 */

public class Chunk
{
    private short[] chunk;

    public Chunk(short[] chunk)
    {
        this.chunk = chunk;
    }

    public short[] getChunk()
    {
        return this.chunk;
    }
}
