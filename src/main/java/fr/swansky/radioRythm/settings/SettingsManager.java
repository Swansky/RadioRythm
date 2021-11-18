package fr.swansky.radioRythm.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import fr.swansky.radioRythm.settings.exceptions.SettingsFileNotFoundException;
import fr.swansky.radioRythm.settings.exceptions.SettingsInvalidJsonException;
import org.apache.log4j.Logger;

import java.io.*;

import static fr.swansky.radioRythm.constants.Constants.SETTINGS_FILE_URL;

public class SettingsManager {
    private static final Logger LOGGER = Logger.getLogger(SettingsManager.class);
    private static final Gson GSON = new GsonBuilder().create();


    public static Settings loadSettings() {
        Settings settings = null;
        File settingsFile = new File(SETTINGS_FILE_URL + "Settings.json");
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(settingsFile));

            settings = GSON.fromJson(jsonReader, Settings.class);

        } catch (FileNotFoundException e) {
            LOGGER.fatal("", new SettingsFileNotFoundException());

        } catch (JsonIOException | JsonSyntaxException e) {
            LOGGER.fatal("", new SettingsInvalidJsonException());
        }
        return settings;
    }

    public static void createSampleSettings() {
        File settingsFile = new File(SETTINGS_FILE_URL + "Settings.json");
        if (settingsFile.exists()) {
            LOGGER.fatal("settings file already exist ! ");
            return;
        }
        Settings settings = new Settings("", "", Language.ENG);
        try {
            FileWriter fileWriter = new FileWriter(settingsFile);
            GSON.toJson(settings, fileWriter);
            fileWriter.close();
            LOGGER.info("Settings file have been created.");
        } catch (IOException e) {
            LOGGER.fatal("", e);
        }
    }

}
