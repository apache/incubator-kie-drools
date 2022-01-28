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
package org.kie.kogito.codegen.openapi.client;

import org.kie.kogito.codegen.api.context.KogitoBuildContext;

import static java.util.Objects.requireNonNull;

public final class OpenApiUtils {

    private OpenApiUtils() {
    }

    /**
     * Abstraction of the user's project temporary location
     */
    public static String getTempDirLocation(KogitoBuildContext context) {
        return context.getAppPaths().getOutputTarget().toString();
    }

    public static void requireValidSpecURI(final OpenApiSpecDescriptor resource) {
        requireNonNull(resource, "OpenApiSpecDescriptor can't be null");
        requireNonNull(resource.getURI(), "URI in OpenApiSpecDescriptor is null");
        if (resource.getURI().getPath().equals("")) {
            throw new IllegalArgumentException("Invalid OpenAPI spec file path: " + resource);
        }
    }
}
