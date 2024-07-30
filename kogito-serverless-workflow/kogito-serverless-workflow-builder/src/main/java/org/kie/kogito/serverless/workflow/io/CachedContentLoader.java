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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CachedContentLoader implements URIContentLoader {

    private static final Logger logger = LoggerFactory.getLogger(CachedContentLoader.class);

    private static class NoCopyByteArrayInputStream extends ByteArrayInputStream {
        public NoCopyByteArrayInputStream(byte[] buf) {
            super(buf);
        }

        @Override
        public synchronized byte[] readAllBytes() {
            // This is an optimization that avoids copying the whole array if no byte has been read
            if (pos == 0) {
                pos = count;
                return buf;
            } else {
                return super.readAllBytes();
            }
        }
    }

    protected final String uri;
    private URIContentLoader[] fallbackContentLoaders;

    protected CachedContentLoader(String uri, URIContentLoader... fallbackContentLoaders) {
        this.uri = uri;
        this.fallbackContentLoaders = fallbackContentLoaders;
    }

    protected Optional<Path> internalGetPath() {
        return Optional.empty();
    }

    @Override
    public Optional<Path> getPath() {
        return internalGetPath().or(() -> {
            for (URIContentLoader contentLoader : fallbackContentLoaders) {
                Optional<Path> alternativePath = contentLoader.getPath();
                if (alternativePath.isPresent()) {
                    return alternativePath;
                }
            }
            return Optional.empty();
        });
    }

    @Override
    public InputStream getInputStream() {
        try {
            return new NoCopyByteArrayInputStream(ResourceCacheFactory.getCache().get(uri, this::loadURI));
        } catch (RuntimeException ex) {
            for (URIContentLoader contentLoader : fallbackContentLoaders) {
                try {
                    InputStream stream = contentLoader.getInputStream();
                    logger.warn("URI {} was retrieved using a fallback mechanism {} rather than original {}", uri, contentLoader.type(), type());
                    return stream;
                } catch (RuntimeException supressed) {
                    ex.addSuppressed(supressed);
                }
            }
            throw ex;
        }
    }

    protected static String trimScheme(String uri, String scheme) {
        String str = uri;
        if (str.toLowerCase().startsWith(scheme)) {
            str = str.substring(scheme.length());
        }
        return str;
    }

    protected abstract byte[] loadURI();
}
