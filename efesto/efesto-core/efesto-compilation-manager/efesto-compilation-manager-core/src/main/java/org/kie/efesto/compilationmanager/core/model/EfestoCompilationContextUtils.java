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
package org.kie.efesto.compilationmanager.core.model;

import org.kie.efesto.compilationmanager.api.exceptions.EfestoCompilationManagerException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.memorycompiler.KieMemoryCompiler;

public class EfestoCompilationContextUtils {

    private EfestoCompilationContextUtils() {
    }

    public static EfestoCompilationContext buildWithParentClassLoader(ClassLoader parentClassLoader) {
        return new EfestoCompilationContextImpl(new KieMemoryCompiler.MemoryCompilerClassLoader(parentClassLoader));
    }

    public static EfestoCompilationContext buildFromContext(EfestoCompilationContextImpl original, Class<?
            extends EfestoCompilationContext> toInstantiate) {
        try {
            EfestoCompilationContext toReturn =
                    toInstantiate.getDeclaredConstructor(KieMemoryCompiler.MemoryCompilerClassLoader.class).newInstance(original.memoryCompilerClassLoader);
            toReturn.getGeneratedResourcesMap().putAll(original.getGeneratedResourcesMap());
            return toReturn;
        } catch (Exception e) {
            throw new EfestoCompilationManagerException("Failed to instantiate " + toInstantiate.getName(), e);
        }
    }
}
