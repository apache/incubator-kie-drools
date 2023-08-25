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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileContentLoader extends CachedContentLoader {

    private final Path path;

    FileContentLoader(URI uri) {
        super(uri);
        this.path = Path.of(getPath(uri));
    }

    public Path getPath() {
        return path;
    }

    @Override
    public URIContentLoaderType type() {
        return URIContentLoaderType.FILE;
    }

    @Override
    protected byte[] loadURI(URI uri) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException io) {
            throw new UncheckedIOException(io);
        }
    }

    static String getPath(URI uri) {
        return uri.getPath();
    }
}
