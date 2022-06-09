package org.optaplanner.examples.common.business;

import java.io.File;
import java.io.FileFilter;

public class ExtensionFileFilter implements FileFilter {

    private final String extensionWithDot;

    public ExtensionFileFilter(String extension) {
        extensionWithDot = "." + extension;
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory() || file.isHidden()) {
            return false;
        }
        return file.getName().endsWith(extensionWithDot);
    }

}
