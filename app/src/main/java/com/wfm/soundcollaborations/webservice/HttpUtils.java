package com.wfm.soundcollaborations.webservice;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

/**
 * Http Utility methods that handle the interactive with the rest webservice api
 */
public class HttpUtils {

    //private static final String BASE_URL = "https://klangfang-service.herokuapp.com/compositions/compositionsOverview?page=0&size=5";

    private static final String BASE_URL = "http://localhost/compositions/1/pick";
    private static SyncHttpClient client = new SyncHttpClient();

    public static void get(JsonHttpResponseHandler responseHandler) {
        client.get(BASE_URL, responseHandler);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
