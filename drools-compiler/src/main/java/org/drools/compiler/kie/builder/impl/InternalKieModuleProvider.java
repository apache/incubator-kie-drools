package org.drools.compiler.kie.builder.impl;

import java.io.File;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.internal.utils.KieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface InternalKieModuleProvider extends KieService {

    default InternalKieModule createClasspathKieModule() {
        return null;
    }

    InternalKieModule createKieModule( ReleaseId releaseId, KieModuleModel kieProject, File file );

    InternalKieModule createKieModule( ReleaseId releaseId, KieModuleModel kieProject, MemoryFileSystem mfs );

    class DrlBasedKieModuleProvider implements InternalKieModuleProvider {

        private static final Logger log = LoggerFactory.getLogger( InternalKieModuleProvider.class );

        @Override
        public InternalKieModule createKieModule( ReleaseId releaseId, KieModuleModel kieProject, File file ) {
            if (log.isInfoEnabled()) {
                log.info( "Creating KieModule for artifact " + releaseId );
            }
            return file.isDirectory() ? new FileKieModule( releaseId, kieProject, file ) : new ZipKieModule( releaseId, kieProject, file );
        }

        @Override
        public InternalKieModule createKieModule( ReleaseId releaseId, KieModuleModel kieProject, MemoryFileSystem mfs ) {
            if (log.isInfoEnabled()) {
                log.info( "Creating in memory KieModule for artifact " + releaseId );
            }
            return new MemoryKieModule(releaseId, kieProject, mfs);
        }
    }

    static InternalKieModule get( ReleaseId releaseId, KieModuleModel kieProject, File file ) {
        return Factory.get().createKieModule(releaseId, kieProject, file);
    }

    static InternalKieModule get( ReleaseId releaseId, KieModuleModel kieProject, MemoryFileSystem mfs ) {
        return Factory.get().createKieModule(releaseId, kieProject, mfs);
    }

    static InternalKieModule getFromClasspath() {
        return Factory.get().createClasspathKieModule();
    }

    class Factory {

        private static class LazyHolder {
            private static InternalKieModuleProvider INSTANCE = createZipKieModuleProvider();
        }

        private static InternalKieModuleProvider createZipKieModuleProvider() {
            InternalKieModuleProvider provider = KieService.load(InternalKieModuleProvider.class);
            return provider != null ? provider : new DrlBasedKieModuleProvider();
        }

        public static InternalKieModuleProvider get() {
            return LazyHolder.INSTANCE;
        }
    }
}
