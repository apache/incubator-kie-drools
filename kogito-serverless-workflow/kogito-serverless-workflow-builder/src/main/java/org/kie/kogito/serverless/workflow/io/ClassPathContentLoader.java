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
import java.util.Optional;

public class ClassPathContentLoader extends CachedContentLoader {

    private final Optional<URL> resource;
    private final String path;

    ClassPathContentLoader(URI uri, Optional<ClassLoader> cl) {
        super(uri);
        this.path = getPath(uri);
        this.resource = Optional.ofNullable(cl.orElse(Thread.currentThread().getContextClassLoader()).getResource(path));
    }

    static String getPath(URI uri) {
        final String classPathPrefix = "classpath:";
        String str = uri.toString();
        if (str.toLowerCase().startsWith(classPathPrefix)) {
            str = str.substring(classPathPrefix.length());
            while (str.startsWith("/")) {
                str = str.substring(1);
            }
        }
        return str;
    }

    public Optional<URL> getResource() {
        return resource;
    }

    public String getPath() {
        return path;
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
