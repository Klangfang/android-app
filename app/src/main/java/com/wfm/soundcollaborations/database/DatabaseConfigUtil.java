package com.wfm.soundcollaborations.database;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Markus Eberts on 13.10.16.
 */

public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    private static final Class<?>[] classes = new Class[]{SoundEntity.class};

    public static void main(String[] args) throws IOException, SQLException {
        String currDirectory = "user.dir";
        String configPath = "/app/src/main/res/raw/ormlite_config.txt";

        String projectRoot = System.getProperty(currDirectory);
        String fullConfigPath = projectRoot + configPath;

        File configFile = new File(fullConfigPath);

        if (configFile.exists()) {
            configFile.delete();
            configFile = new File(fullConfigPath);
        }

        writeConfigFile(configFile, classes);
    }
}
