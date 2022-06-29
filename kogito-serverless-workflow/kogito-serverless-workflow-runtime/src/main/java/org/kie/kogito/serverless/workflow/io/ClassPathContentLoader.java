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
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

public class ClassPathContentLoader extends CachedContentLoader {

    private final Optional<URL> resource;
    private final String path;

    public ClassPathContentLoader(URI uri, Optional<ClassLoader> cl) {
        super(uri);
        this.path = getPath(uri);
        this.resource = Optional.ofNullable(cl.orElse(Thread.currentThread().getContextClassLoader()).getResource(path));
    }

    private static String getPath(URI uri) {
        String path = uri.getPath();
        Objects.requireNonNull(path, "classpath cannot be null");
        while (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    public Optional<URL> getResource() {
        return resource;
    }

    @Override
    protected byte[] loadURI(URI uri) {
        return resource.map(this::loadBytes).orElseThrow(() -> new IllegalArgumentException("cannot find classpath resource " + path));
    }

    private byte[] loadBytes(URL r) {
        try (InputStream is = r.openStream()) {
            return is.readAllBytes();
        } catch (IOException io) {
            throw new UncheckedIOException(io);
        }
    }

    @Override
    public URIContentLoaderType type() {
        return URIContentLoaderType.CLASSPATH;
    }
}
