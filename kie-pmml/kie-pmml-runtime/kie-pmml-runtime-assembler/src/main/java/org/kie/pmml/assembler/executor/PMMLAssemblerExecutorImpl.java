/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.assembler.executor;

import java.util.List;
import java.util.stream.Collectors;

import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.pmml.assembler.converter.KiePMMLConverter;
import org.kie.pmml.compiler.executor.PMMLCompilerExecutor;

/**
 * PMMLAssemblerExecutor default implementation
 */
public class PMMLAssemblerExecutorImpl implements PMMLAssemblerExecutor {

    private PMMLCompilerExecutor pmmlCompilerExecutor;
    private KiePMMLConverter kiePMMLConverter;

    @Override
    public List<KnowledgeBuilderResult> getResults(Resource resource) {
        // TODO @gcardosi read actual Resource content
        return getResults(resource.toString());
    }

    private List<KnowledgeBuilderResult> getResults(String resource) {
        return pmmlCompilerExecutor
                .getResults(resource)
                .stream()
                .map(kiePMMLModel -> kiePMMLConverter.getFromKiePMMLModel(kiePMMLModel))
                .collect(Collectors.toList());
    }
}
