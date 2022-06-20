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
package org.kie.pmml.compilation.impl;

import org.kie.memorycompiler.JavaConfiguration;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.commons.model.HasClassLoader;

import java.util.Map;

public class HasClassloaderImpl implements HasClassLoader {

    private final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    public HasClassloaderImpl(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        this.memoryCompilerClassLoader = memoryCompilerClassLoader;
    }

    @Override
    public ClassLoader getClassLoader() {
        return memoryCompilerClassLoader;
    }

    @Override
    public Map<String, byte[]> compileClasses(Map<String, String> sourcesMap, String fullClassName) {
        return KieMemoryCompiler.compileNoLoad(sourcesMap, memoryCompilerClassLoader, JavaConfiguration.CompilerType.NATIVE);
//
//
//        ClassLoader classLoader = getClassLoader();
//        if (!(classLoader instanceof ProjectClassLoader)) {
//            throw new IllegalStateException("Expected ProjectClassLoader, received " + classLoader.getClass().getName());
//        }
//        ProjectClassLoader projectClassLoader = (ProjectClassLoader) classLoader;
//        final Map<String, byte[]> byteCode = KieMemoryCompiler.compileNoLoad(sourcesMap, projectClassLoader, JavaConfiguration.CompilerType.ECLIPSE);
//        byteCode.forEach(projectClassLoader::defineClass);
//        try {
//            return projectClassLoader.loadClass(fullClassName);
//        } catch (Exception e) {
//            throw new KiePMMLException(e);
//        }
    }
}
