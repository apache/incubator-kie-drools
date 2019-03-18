package org.drools.mvelcompiler;

// TODO this class has to be deleted
public class SemicolonSanitizer {

    public static String sanitizeMvelScript(String mvelScript) {
        String[] split = mvelScript.split("\n");

        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (String s : split) {
            String trimmed = s.trim();
            builder.append(s);
            if (!trimmed.endsWith(";")
                    && !trimmed.startsWith("modify")) {
                builder.append(";");
            }
        }
        builder.append("}");
        return builder.toString();
    }
}
