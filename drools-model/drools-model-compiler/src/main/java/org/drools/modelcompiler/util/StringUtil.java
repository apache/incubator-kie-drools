package org.drools.modelcompiler.util;

public class StringUtil {

    public static String toId(String id) {
        String fixStart = id;
        if ( !Character.isJavaIdentifierStart(fixStart.charAt(0)) ) {
            fixStart = "_" + fixStart;
        }
        fixStart = fixStart.replaceAll("_", "__");
        StringBuilder result = new StringBuilder();
        char[] cs = fixStart.toCharArray();
        for ( char c : cs ) {
            if ( Character.isJavaIdentifierPart(c) ) {
                result.append(c);
            } else {
                result.append("_" + Integer.valueOf(c));
            }
        }
        return result.toString();
    }

    public static String fileNameToClass(String fileName) {
        return fileName.substring( 0, fileName.length() - ".class".length() ).replace( '/', '.' );
    }

}
