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
import java.nio.file.Path;
import java.util.Optional;

import io.serverlessworkflow.api.Workflow;

public class URIContentLoaderFactory {

    public static byte[] readAllBytes(URIContentLoader loader) throws IOException {
        try (InputStream is = loader.getInputStream()) {
            return is.readAllBytes();
        }
    }

    public static URIContentLoader runtimeLoader(String uriStr) {
        URI uri = URI.create(uriStr);
        return loader(uri, Optional.empty(), Optional.of(new ClassPathContentLoader(uri, Optional.empty())), Optional.empty(), null);
    }

    public static URIContentLoader buildLoader(URI uri, ClassLoader cl, Workflow workflow, String authRef) {
        return loader(uri, Optional.of(cl), Optional.empty(), Optional.of(workflow), authRef);
    }

    private static URIContentLoader loader(URI uri, Optional<ClassLoader> cl, Optional<URIContentLoader> fallback, Optional<Workflow> workflow, String authRef) {
        switch (URIContentLoaderType.from(uri)) {
            case FILE:
                return new FileContentLoader(Path.of(uri), fallback);
            case HTTP:
                return new HttpContentLoader(uri, fallback, workflow, authRef);
            default:
            case CLASSPATH:
                return new ClassPathContentLoader(uri, cl);
        }
    }

    private URIContentLoaderFactory() {
    }

}
