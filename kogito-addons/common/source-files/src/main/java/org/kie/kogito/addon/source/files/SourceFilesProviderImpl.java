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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.kie.kogito.internal.SupportedExtensions;

public final class SourceFilesProviderImpl implements SourceFilesProvider {

    private final Map<String, Collection<SourceFile>> sourceFiles = new HashMap<>();

    public void addSourceFile(String id, SourceFile sourceFile) {
        sourceFiles.computeIfAbsent(id, k -> new HashSet<>()).add(sourceFile);
    }

    @Override
    public Optional<SourceFile> getSourceFilesByUri(String uri) {
        return sourceFiles.values().stream()
                .flatMap(Collection::stream)
                .filter(file -> Objects.equals(file.getUri(), uri))
                .findFirst();
    }

    @Override
    public Collection<SourceFile> getProcessSourceFiles(String processId) {
        return sourceFiles.getOrDefault(processId, Set.of());
    }

    @Override
    public Optional<SourceFile> getProcessSourceFile(String processId) throws SourceFilesException {
        return getProcessSourceFiles(processId).stream()
                .filter(this::isValidDefinitionSource)
                .findFirst();
    }

    private boolean isValidDefinitionSource(SourceFile sourceFile) {
        return SupportedExtensions.isSourceFile(sourceFile.getUri());
    }
}
