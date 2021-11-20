package fr.swansky.radioRythm.settings.exceptions;

import fr.swansky.radioRythm.settings.Language;

public class TranslationInvalidPropertiesException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public TranslationInvalidPropertiesException(Language language, String line) {
        super(String.format("Invalid properties file for language '%s' line: '%s'", language.name(), line));
    }
}
