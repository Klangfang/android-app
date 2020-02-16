package com.wfm.soundcollaborations.interaction.editor.tasks;

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
