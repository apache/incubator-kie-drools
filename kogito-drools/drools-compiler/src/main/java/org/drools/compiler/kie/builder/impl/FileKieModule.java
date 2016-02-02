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

import org.drools.core.util.IoUtils;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.Resource;
import org.kie.internal.io.ResourceFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;

import static org.drools.core.util.IoUtils.readBytesFromInputStream;

public class FileKieModule extends AbstractKieModule implements InternalKieModule {
    private final File             file;   

    public FileKieModule(ReleaseId releaseId,
                      KieModuleModel kieProject,
                      File file) {
        super(releaseId, kieProject );
        this.file = file;
    }

    @Override
    public File getFile() {
        return this.file;
    }

    public long getCreationTimestamp() {
        return file.lastModified();
    }

    @Override
    public boolean isAvailable(String pResourceName) {
        return new File( file, pResourceName).exists();
    }


    @Override
    public byte[] getBytes(String pResourceName) {
        try {
            File resource = new File( file, pResourceName);
            if( resource.exists() ) {
                return IoUtils.readBytesFromInputStream( new FileInputStream( resource ) );
            } else {
                return null;
            }
        } catch ( Exception e ) {
            throw new RuntimeException("Unable to get bytes for: " + new File( file, pResourceName) );
        }
    }

    @Override
    public Resource getResource( String fileName ) {
        File resource = new File( file, fileName);
        return resource.exists() ? ResourceFactory.newFileResource( resource ) : null;
    }

    @Override
    public Collection<String> getFileNames() {
        return IoUtils.recursiveListFile( file );
    }


    @Override
    public byte[] getBytes() {
        try {
            return readBytesFromInputStream(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        return "FileKieModule[releaseId=" + getReleaseId() + ",file=" + file + "]";
    }

}
