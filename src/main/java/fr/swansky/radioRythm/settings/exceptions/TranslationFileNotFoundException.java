package fr.swansky.radioRythm.settings.exceptions;

import fr.swansky.radioRythm.settings.Language;

public class TranslationFileNotFoundException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public TranslationFileNotFoundException(Language language, String path) {
        super(String.format("Translation file for '%s' not found. (path: %s)", language.name(), path));
    }
}
