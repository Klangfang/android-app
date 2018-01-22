package com.wfm.soundcollaborations.model.audio;

/**
 * Created by mohammed on 11/3/17.
 */

public class AudioVisualizer
{
    private static final String TAG = AudioVisualizer.class.getSimpleName();

    private int counter=0;
    private int max = 0;

    private int sampleRate = 0;
    private int channels = 0;

    public AudioVisualizer()
    {

    }

    public interface listener
    {
        void valueCaptured(int value);
    }

    public void setSampleRate(int sampleRate)
    {
        this.sampleRate = sampleRate;
    }

    public void setChannelsNumber(int channels)
    {
        this.channels = channels;
    }

    public void visualize(short[] chunk, int howManyValuesInSecond, listener listener)
    {
        long step = (this.sampleRate * this.channels ) / (howManyValuesInSecond );
        for(short value: chunk)
        {
            if(value > max)
                max = value;
            this.counter++;
            if(this.counter > step)
            {
                this.counter = 0;
                listener.valueCaptured(this.max);
                this.max = 0;
            }
        }
    }
}