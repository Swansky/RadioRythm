package fr.swansky.radioRythm.settings;

import org.jetbrains.annotations.NotNull;
import fr.swansky.radioRythm.settings.exceptions.TranslationKeyNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class Translation {
    private final Language language;
    private final Map<String, String> translationMap = new HashMap<>();

    public Translation(@NotNull Language language) {
        this.language = language;
    }

    public void registerTranslationKey(@NotNull String key, @NotNull String translation) {
        translationMap.put(key, translation);
    }

    public @NotNull String getTranslationByKey(@NotNull String key) throws TranslationKeyNotFoundException {
        if (!translationMap.containsKey(key)) {
            throw new TranslationKeyNotFoundException(language, key);
        }
        return translationMap.get(key);
    }

    public @NotNull Language getLanguage() {
        return language;
    }

}
