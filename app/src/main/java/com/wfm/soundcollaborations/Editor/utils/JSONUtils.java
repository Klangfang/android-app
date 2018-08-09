package com.wfm.soundcollaborations.Editor.utils;

import com.wfm.soundcollaborations.Editor.model.composition.Sound;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohammed on 10/27/17.
 */

public class JSONUtils
{
    public static List<Sound> getSounds(String jsonData)
    {
        List<Sound> sounds = new ArrayList<>();
        try
        {
            JSONObject data = new JSONObject(jsonData);
            JSONArray jsonSounds = data.getJSONArray("sounds");
            for(int i=0; i<jsonSounds.length(); i++)
            {
                JSONObject soundObj = jsonSounds.getJSONObject(i);
                String link = soundObj.getString("link");
                int length = soundObj.getInt("length");
                int track = soundObj.getInt("track") - 1;
                int startPosition = soundObj.getInt("start_position");
                String name = link.split("/")[link.split("/").length - 1];
                String uri = FileUtils.getKlangfangCacheDirectory()+"/" + name;
                Sound sound = new Sound(link, length, track, startPosition, uri);
                sounds.add(sound);
            }

        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }

        return sounds;
    }
}
