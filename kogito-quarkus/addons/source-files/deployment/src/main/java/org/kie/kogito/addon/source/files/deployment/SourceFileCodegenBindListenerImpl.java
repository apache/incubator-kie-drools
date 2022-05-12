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
package org.kie.kogito.addon.source.files.deployment;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

import org.kie.kogito.addon.source.files.SourceFile;
import org.kie.kogito.addon.source.files.SourceFilesRecorder;
import org.kie.kogito.codegen.api.SourceFileCodegenBindEvent;
import org.kie.kogito.codegen.api.SourceFileCodegenBindListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class SourceFileCodegenBindListenerImpl implements SourceFileCodegenBindListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceFileCodegenBindListenerImpl.class);

    private final File[] resourcePaths;

    private final SourceFilesRecorder sourceFilesRecorder;

    protected SourceFileCodegenBindListenerImpl(File[] resourcePaths, SourceFilesRecorder sourceFilesRecorder) {
        this.resourcePaths = resourcePaths;
        this.sourceFilesRecorder = sourceFilesRecorder;
    }

    @Override
    public void onSourceFileCodegenBind(SourceFileCodegenBindEvent event) {
        LOGGER.debug("Received event {}", event);

        Path sourceFilePath = Path.of(event.getUri());

        Arrays.stream(resourcePaths)
                .map(File::toPath)
                .filter(sourceFilePath::startsWith)
                .findFirst()
                .ifPresentOrElse(resourcePath -> {
                    SourceFile sourceFile = new SourceFile(resolveSourceFilePath(sourceFilePath, resourcePath));
                    sourceFilesRecorder.addSourceFile(event.getSourceFileId(), sourceFile);
                }, () -> sourceFilesRecorder.addSourceFile(event.getSourceFileId(), new SourceFile(event.getUri())));
    }

    private String resolveSourceFilePath(Path sourceFilePath, Path locationPath) {
        return sourceFilePath.subpath(locationPath.getNameCount(), sourceFilePath.getNameCount()).toString();
    }
}
