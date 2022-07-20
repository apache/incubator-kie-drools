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
package org.kie.efesto.runtimemanager.api.model;

import java.util.Set;

import org.kie.efesto.common.api.model.FRI;
import org.kie.memorycompiler.KieMemoryCompiler;

public class EfestoRuntimeContextImpl implements EfestoRuntimeContext {

    private final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    EfestoRuntimeContextImpl(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        this.memoryCompilerClassLoader = memoryCompilerClassLoader;
        prepareClassLoader();
    }

    private void prepareClassLoader() {
        Set<FRI> friKeySet = friKeySet();
        friKeySet.stream()
                 .map(this::getGeneratedClasses)
                 .forEach(generatedClasses -> generatedClasses.forEach(memoryCompilerClassLoader::addCodeIfAbsent));
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return memoryCompilerClassLoader.loadClass(className);
    }
}
