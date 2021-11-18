package fr.swansky.radioRythm.settings.exceptions;

import fr.swansky.radioRythm.settings.Language;

public class TranslationKeyNotFoundException extends Exception {


    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public TranslationKeyNotFoundException(Language language, String key) {
        super(String.format("Translation key: `%s` for language `%s` not found.", key, language.name()));
    }
}
