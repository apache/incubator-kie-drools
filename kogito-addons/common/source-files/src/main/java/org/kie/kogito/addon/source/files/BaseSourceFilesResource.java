/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kie.kogito.addon.source.files;

import java.util.Collection;
import java.util.Optional;

import org.kie.kogito.source.files.SourceFile;
import org.kie.kogito.source.files.SourceFilesProvider;

import static java.nio.file.Path.of;

public abstract class BaseSourceFilesResource<T> implements SourceFiles<T> {

    private final SourceFilesProvider sourceFilesProvider;

    public BaseSourceFilesResource(SourceFilesProvider sourceFilesProvider) {
        this.sourceFilesProvider = sourceFilesProvider;
    }

    @Override
    public T getSourceFileByUri(String uri) throws Exception {
        Optional<SourceFile> sourceFile = sourceFilesProvider.getSourceFilesByUri(uri);

        if (sourceFile.isEmpty()) {
            return buildNotFoundResponse();
        }

        return buildStreamResponse(sourceFile.get().readContents(), of(sourceFile.get().getUri()).getFileName().toString());
    }

    @Override
    public Collection<SourceFile> getSourceFilesByProcessId(String processId) {
        return sourceFilesProvider.getProcessSourceFiles(processId);
    }

    @Override
    public T getSourceFileByProcessId(String processId) throws Exception {
        Optional<SourceFile> sourceFile = sourceFilesProvider.getProcessSourceFile(processId);

        if (sourceFile.isEmpty()) {
            return buildNotFoundResponse();
        }

        return buildPlainResponse(sourceFile.get().readContents());
    }

    protected abstract T buildPlainResponse(byte[] content);

    protected abstract T buildStreamResponse(byte[] content, String fileName);

    protected abstract T buildNotFoundResponse();
}
