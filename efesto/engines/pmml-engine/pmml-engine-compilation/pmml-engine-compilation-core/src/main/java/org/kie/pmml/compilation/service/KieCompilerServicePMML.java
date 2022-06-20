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
package org.kie.pmml.compilation.service;

import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.kie.memorycompiler.KieMemoryCompiler;

import java.util.List;

import static org.kie.pmml.compilation.service.PMMLCompilerService.getEfestoCompilationOutputPMML;

public class KieCompilerServicePMML implements KieCompilerService {

    @Override
    public <T extends EfestoResource> boolean canManageResource(T toProcess) {
        return toProcess instanceof EfestoFileResource && ((EfestoFileResource) toProcess).getModelType().equalsIgnoreCase("pmml");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends EfestoResource, E extends EfestoCompilationOutput> List<E> processResource(T toProcess, KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        if (!canManageResource(toProcess)) {
            throw new KieCompilerServiceException(String.format("%s can not process %s",
                    this.getClass().getName(),
                    toProcess.getClass().getName()));
        }
        return (List<E>) getEfestoCompilationOutputPMML((EfestoFileResource) toProcess, memoryCompilerClassLoader);
    }
}
