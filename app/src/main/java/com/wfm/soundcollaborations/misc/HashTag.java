package com.wfm.soundcollaborations.misc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Markus Eberts on 13.10.16.
 */
public enum HashTag {
    INSIDE("Innen"), OUTSIDE("Außen"), TRAFFIC("Verkehr"), VOICES("Stimmen"),
    MECANIC("Mechanisch"), MOVEMENT("Bewegung"), NATURE("Natur"),
    ANIMAS("Tiere"), HUMANS("Menschen");

    public String name;
    private static final Map<Integer, HashTag> tags = new HashMap<>();
    static {
        for (HashTag hashTag : HashTag.values()) {
            tags.put(hashTag.ordinal(), hashTag);
        }
    }

    private static final Map<String, HashTag> tagNames = new HashMap<>();
    static {
        for (HashTag hashTag : HashTag.values()) {
            tagNames.put(hashTag.getName(), hashTag);
        }
    }

    HashTag(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public static HashTag fromOrdinal(int ordinal){
        return tags.get(ordinal);
    }

    public static HashTag fromName(String name){
        return tagNames.get(name);
    }
}
