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
package org.drools.compiler.kie.builder.impl;

import java.util.Map;

import org.kie.api.internal.utils.KieService;
import org.kie.memorycompiler.resources.ResourceStore;

public interface CompilationCacheProvider extends KieService {

    class Holder {
        private static final CompilationCacheProvider INSTANCE = getCompilationCacheProvider();

        private static CompilationCacheProvider getCompilationCacheProvider() {
            CompilationCacheProvider provider = KieService.load( CompilationCacheProvider.class );
            return provider != null ? provider : DefaultCompilationCacheProvider.INSTANCE;
        }
    }

    static CompilationCacheProvider get() {
        return Holder.INSTANCE;
    }

    InternalKieModule.CompilationCache getCompilationCache( AbstractKieModule kieModule, Map<String, InternalKieModule.CompilationCache> compilationCache, String kbaseName);

    void writeKieModuleMetaInfo(InternalKieModule kModule, ResourceStore trgMfs);

    class DefaultCompilationCacheProvider implements CompilationCacheProvider {
        private static final CompilationCacheProvider INSTANCE = new DefaultCompilationCacheProvider();

        @Override
        public InternalKieModule.CompilationCache getCompilationCache( AbstractKieModule kieModule, Map<String, InternalKieModule.CompilationCache> compilationCache, String kbaseName ) {
            return null;
        }

        @Override
        public void writeKieModuleMetaInfo(InternalKieModule kModule, ResourceStore trgMfs) {
            new KieMetaInfoBuilder( kModule ).writeKieModuleMetaInfo( trgMfs );
        }
    }
}
