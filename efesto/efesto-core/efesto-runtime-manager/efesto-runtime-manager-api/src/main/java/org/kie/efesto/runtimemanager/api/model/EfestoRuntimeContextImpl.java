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

import java.util.List;
import java.util.Map;

import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.memorycompiler.KieMemoryCompiler;

public class EfestoRuntimeContextImpl implements EfestoRuntimeContext {

    private final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    EfestoRuntimeContextImpl(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        this.memoryCompilerClassLoader = memoryCompilerClassLoader;
    }

    public static EfestoRuntimeContext buildWithParentClassLoader(ClassLoader parentClassLoader) {
        return new EfestoRuntimeContextImpl(new KieMemoryCompiler.MemoryCompilerClassLoader(parentClassLoader));
    }

    public static EfestoRuntimeContext buildWithMemoryCompilerClassLoader(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        return new EfestoRuntimeContextImpl(memoryCompilerClassLoader);
    }

    @Override
    public void prepareClassLoader(GeneratedExecutableResource finalResource) {
        FRI fri = finalResource.getFri();
        List<String> fullClassNames = finalResource.getFullClassNames();

        boolean notFound = false;
        for (String name : fullClassNames) {
            try {
                memoryCompilerClassLoader.loadClass(name);
            } catch (ClassNotFoundException e) {
                notFound = true;
                break;
            }
        }

        // populate memoryCompilerClassLoader with generatedClasses stored in context (Actually, in GeneratedClassesRepository)
        if (notFound && this.containsKey(fri)) {
            Map<String, byte[]> generatedClasses = this.getGeneratedClasses(fri);
            generatedClasses.forEach(memoryCompilerClassLoader::addCode);
        }
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return memoryCompilerClassLoader.loadClass(className);
    }
}
