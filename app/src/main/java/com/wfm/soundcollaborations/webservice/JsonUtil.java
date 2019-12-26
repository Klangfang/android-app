package com.wfm.soundcollaborations.webservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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


    public static <T> String toJson(T object) {

        String jsonValue = "";
        try {
            jsonValue = OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            System.err.println(e);
        }

        return jsonValue;

    }


    public static <T> List<File> toJsonFile(String filename, T object) {

        List<File> tmpFiles = new ArrayList<>();
        try {
            File tempFile = File.createTempFile(filename + "-", ".json");
            OBJECT_MAPPER.writeValue(tempFile, object);
            tmpFiles.add(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tmpFiles;

    }
}
