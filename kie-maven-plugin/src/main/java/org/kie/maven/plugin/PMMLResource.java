/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.maven.plugin;

import java.nio.file.Path;
import java.util.List;

import org.kie.pmml.commons.model.KiePMMLModel;

public class PMMLResource {
    private final List<KiePMMLModel> kiePmmlModels;
    private final Path path;
    private final String modelPath;

    public PMMLResource(List<KiePMMLModel> kiePmmlModels, Path path , String modelPath) {
        this.kiePmmlModels = kiePmmlModels;
        this.path = path;
        this.modelPath = modelPath;
    }

    public List<KiePMMLModel> getKiePmmlModels() {
        return kiePmmlModels;
    }

    public Path getPath() {
        return path;
    }

    public String getModelPath() {
        return modelPath;
    }
}
