/**
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
package org.drools.codegen.common;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneratedFile {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratedFile.class);

    public static final String META_INF_RESOURCES = "META-INF/resources/";
    public static final Path META_INF_RESOURCES_PATH = Paths.get(META_INF_RESOURCES);

    private final Path path;
    private final String pathAsString;
    private final byte[] contents;
    private final GeneratedFileType type;

    public GeneratedFile(GeneratedFileType type, Path path, String contents) {
        this(type, path, contents.getBytes(StandardCharsets.UTF_8));
    }

    public GeneratedFile(GeneratedFileType type, Path path, byte[] contents) {
        this(type, path, path.toString(), contents);
    }

    public GeneratedFile(GeneratedFileType type, String relativePath, String contents) {
        this(type, relativePath, contents.getBytes(StandardCharsets.UTF_8));
    }

    public GeneratedFile(GeneratedFileType type, String relativePath, byte[] contents) {
        this(type, Paths.get(relativePath), relativePath, contents);
    }

    private GeneratedFile(GeneratedFileType type, Path path, String pathAsString, byte[] contents) {
        this.type = type;

        if (type.category().equals(GeneratedFileType.Category.STATIC_HTTP_RESOURCE)) {
            if (path.startsWith(META_INF_RESOURCES)) {
                LOGGER.warn("STATIC_HTTP_RESOURCE is automatically placed under " + META_INF_RESOURCES + ". You don't need to specify the directory : {}", path);
            } else {
                path = META_INF_RESOURCES_PATH.resolve(path);
                pathAsString = path.toString();
            }
        }

        this.path = path;
        this.pathAsString = pathAsString.replace('\\', '/');
        this.contents = contents;
    }

    public String relativePath() {
        return pathAsString;
    }

    public Path path() {
        return path;
    }

    public byte[] contents() {
        return contents;
    }

    public GeneratedFileType type() {
        return type;
    }

    public GeneratedFileType.Category category() {
        return type.category();
    }

    public String toStringWithContent() {
        return "GeneratedFile{" + "path=" + path +
                ", content='\n" + new String(contents, StandardCharsets.UTF_8) + "\n'}";
    }

    @Override
    public String toString() {
        return "GeneratedFile{" +
                "type=" + type +
                ", path='" + path + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeneratedFile that = (GeneratedFile) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
