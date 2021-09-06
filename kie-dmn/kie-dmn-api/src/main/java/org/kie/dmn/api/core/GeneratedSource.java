/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.api.core;

import java.nio.file.Path;

public class GeneratedSource {

    private final Path filePath;
    private final String sourceContent;

    public GeneratedSource(Path filePath, String sourceContent) {
        this.filePath = filePath;
        this.sourceContent = sourceContent;
    }

    public Path getFilePath() {
        return filePath;
    }

    public String getSourceContent() {
        return sourceContent;
    }

    @Override
    public String toString() {
        return "GeneratedSource{" +
                "fileName='" + filePath + '\'' +
                ", sourceContent='" + sourceContent + '\'' +
                '}';
    }
}