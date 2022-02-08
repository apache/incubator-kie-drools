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
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

public class ClassPathContentLoader implements URIContentLoader {

    private String path;
    private Optional<ClassLoader> cl;

    public ClassPathContentLoader(URI uri, Optional<ClassLoader> cl) {
        this.path = getPath(uri);
        this.cl = cl;
    }

    private static String getPath(URI uri) {
        String path = uri.getPath();
        Objects.requireNonNull(path, "classpath cannot be null");
        while (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    @Override
    public byte[] toBytes() throws IOException {
        try (InputStream is = cl.orElse(Thread.currentThread().getContextClassLoader()).getResourceAsStream(path)) {
            if (is == null) {
                throw new IOException("Cannot find resource " + path + " in classpath");
            }
            return is.readAllBytes();
        }
    }

}
