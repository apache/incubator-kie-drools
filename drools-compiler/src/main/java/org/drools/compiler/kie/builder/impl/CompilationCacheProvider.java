/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.kie.builder.impl;

import java.util.Map;

import org.kie.memorycompiler.resources.ResourceStore;
import org.kie.api.internal.utils.ServiceRegistry;

public interface CompilationCacheProvider {

    class Holder {
        private static final CompilationCacheProvider INSTANCE = getCompilationCacheProvider();

        private static CompilationCacheProvider getCompilationCacheProvider() {
            CompilationCacheProvider provider = ServiceRegistry.getService( CompilationCacheProvider.class );
            return provider != null ? provider : DefaultCompilationCacheProvider.INSTANCE;
        }
    }

    static CompilationCacheProvider get() {
        return Holder.INSTANCE;
    }

    InternalKieModule.CompilationCache getCompilationCache( AbstractKieModule kieModule, Map<String, InternalKieModule.CompilationCache> compilationCache, String kbaseName);

    void writeKieModuleMetaInfo(InternalKieModule kModule, ResourceStore trgMfs);

    enum DefaultCompilationCacheProvider implements CompilationCacheProvider {
        INSTANCE;

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
