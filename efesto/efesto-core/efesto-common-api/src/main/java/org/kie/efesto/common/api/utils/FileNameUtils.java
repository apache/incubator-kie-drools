package org.kie.efesto.common.api.utils;

import java.io.File;

public class FileNameUtils {
    private FileNameUtils() {
    }

    public static String getFileName(String source) {
        return source.contains(File.separator) ?
                source.substring(source.lastIndexOf(File.separatorChar) + 1) : source;
    }

    public static String getSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public static String removeSuffix(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }
}
