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
package org.kie.kogito.addon.source.files;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.kie.kogito.codegen.process.ProcessCodegen;

public final class SourceFilesProviderImpl implements SourceFilesProvider {

    private final Map<String, Collection<SourceFile>> sourceFiles = new HashMap<>();

    public void addSourceFile(String id, SourceFile sourceFile) {
        sourceFiles.computeIfAbsent(id, k -> new HashSet<>()).add(sourceFile);
    }

    @Override
    public Collection<SourceFile> getProcessSourceFiles(String processId) {
        return sourceFiles.getOrDefault(processId, Set.of());
    }

    @Override
    public Optional<String> getProcessSourceFile(String processId) throws SourceFilesException {
        return getProcessSourceFiles(processId).stream().map(SourceFile::getUri).filter(this::isValidDefinitionSource).findFirst().flatMap(this::readFileContentFromClassPath);
    }

    private boolean isValidDefinitionSource(String uri) {
        if (ProcessCodegen.SUPPORTED_BPMN_EXTENSIONS.stream().noneMatch(uri::endsWith)) {
            return ProcessCodegen.SUPPORTED_SW_EXTENSIONS.keySet().stream().anyMatch(uri::endsWith);
        }
        return true;
    }

    private Optional<String> readFileContentFromClassPath(String relativeFileURI) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/resources" + relativeFileURI)) {
            return Optional.of(IOUtils.toString(is, StandardCharsets.UTF_8.name()));
        } catch (Exception ex) {
            throw new SourceFilesException("Exception trying to read definition source file with relative URI:" + relativeFileURI, ex);
        }
    }
}
