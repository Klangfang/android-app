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
                String serverFilePath = soundObj.getString("filePath");
                Integer duration = soundObj.getInt("duration");
                Integer trackNumber = soundObj.getInt("trackNumber") - 1;
                Integer startPosition = soundObj.getInt("start_position");
                String name = serverFilePath.split("/")[serverFilePath.split("/").length - 1];
                String filePath = FileUtils.getKlangfangCacheDirectory()+"/" + name;
                Sound sound = new Sound(trackNumber, startPosition, duration, filePath);
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
