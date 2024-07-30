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
package org.kie.kogito.serverless.workflow.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

public class ClassPathContentLoader extends CachedContentLoader {

    private final Optional<URL> resource;
    private final String classpath;

    ClassPathContentLoader(String uri, Optional<ClassLoader> cl, URIContentLoader... fallbackContentLoaders) {
        super(uri, fallbackContentLoaders);
        this.classpath = uriToPath(uri);
        this.resource = Optional.ofNullable(cl.orElse(Thread.currentThread().getContextClassLoader()).getResource(classpath));
    }

    public Optional<URL> getResource() {
        return resource;
    }

    @Override
    protected Optional<Path> internalGetPath() {
        return resource.map(ClassPathContentLoader::fromURL);
    }

    String classpath() {
        return classpath;
    }

    private static Path fromURL(URL url) {
        try {
            return Path.of(url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI " + url, e);
        }
    }

    @Override
    protected byte[] loadURI() {
        return resource.map(this::loadBytes).orElseThrow(() -> new IllegalArgumentException("cannot find classpath resource " + classpath));
    }

    private byte[] loadBytes(URL r) {
        try (InputStream is = r.openStream()) {
            return is.readAllBytes();
        } catch (IOException io) {
            throw new UncheckedIOException(io);
        }
    }

    static String uriToPath(String uri) {
        return removeSlash(trimScheme(uri, URIContentLoaderType.CLASSPATH.scheme()));
    }

    private static String removeSlash(String str) {
        while (str.startsWith("/")) {
            str = str.substring(1);
        }
        return str;
    }

    @Override
    public URIContentLoaderType type() {
        return URIContentLoaderType.CLASSPATH;
    }
}
