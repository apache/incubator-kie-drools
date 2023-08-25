/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.serverless.workflow.io;

import java.io.File;
import java.net.URI;

public enum URIContentLoaderType {
    CLASSPATH(),
    FILE(File.separatorChar),
    HTTP();

    private final char[] additionalSeparators;

    private URIContentLoaderType(char... additionalSeparators) {
        this.additionalSeparators = additionalSeparators;
    }

    public static URIContentLoaderType from(URI uri) {
        String scheme = uri.getScheme();
        if (scheme == null) {
            return FILE;
        }
        switch (uri.getScheme().toLowerCase()) {
            case "file":
                return FILE;
            case "classpath":
                return CLASSPATH;
            case "http":
            case "https":
                return HTTP;
            default:
                throw new IllegalArgumentException("Unrecognized uri protocol " + uri);
        }
    }

    public boolean isAbsolutePath(String path) {
        if (!path.isBlank()) {
            char firstChar = path.trim().charAt(0);
            boolean result = firstChar == '/';
            for (int i = 0; !result && i < additionalSeparators.length; i++) {
                result = firstChar == additionalSeparators[i];
            }
            return result;
        }
        return false;
    }

    public String concat(String basePath, String additionalPath) {
        char separator = separator();
        if (!basePath.isBlank() && !isAbsolutePath(basePath)) {
            basePath = separator + basePath;
        }
        return basePath + separator + additionalPath;
    }

    public String trimLast(String path) {
        int indexOf = lastIndexOf(path);
        return indexOf != -1 ? path.substring(0, indexOf) : "";
    }

    public String lastPart(String path) {
        int indexOf = lastIndexOf(path);
        return indexOf != -1 ? path.substring(indexOf + 1) : path;
    }

    private int lastIndexOf(String path) {
        int indexOf = path.lastIndexOf('/');
        int i = 0;
        while (indexOf == -1 && i < additionalSeparators.length) {
            indexOf = path.lastIndexOf(additionalSeparators[i++]);
        }
        return indexOf;
    }

    private char separator() {
        return additionalSeparators.length > 0 ? additionalSeparators[0] : '/';
    }
}
