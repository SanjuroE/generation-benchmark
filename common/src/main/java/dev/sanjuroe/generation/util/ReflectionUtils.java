package dev.sanjuroe.generation.util;

public class ReflectionUtils {

    public static String determineSetter(String fieldName) {
        var sb = new StringBuilder(fieldName.length() + 3);
        sb.append("set");
        sb.append(Character.toUpperCase(fieldName.charAt(0)));
        sb.append(fieldName.substring(1));
        return sb.toString();
    }

    public static String determineParseMethod(Class<?> type) {
        if (String.class.equals(type)) {
            return "readString";
        } else if (int.class.equals(type)) {
            return "readInteger";
        } else if (boolean.class.equals(type)) {
            return "readBoolean";
        } else {
            throw new IllegalArgumentException("Unsupported type");
        }
    }
}
