package org.kie.dmn.feel.util;

/**
 * Internal utility class.
 */
public final class MsgUtil {
    
    private MsgUtil() {
        // Constructing instances is not allowed for this class
    }

    public static String clipToString(Object source, int maxChars) {
        return source == null ? "null" : clipString(source.toString(), maxChars);
    }
    
    public static String clipString(String source, int maxChars) {
        if (source.length() <= maxChars) {
            return source;
        } else {
            return new StringBuilder().append(source.substring(0, maxChars))
                                      .append(String.format("... [string clipped after %s chars, total length is %s]", maxChars, source.length()))
                                      .toString();
        }
    }
}
