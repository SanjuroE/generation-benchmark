package dev.sanjuroe.generation.runtime;

public class ReflectionUtils {

    public static String determineSetter(String fieldName) {
        var sb = new StringBuilder(fieldName.length() + 3);
        sb.append("set");
        sb.append(Character.toUpperCase(fieldName.charAt(0)));
        sb.append(fieldName.substring(1));
        return sb.toString();
    }
}
