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
package org.kie.pmml.compiler.commons.utils;

import java.util.Map;

import org.drools.reflective.classloader.ProjectClassLoader;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.exceptions.KiePMMLException;

/**
 * Class meant to provide <i>helper</i> methods related to <code>ClassLoader</code>s
 */
public class ClassloaderUtils {

    private ClassloaderUtils() {
        // avoid instantiation
    }

    /**
     * This method compile the given sources and add them to given <code>ProjectClassLoader</code>
     * Returns the <code>Class</code> with the given <b>fullClassName</b>
     * @param projectClassLoader
     * @param sourcesMap
     * @param fullClassName
     * @return
     */
    public static Class<?> compileAndLoadClass(ProjectClassLoader projectClassLoader, Map<String, String> sourcesMap, String fullClassName) {
        final Map<String, byte[]> byteCode = KieMemoryCompiler.compileNoLoad(sourcesMap, projectClassLoader);
        byteCode.forEach(projectClassLoader::defineClass);
        try {
            return projectClassLoader.loadClass(fullClassName);
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }
}
