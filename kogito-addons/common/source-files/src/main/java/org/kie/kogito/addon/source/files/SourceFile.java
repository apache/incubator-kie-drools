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

    // Serialization requires it not read-only
    private String uri;

    // Serialization requires it not read-only
    private String contents;

    public SourceFile() {
        // Needed for serialization
    }

    /**
     * Creates a new SourceFile with the given URI.
     * Ex.: {@code new SourceFile("path/to/file.txt")} will create a SourceFile with URI {@code path/to/file.txt}.
     *
     * @param uri the URI of the source file
     * @param contents the contents of the source file
     */
    public SourceFile(String uri, String contents) {
        this.uri = Objects.requireNonNull(uri);
        this.contents = Objects.requireNonNull(contents);
    }

    // Needed for serialization
    public void setUri(String uri) {
        this.uri = uri;
    }

    // Needed for serialization
    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getUri() {
        return uri;
    }

    public String getContents() {
        return contents;
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
        return uri.equals(that.uri) && contents.equals(that.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, contents);
    }

    @Override
    public String toString() {
        return "SourceFile{" +
                "uri='" + uri + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }
}
