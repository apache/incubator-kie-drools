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
package org.kie.pmml.compiler.commons.mocks;

import java.util.Map;

import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.commons.model.HasClassLoader;

public class HasClassLoaderMock implements HasClassLoader {

    private final ClassLoader classLoader;

    public HasClassLoaderMock() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public Class<?> compileAndLoadClass(Map<String, String> sourcesMap, String fullClassName) {
        Map<String, Class<?>> compiled = KieMemoryCompiler.compile(sourcesMap, classLoader);
        return compiled.get(fullClassName);
    }
}
