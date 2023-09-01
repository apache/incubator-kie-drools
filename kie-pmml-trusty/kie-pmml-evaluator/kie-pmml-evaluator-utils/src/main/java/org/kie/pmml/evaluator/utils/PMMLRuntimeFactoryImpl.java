/**
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
package org.kie.pmml.evaluator.utils;

import java.io.File;

import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.utils.SPIUtils;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.PMMLRuntimeFactory;
import org.kie.pmml.api.compilation.PMMLCompilationContext;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.compiler.PMMLCompilationContextImpl;
import org.kie.pmml.evaluator.core.service.PMMLRuntimeInternalImpl;

import static org.drools.util.FileUtils.getFile;

/**
 * Publicly-available facade to hide internal implementation details
 */
public class PMMLRuntimeFactoryImpl implements PMMLRuntimeFactory {

    private static final CompilationManager compilationManager = SPIUtils.getCompilationManager(false).get();

    @Override
    public PMMLRuntime getPMMLRuntimeFromFile(File pmmlFile) {
        EfestoResource<File> efestoFileResource = new EfestoFileResource(pmmlFile);
        KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
        PMMLCompilationContext pmmlContext = new PMMLCompilationContextImpl(pmmlFile.getName(), memoryCompilerClassLoader);
        compilationManager.processResource(pmmlContext, efestoFileResource);
        return new PMMLRuntimeInternalImpl(pmmlContext.getGeneratedResourcesMap());
    }

    @Override
    public PMMLRuntime getPMMLRuntimeFromClasspath(String pmmlFileName) {
        File pmmlFile = getFile(pmmlFileName);
        return getPMMLRuntimeFromFile(pmmlFile);
    }


}
