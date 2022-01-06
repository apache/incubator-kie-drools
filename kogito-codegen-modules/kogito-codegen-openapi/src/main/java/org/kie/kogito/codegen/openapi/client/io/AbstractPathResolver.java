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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.openapi.client.OpenApiClientException;
import org.kie.kogito.codegen.openapi.client.OpenApiSpecDescriptor;
import org.kie.kogito.codegen.openapi.client.OpenApiUtils;

/**
 * Base class for every supported @{@link PathResolver}
 */
public abstract class AbstractPathResolver implements PathResolver {

    private static final String BASE_PATH = "openapi-spec-cache";

    protected final KogitoBuildContext context;

    public AbstractPathResolver(final KogitoBuildContext context) {
        this.context = context;
    }

    private String getOutputPath() {
        final Path outputPath = Paths.get(OpenApiUtils.getEndUserTargetDir(this.context), BASE_PATH);
        if (Files.notExists(outputPath)) {
            try {
                Files.createDirectories(outputPath);
            } catch (IOException e) {
                throw new OpenApiClientException("Failed to create output path for OpenAPI spec files at " + outputPath, e);
            }
        }
        return outputPath.toString();
    }

    protected String saveFileToTempLocation(final OpenApiSpecDescriptor resource, final InputStream stream) {
        final String outputPath = Paths.get(this.getOutputPath(), resource.getId() + "_" + resource.getResourceName()).toString();
        try (ReadableByteChannel channel = Channels.newChannel(stream);
                FileOutputStream output = new FileOutputStream(outputPath)) {
            output.getChannel().transferFrom(channel, 0, Integer.MAX_VALUE);
            return outputPath;
        } catch (IOException e) {
            throw new OpenApiClientException("Fail to resolve remote file: " + resource.getURI().toString(), e);
        }
    }
}
