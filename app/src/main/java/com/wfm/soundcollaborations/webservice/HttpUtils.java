package com.wfm.soundcollaborations.webservice;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpUtils {

    //private static final String BASE_URL = "https://klangfang-service.herokuapp.com/compositions/compositionsOverview?page=0&size=5";

    private static final String BASE_URL = "https://klangfang-service.herokuapp.com/compositions/1/pick";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(AsyncHttpResponseHandler responseHandler) {
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
