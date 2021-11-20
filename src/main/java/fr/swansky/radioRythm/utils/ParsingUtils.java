package fr.swansky.radioRythm.utils;

public class ParsingUtils {
    public static boolean IsInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }
}
