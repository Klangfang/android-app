package com.wfm.soundcollaborations.model.audio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by mohammed on 11/3/17.
 * Works Perfect for 3GPP audio format.
 */

public class AudioDecoder
{
    private static final String TAG = AudioDecoder.class.getSimpleName();

    private MediaExtractor mMediaExtractor;
    private MediaCodec mMediaCodec;
    private ByteBuffer[] codecInputBuffers;
    private ByteBuffer[] codecOutputBuffers;

    public AudioDecoder()
    {

    }

    public interface Listener
    {
        void onAudioDurationDetected(long audioLengthMilli);
        void onSampleRateAndChannelsDetected(int sampleRate, int channels);
        void onRawChunkDecoded(byte[] chunk);
        void onShortChunkData(short[] chunk);
    }

    public void decode(String filePath, Listener listener)
    {
        try
        {
            setDataSource(filePath);
            decode(listener);
        }
        catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage());
        }
    }

    public void decode(Context context, int fileId, Listener listener)
    {
        try
        {
            setDataSource(context, fileId);
            decode(listener);
        }
        catch (Exception ex)
        {
            Log.e(TAG, Log.getStackTraceString(ex));
        }
    }

    private void setDataSource(String filePath) throws IOException
    {
        mMediaExtractor = new MediaExtractor();
        mMediaExtractor.setDataSource(filePath);
    }

    private void setDataSource(Context context, int fileId) throws IOException
    {
        AssetFileDescriptor sampleFD = context.getResources().openRawResourceFd(fileId);
        mMediaExtractor = new MediaExtractor();
        mMediaExtractor.setDataSource(sampleFD.getFileDescriptor(), sampleFD.getStartOffset(), sampleFD.getLength());
    }

    private void decode(Listener listener) throws IOException
    {
        MediaFormat format = mMediaExtractor.getTrackFormat(0);
        String mime = format.getString(MediaFormat.KEY_MIME);

        mMediaCodec = MediaCodec.createDecoderByType(mime);
        mMediaCodec.configure(format, null, null, 0);
        mMediaCodec.start();
        codecInputBuffers = mMediaCodec.getInputBuffers();
        codecOutputBuffers = mMediaCodec.getOutputBuffers();

        long duration = (long) Math.ceil(format.getLong(MediaFormat.KEY_DURATION) / 1000.0); // milli seconds
        listener.onAudioDurationDetected(duration);

        int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        int channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);

        listener.onSampleRateAndChannelsDetected(sampleRate, channels);

        mMediaExtractor.selectTrack(0);

        final long kTimeOutUs = 10000;
        MediaCodec.BufferInfo BufInfo = new MediaCodec.BufferInfo();
        boolean sawInputEOS = false;
        boolean sawOutputEOS = false;

        int inputBufIndex;

        Log.d(TAG, "Decoding Started!");
        while (!sawOutputEOS)
        {
            if (!sawInputEOS)
            {
                inputBufIndex = mMediaCodec.dequeueInputBuffer(kTimeOutUs);
                if (inputBufIndex >= 0)
                {
                    ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];
                    int sampleSize = mMediaExtractor.readSampleData(dstBuf, 0);
                    long presentationTimeUs = 0;

                    if (sampleSize < 0)
                    {
                        sawInputEOS = true;
                        sampleSize = 0;
                    }
                    else
                    {
                        presentationTimeUs = mMediaExtractor.getSampleTime();
                    }

                    mMediaCodec.queueInputBuffer(inputBufIndex, 0,
                            sampleSize, presentationTimeUs,
                            sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM
                                    : 0);

                    if (!sawInputEOS)
                    {
                        mMediaExtractor.advance();
                    }
                }
            }

            int res = mMediaCodec.dequeueOutputBuffer(BufInfo, kTimeOutUs);

            if (res >= 0)
            {
                int outputBufIndex = res;
                ByteBuffer buf = codecOutputBuffers[outputBufIndex];

                final byte[] chunk = new byte[BufInfo.size];
                buf.get(chunk);
                buf.clear();

                if (chunk.length > 0)
                {
                    Log.d(TAG, "Chunk decoded length -> "+chunk.length);
                    listener.onRawChunkDecoded(chunk);
                    listener.onShortChunkData(convertBytesTo16BitShortArray(chunk));
                }
                mMediaCodec.releaseOutputBuffer(outputBufIndex, false);

                if ((BufInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
                {
                    sawOutputEOS = true;
                }
            }
            else if (res == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED)
            {
                codecOutputBuffers = mMediaCodec.getOutputBuffers();
                Log.i(TAG, "Output buffers have changed!");
            }
            else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED)
            {
                MediaFormat newFormat = mMediaCodec.getOutputFormat();
                Log.i(TAG, "Output format has changed to "+newFormat);
            }
        }

        Log.d(TAG, "Audio Decoder Finished!");
        mMediaCodec.stop();
        mMediaCodec.release();
    }

    private short[] convertBytesTo16BitShortArray(byte[] buffer)
    {
        short[] shortArray = new short[buffer.length / 2];
        for (int i=0; i<shortArray.length; i++)
        {
            ByteBuffer bb = ByteBuffer.allocate(2);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.put(buffer[2*i]);
            bb.put(buffer[2*i + 1]);
            shortArray[i] = bb.getShort(0);
        }
        return shortArray;
    }

}
