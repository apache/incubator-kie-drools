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
package org.kie.drl.engine.compilation.model;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.io.FileSystemResource;
import org.kie.efesto.compilationmanager.api.model.EfestoFileSetResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;

public abstract class AbstractDrlFileSetResource extends EfestoFileSetResource implements EfestoResource<Set<File>> {

    private final Set<FileSystemResource> fileSystemResources;

    protected AbstractDrlFileSetResource(Set<File> modelFiles, String basePath) {
        super(modelFiles, "drl", basePath);
        this.fileSystemResources =
                modelFiles.stream()
                        .map(FileSystemResource::new)
                        .collect(Collectors.toSet());
    }


    public Set<FileSystemResource> getFileSystemResource() {
        return fileSystemResources;
    }


}
