package com.wfm.soundcollaborations.webservice;


/**
 * Http Utility methods that handle the interactive with the rest webservice api
 */
public class HttpUtils {


    public static final String COMPOSITION_SERVICE_BASE_URL = "https://klangfang-service.herokuapp.com/compositions/";
    public static final String COMPOSITION_VIEW_URL = COMPOSITION_SERVICE_BASE_URL + "compositionsOverview?page=0&size=5";
    public static final String COMPOSITION_PICK_URL = "/pick";
    public static final String COMPOSITION_RELEASE_URL = "/release";

}
