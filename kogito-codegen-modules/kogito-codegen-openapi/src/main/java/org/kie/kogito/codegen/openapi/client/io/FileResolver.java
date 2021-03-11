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

import java.nio.file.Files;
import java.nio.file.Paths;

import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.openapi.client.OpenApiSpecDescriptor;
import org.kie.kogito.codegen.openapi.client.OpenApiUtils;

/**
 * Resolves the schema "file:" in a given OpenApi operation definition.
 * For example: "file://home/luke/specs/swagger.json"
 */
public class FileResolver extends AbstractPathResolver {

    protected FileResolver(final KogitoBuildContext context) {
        super(context);
    }

    @Override
    public String resolve(OpenApiSpecDescriptor resource) {
        OpenApiUtils.requireValidSpecURI(resource);
        if (Files.notExists(Paths.get(resource.getURI()))) {
            throw new IllegalArgumentException("File " + resource.getURI() + " does not exist");
        }
        // there's no need to save the file somewhere else
        return Paths.get(resource.getURI()).toString();
    }
}
