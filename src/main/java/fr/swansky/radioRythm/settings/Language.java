package fr.swansky.radioRythm.settings;

public enum Language {
    FR("FR"),
    ENG("ENG");

    private final String translationFileName;

    Language(String translationFileName) {
        this.translationFileName = translationFileName;
    }

    public String getTranslationFileName() {
        return translationFileName;
    }
}
