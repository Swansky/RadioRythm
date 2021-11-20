package fr.swansky.radioRythm.settings;

import fr.swansky.radioRythm.settings.exceptions.TranslationFileNotFoundException;
import fr.swansky.radioRythm.settings.exceptions.TranslationInvalidPropertiesException;
import fr.swansky.radioRythm.settings.exceptions.TranslationKeyNotFoundException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
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
        String path = "/languages/" + language.getTranslationFileName() + ".properties";
        InputStream resourceAsStream = getClass().getResourceAsStream(path);
        if (resourceAsStream == null) {
            LOGGER.fatal("", new TranslationFileNotFoundException(language, path));
            return;
        }
        try {
            String s = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
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
