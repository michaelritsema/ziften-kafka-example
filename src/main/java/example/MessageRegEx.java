package example;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageRegEx {
    static final Pattern contentPattern = Pattern.compile(">.*<");
    static final Pattern typePattern = Pattern.compile("type=\"[^\"]*");

    public static String extractContent(String message) {
        Matcher matcher = contentPattern.matcher(message);
        matcher.find();
        return matcher.group().substring(1, matcher.group().length() - 1);
    }

    public static String extractType(String message) {
        Matcher typeMatcher = typePattern.matcher(message);
        typeMatcher.find();
        return typeMatcher.group().substring(6);
    }
}