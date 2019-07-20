package com.wfm.soundcollaborations.webservice;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public final class JsonUtil {

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> T fromJson(String json, Class<T> clazz) {

        T value = null;
        try {
            value = OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            System.err.println(e);
        }

        return value;
    }
}
