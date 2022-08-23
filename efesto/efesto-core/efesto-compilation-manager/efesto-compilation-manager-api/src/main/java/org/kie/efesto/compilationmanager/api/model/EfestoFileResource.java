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
package org.kie.efesto.compilationmanager.api.model;

import org.kie.efesto.common.api.io.MemoryFile;
import org.kie.efesto.common.api.utils.FileNameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class EfestoFileResource implements EfestoResource<File> {

    private final File modelFile;

    public EfestoFileResource(File modelFile) {
        this.modelFile = modelFile;
    }

    @Override
    public File getContent() {
        return modelFile;
    }

    public String getModelType() {
        return FileNameUtils.getSuffix(modelFile.getName());
    }

    public InputStream getInputStream() throws IOException {
        return modelFile instanceof MemoryFile ? ((MemoryFile) modelFile).getInputStream() : Files.newInputStream(modelFile.toPath());
    }

    public String getSourcePath() {
        return modelFile.getPath();
    }


}
