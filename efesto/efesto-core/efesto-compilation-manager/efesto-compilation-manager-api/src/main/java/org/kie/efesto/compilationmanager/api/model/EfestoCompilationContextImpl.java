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

import java.util.Map;
import java.util.Set;

import org.kie.efesto.common.api.model.FRI;
import org.kie.memorycompiler.JavaConfiguration;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.memorycompiler.KieMemoryCompilerException;

public class EfestoCompilationContextImpl implements EfestoCompilationContext {

    protected final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    protected EfestoCompilationContextImpl(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
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
    public Map<String, byte[]> compileClasses(Map<String, String> sourcesMap) {
        return KieMemoryCompiler.compileNoLoad(sourcesMap, memoryCompilerClassLoader, JavaConfiguration.CompilerType.NATIVE);
    }

    @Override
    public void loadClasses(Map<String, byte[]> compiledClassesMap) {
        for (Map.Entry<String, byte[]> entry : compiledClassesMap.entrySet()) {
            memoryCompilerClassLoader.addCode(entry.getKey(), entry.getValue());
            try {
                memoryCompilerClassLoader.loadClass(entry.getKey());
            } catch (ClassNotFoundException e) {
                throw new KieMemoryCompilerException(e.getMessage(), e);
            }
        }
    }
}
