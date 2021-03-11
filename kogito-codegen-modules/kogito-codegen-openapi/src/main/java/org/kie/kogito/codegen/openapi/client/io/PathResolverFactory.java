/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.openapi.client.io;

import java.util.Objects;

import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.openapi.client.OpenApiSpecDescriptor;

/**
 * Entry point for the {@link PathResolver} API.
 */
public final class PathResolverFactory {

    static final String CLASSPATH = "classpath";
    static final String FILE = "file";
    static final String HTTP = "http";
    static final String HTTPS = "https";

    private PathResolverFactory() {
    }

    /**
     * Can create a specific resolver to resolve the OpenAPI specification files in the end user target directory.
     *
     * @param resource the given OpenAPI resource file representation
     * @return the appropriate resolver based on the given URI schema. The supported "path resolvers" can be seeing in this package.
     * @see PathResolver
     */
    public static PathResolver newResolver(final OpenApiSpecDescriptor resource, final KogitoBuildContext context) {
        Objects.requireNonNull(resource);
        if (!resource.getURI().isAbsolute()) {
            return new ClasspathResolver(context);
        }
        switch (resource.getURI().getScheme()) {
            case (CLASSPATH):
                return new ClasspathResolver(context);
            case (FILE):
                return new FileResolver(context);
            case (HTTP):
            case (HTTPS):
                return new HTTPResolver(context);
            default:
                throw new IllegalArgumentException("URI schema not supported to resolve OpenAPI spec file. Supported schemas are http, file or classpath");
        }
    }
}
