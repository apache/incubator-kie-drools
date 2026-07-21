/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.source.files;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;

public final class SourceFile {

    // Serialization requires it not read-only
    private String uri;

    public SourceFile() {
        // Needed for serialization
    }

    /**
     * Creates a new SourceFile with the given URI.
     * Ex.: {@code new SourceFile("path/to/file.txt")} will create a SourceFile with URI {@code path/to/file.txt}.
     *
     * @param uri the URI of the source file
     */
    public SourceFile(String uri) {
        this.uri = toPosixPath(Path.of(Objects.requireNonNull(uri)));
    }

    // Needed for serialization
    public void setUri(String uri) {
        this.uri = toPosixPath(Path.of(uri));
    }

    public String getUri() {
        return uri;
    }

    public static String toPosixPath(Path path) {
        if (path == null) {
            return null;
        }

        if (path.getFileSystem().getSeparator().equals("/")) {
            return path.toString();
        }

        return path.toString().replace(path.getFileSystem().getSeparator(), "/");
    }

    public byte[] readContents() throws IOException {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(getUri())) {
            if (inputStream == null) {
                throw new FileNotFoundException(getUri() + " could not be found.");
            }

            return inputStream.readAllBytes();
        }
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
