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

import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FallbackContentLoader extends CachedContentLoader {

    private static final Logger logger = LoggerFactory.getLogger(FallbackContentLoader.class);

    private final Optional<URIContentLoader> fallbackLoader;

    protected FallbackContentLoader(URI uri, Optional<URIContentLoader> fallbackContentLoader) {
        super(uri);
        this.fallbackLoader = fallbackContentLoader;
    }

    @Override
    public InputStream getInputStream() {
        try {
            return super.getInputStream();
        } catch (UncheckedIOException io) {
            try {
                return fallbackLoader.orElseThrow(() -> io).getInputStream();
            } catch (UncheckedIOException | IllegalArgumentException io2) {
                logger.error("Fallback loader failed with message \"{}\", throwing original exception", io2.getMessage());
                throw io;
            }
        }
    }
}
