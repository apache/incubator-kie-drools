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

import org.kie.efesto.common.api.model.EfestoContext;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.common.api.model.GeneratedClassesRepository;
import org.kie.memorycompiler.KieMemoryCompiler;

/**
 *
 * Wrap MemoryCompilerClassLoader and convey generated classes to be used by other CompilationManager or RuntimeManager
 *
 */
public class EfestoCompilationContext implements EfestoContext {

    private KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    private EfestoCompilationContext() {}

    public static EfestoCompilationContext buildWithParentClassLoader(ClassLoader parentClassLoader) {
        EfestoCompilationContext context = new EfestoCompilationContext();
        context.setMemoryCompilerClassLoader(new KieMemoryCompiler.MemoryCompilerClassLoader(parentClassLoader));
        return context;
    }

    public static EfestoCompilationContext buildWithMemoryCompilerClassLoader(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        EfestoCompilationContext context = new EfestoCompilationContext();
        context.memoryCompilerClassLoader = memoryCompilerClassLoader;
        return context;
    }

    public KieMemoryCompiler.MemoryCompilerClassLoader getMemoryCompilerClassLoader() {
        return memoryCompilerClassLoader;
    }

    public void setMemoryCompilerClassLoader(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        this.memoryCompilerClassLoader = memoryCompilerClassLoader;
    }

    public Map<String, byte[]> getGeneratedClasses(FRI fri) {
        return GeneratedClassesRepository.INSTANCE.getGeneratedClasses(fri);
    }

    public void addGeneratedClasses(FRI fri, Map<String, byte[]> generatedClasses) {
        GeneratedClassesRepository.INSTANCE.addGeneratedClasses(fri, generatedClasses);
    };

    public boolean containsKey(FRI fri) {
        return GeneratedClassesRepository.INSTANCE.containsKey(fri);
    }
}
