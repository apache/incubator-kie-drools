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
package org.kie.kogito.addon.source.files;

import java.util.Objects;

public final class SourceFile {

    public static final String SOURCES_HTTP_PATH = "/sources/";

    // Serialization requires it not read-only
    private String uri;

    public SourceFile() {
        // Needed for serialization
    }

    /**
     * Creates a new SourceFile with the given URI under the {@link SourceFile#SOURCES_HTTP_PATH}.
     * Ex.: {@code new SourceFile("path/to/file.txt")} will create a SourceFile with URI {@code SourceFilesProvider.SOURCES_HTTP_PATH + path/to/file.txt}.
     *
     * @param uri the URI of the source file under the {@link SourceFile#SOURCES_HTTP_PATH}
     */
    public SourceFile(String uri) {
        this.uri = SOURCES_HTTP_PATH + Objects.requireNonNull(uri);
    }

    // Needed for serialization
    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SourceFile that = (SourceFile) o;
        return uri.equals(that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }

    @Override
    public String toString() {
        return "SourceFile{" +
                "uri='" + uri + '\'' +
                '}';
    }
}
