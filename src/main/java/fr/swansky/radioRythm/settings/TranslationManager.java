package fr.swansky.radioRythm.settings;

import fr.swansky.radioRythm.settings.exceptions.TranslationFileNotFoundException;
import fr.swansky.radioRythm.settings.exceptions.TranslationInvalidPropertiesException;
import fr.swansky.radioRythm.settings.exceptions.TranslationKeyNotFoundException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TranslationManager {
    private static final Logger LOGGER = Logger.getLogger(TranslationManager.class);
    private final Language language;
    private final Map<String, String> translationMap = new HashMap<>();

    public TranslationManager(@NotNull Language language) {
        this.language = language;
        loadPropertiesFile();
    }

    private void loadPropertiesFile() {
        String path = "/languages/" + language.getTranslationFileName() + ".language";
        URL resource = getClass().getResource(path);

        if (resource == null) {
            LOGGER.fatal("", new TranslationFileNotFoundException(language, path));
            return;
        }
        File propertiesFile = new File(resource.getFile());

        try {
            String s = FileUtils.readFileToString(propertiesFile,StandardCharsets.UTF_8);
            System.out.println(s);
            String[] split = s.split("\n");
            for (String line : split) {
                if (line.isBlank())
                    continue;
                if (!line.contains("=")) {
                    throw new TranslationInvalidPropertiesException(language, line);
                }
                String[] keyValue = line.split("=");
                String key = keyValue[0];
                String value = keyValue[1];
                key = key.replace(" ", "");
                translationMap.put(key, value);
            }
        } catch (Exception e) {
            LOGGER.fatal("", e);
            e.printStackTrace();
        }
    }

    public String getTranslationByKey(@NotNull String key) throws TranslationKeyNotFoundException {
        key = key.replace(" ", "");
        if (!translationMap.containsKey(key)) {
            throw new TranslationKeyNotFoundException(language, key);
        }
        return translationMap.get(key);
    }

    public @NotNull Language getLanguage() {
        return language;
    }

}
