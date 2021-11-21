package fr.swansky.radioRythm.settings;

public class Settings {

    private final String token;
    private final String tag;
    private final Language language;

    public Settings(String token, String tag, Language language) {
        this.token = token;
        this.tag = tag;
        this.language = language;
    }

    public Settings(String token, String tag) {
        this.token = token;
        this.tag = tag;
        this.language = Language.FR;
    }

    public String getToken() {
        return token;
    }

    public String getTag() {
        return tag;
    }

    public Language getLanguage() {
        return language;
    }

    public boolean isEmpty() {
        if (this.tag.isBlank() || this.token.isBlank() || this.language == null) {
            return true;
        }
        return false;
    }
}
