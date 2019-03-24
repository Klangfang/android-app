package com.wfm.soundcollaborations.webservice;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class CompositionResultServiceReceiver extends ResultReceiver {
    private ResultReceiver mResultReceiver;

    public CompositionResultServiceReceiver(Handler handler) {
        super(handler);
    }

    @Override
    public void send(int resultCode, Bundle resultData) {
        if (mResultReceiver != null) {
            mResultReceiver.send(resultCode, resultData);
        }
    }

    public ResultReceiver getmResultReceiver() {
        return mResultReceiver;
    }

    public void setmResultReceiver(ResultReceiver mResultReceiver) {
        this.mResultReceiver = mResultReceiver;
    }
}
