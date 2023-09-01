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
