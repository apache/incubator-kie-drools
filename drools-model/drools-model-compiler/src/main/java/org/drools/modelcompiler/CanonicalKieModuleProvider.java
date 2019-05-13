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

package org.drools.modelcompiler;

import java.io.File;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.InternalKieModuleProvider;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;

import static org.drools.modelcompiler.CanonicalKieModule.getModelFileWithGAV;

public class CanonicalKieModuleProvider extends InternalKieModuleProvider.DrlBasedKieModuleProvider implements InternalKieModuleProvider {

    @Override
    public InternalKieModule createKieModule( ReleaseId releaseId, KieModuleModel kieProject, File file ) {
        return createCanonicalKieModule( super.createKieModule( releaseId, kieProject, file ) );
    }

    @Override
    public InternalKieModule createKieModule( ReleaseId releaseId, KieModuleModel kieProject, MemoryFileSystem mfs ) {
        return createCanonicalKieModule( super.createKieModule(releaseId, kieProject, mfs) );
    }

    private InternalKieModule createCanonicalKieModule( InternalKieModule internalKieModule ) {
        if (internalKieModule.hasResource(getModelFileWithGAV(internalKieModule.getReleaseId()))) {
            return new CanonicalKieModule(internalKieModule);
        } else {
            return internalKieModule;
        }
    }
}
