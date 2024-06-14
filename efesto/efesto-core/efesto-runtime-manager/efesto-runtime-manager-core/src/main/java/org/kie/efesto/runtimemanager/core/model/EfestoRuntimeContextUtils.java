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
package org.kie.efesto.runtimemanager.core.model;

import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.runtimemanager.api.model.EfestoLocalRuntimeContext;
import org.kie.memorycompiler.KieMemoryCompiler;

import java.util.Map;

public class EfestoRuntimeContextUtils {

    private EfestoRuntimeContextUtils() {
    }

    public static EfestoLocalRuntimeContext buildWithParentClassLoader(ClassLoader parentClassLoader) {
        return new EfestoLocalRuntimeContextImpl(new KieMemoryCompiler.MemoryCompilerClassLoader(parentClassLoader));
    }

    public static EfestoLocalRuntimeContext buildWithParentClassLoader(ClassLoader parentClassLoader, Map<String, GeneratedResources> generatedResourcesMap) {
        return new EfestoLocalRuntimeContextImpl(new KieMemoryCompiler.MemoryCompilerClassLoader(parentClassLoader), generatedResourcesMap);
    }

}
