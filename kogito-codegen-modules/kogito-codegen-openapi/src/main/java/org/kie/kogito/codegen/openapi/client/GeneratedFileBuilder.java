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

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.drools.core.util.StringUtils;
import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.GeneratedFileType;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.openapi.client.di.DependencyInjectionConfigurer;

/**
 * Builds a @{@link GeneratedFile} from a given file and an associated {@link OpenApiSpecDescriptor}.
 */
public class GeneratedFileBuilder {

    private final KogitoBuildContext context;
    private final DependencyInjectionConfigurer dependencyInjectionConfigurer;

    protected GeneratedFileBuilder(final KogitoBuildContext context) {
        this.context = context;
        this.dependencyInjectionConfigurer = new DependencyInjectionConfigurer(context);
    }

    public GeneratedFile build(File file, OpenApiSpecDescriptor descriptor) {
        if (this.context.hasDI()) {
            return this.toGeneratedFile(getGeneratedFilePath(file),
                    this.dependencyInjectionConfigurer.parseAndConfigure(file, descriptor).toString());
        }
        return this.toGeneratedFile(file);
    }

    private GeneratedFile toGeneratedFile(final String path, final String content) {
        return new GeneratedFile(GeneratedFileType.SOURCE, path, content);
    }

    private GeneratedFile toGeneratedFile(final File file) {
        return this.toGeneratedFile(getGeneratedFilePath(file), readFileContent(file));
    }

    /**
     * Generates the final path for the generated file were the Kogito Codegen Context could manage
     *
     * @param openAPIGeneratorFile the OpenAPI generated java file
     * @return the path within the Kogito Codegen context
     */
    protected String getGeneratedFilePath(final File openAPIGeneratorFile) {
        final Path path = openAPIGeneratorFile.toPath();
        for (int i = 0; i < path.getNameCount(); i++) {
            if (isExactSrcPath(path, i)) {
                return path.subpath(i, path.getNameCount()).toString()
                        .replace(Paths.get("src", "main", "java").toString().concat(File.separator), "");
            }
        }
        return "";
    }

    private boolean isExactSrcPath(final Path path, final int currentIndex) {
        if (currentIndex + 2 < path.getNameCount() && "src".equals(path.getName(currentIndex).toString())
                && "main".equals(path.getName(currentIndex + 1).toString()) && "java".equals(path.getName(currentIndex + 2).toString())) {
            return true;
        }
        return false;
    }

    private String readFileContent(final File file) {
        try (final BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            return StringUtils.readFileAsString(reader);
        } catch (Exception e) {
            throw new OpenApiClientException("Fail to read generated OpenAPI file: " + file.toPath(), e);
        }
    }
}
