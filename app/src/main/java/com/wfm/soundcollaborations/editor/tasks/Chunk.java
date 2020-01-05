package com.wfm.soundcollaborations.editor.tasks;

class Chunk {

    private short[] chunk;

    Chunk(short[] chunk)
    {
        this.chunk = chunk;
    }

    short[] getChunk()
    {
        return this.chunk;
    }
}
