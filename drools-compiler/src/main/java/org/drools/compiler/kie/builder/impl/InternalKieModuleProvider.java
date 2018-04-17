/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.kie.builder.impl;

import java.io.File;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.internal.utils.ServiceRegistry;

public interface InternalKieModuleProvider {
    InternalKieModule createKieModule( ReleaseId releaseId, KieModuleModel kieProject, File file );

    InternalKieModule createKieModule( ReleaseId releaseId, KieModuleModel kieProject, MemoryFileSystem mfs );

    class DrlBasedKieModuleProvider implements InternalKieModuleProvider {

        @Override
        public InternalKieModule createKieModule( ReleaseId releaseId, KieModuleModel kieProject, File file ) {
            return file.isDirectory() ? new FileKieModule( releaseId, kieProject, file ) : new ZipKieModule( releaseId, kieProject, file );
        }

        @Override
        public InternalKieModule createKieModule( ReleaseId releaseId, KieModuleModel kieProject, MemoryFileSystem mfs ) {
            return new MemoryKieModule(releaseId, kieProject, mfs);
        }
    }

    static InternalKieModule get( ReleaseId releaseId, KieModuleModel kieProject, File file ) {
        return Factory.get().createKieModule(releaseId, kieProject, file);
    }

    static InternalKieModule get( ReleaseId releaseId, KieModuleModel kieProject, MemoryFileSystem mfs ) {
        return Factory.get().createKieModule(releaseId, kieProject, mfs);
    }

    class Factory {

        private static class LazyHolder {
            private static InternalKieModuleProvider INSTANCE = createZipKieModuleProvider();
        }

        private static InternalKieModuleProvider createZipKieModuleProvider() {
            InternalKieModuleProvider provider = ServiceRegistry.getInstance().get(InternalKieModuleProvider.class);
            return provider != null ? provider : new DrlBasedKieModuleProvider();
        }

        public static InternalKieModuleProvider get() {
            return LazyHolder.INSTANCE;
        }
    }
}
