/*
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
package org.kie.efesto.common.core.storage;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.common.api.model.EfestoRuntimeContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ContextStorage {

    private static final Map<ModelLocalUriId, EfestoCompilationContext> COMPILATION_CONTEXT_MAP = new HashMap<>();
    private static final Map<ModelLocalUriId, String> COMPILATION_SOURCE_MAP = new HashMap<>();
    private static final Map<ModelLocalUriId, EfestoRuntimeContext> RUNTIME_CONTEXT_MAP = new HashMap<>();

    /**
     * Method used to clean up all data currently stored.
     */
    public static void reset() {
        COMPILATION_CONTEXT_MAP.clear();
        COMPILATION_SOURCE_MAP.clear();
        RUNTIME_CONTEXT_MAP.clear();
    }

    public static void putEfestoCompilationContext(ModelLocalUriId modelLocalUriId, EfestoCompilationContext compilationContext) {
        COMPILATION_CONTEXT_MAP.put(modelLocalUriId, compilationContext);
    }

    public static EfestoCompilationContext getEfestoCompilationContext(ModelLocalUriId modelLocalUriId) {
        return COMPILATION_CONTEXT_MAP.get(modelLocalUriId);
    }

    public static void putEfestoCompilationSource(ModelLocalUriId modelLocalUriId, String content) {
        COMPILATION_SOURCE_MAP.put(modelLocalUriId, content);
    }

    public static String getEfestoCompilationSource(ModelLocalUriId modelLocalUriId) {
        return COMPILATION_SOURCE_MAP.get(modelLocalUriId);
    }

    public static Collection<ModelLocalUriId> getAllModelLocalUriIdFromCompilationContext() {
        return COMPILATION_CONTEXT_MAP.keySet();
    }

    public static void putEfestoRuntimeContext(ModelLocalUriId modelLocalUriId, EfestoRuntimeContext runtimeContext) {
        RUNTIME_CONTEXT_MAP.put(modelLocalUriId, runtimeContext);
    }

    public static EfestoRuntimeContext getEfestoRuntimeContext(ModelLocalUriId modelLocalUriId) {
        return RUNTIME_CONTEXT_MAP.get(modelLocalUriId);
    }

}
