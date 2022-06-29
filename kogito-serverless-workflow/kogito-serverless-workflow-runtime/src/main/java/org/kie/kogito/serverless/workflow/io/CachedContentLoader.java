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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;

public abstract class CachedContentLoader implements URIContentLoader {

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

    private final URI uri;

    protected CachedContentLoader(URI uri) {
        this.uri = uri;
    }

    @Override
    public InputStream getInputStream() {
        return new NoCopyByteArrayInputStream(ResourceCacheFactory.getCache().get(uri, this::loadURI));
    }

    protected abstract byte[] loadURI(URI uri);

    @Override
    public URI uri() {
        return uri;
    }
}
