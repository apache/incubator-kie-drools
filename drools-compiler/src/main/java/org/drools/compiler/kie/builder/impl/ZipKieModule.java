/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
import java.util.Collection;
import java.util.Map;

import org.drools.core.util.IoUtils;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;

public class ZipKieModule extends AbstractKieModule implements InternalKieModule {
    private final File             file;    
    private Map<String, byte[]> zipEntries;

    public ZipKieModule(ReleaseId releaseId,
                        KieModuleModel kieProject,
                        File file) {
        super(releaseId, kieProject );
        this.file = file;
        this.zipEntries = IoUtils.indexZipFile( file );
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public boolean isAvailable(String name ) {
        return this.zipEntries.containsKey( name );
    }

    @Override
    public byte[] getBytes(String name) {
        return zipEntries.get(name);
    }

    @Override
    public Collection<String> getFileNames() {
        return this.zipEntries.keySet();
    }

    @Override
    public byte[] getBytes() {
        throw new UnsupportedOperationException();
    }

    public long getCreationTimestamp() {
        return file.lastModified();
    }

    public String toString() {
        return "ZipKieModule[releaseId=" + getReleaseId() + ",file=" + file + "]";
    }
}
