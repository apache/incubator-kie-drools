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
package org.drools.modelcompiler;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import org.drools.compiler.kie.builder.impl.AbstractKieModule;
import org.drools.io.InternalResource;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;

public class CanonicalInternalKieModule extends AbstractKieModule {

    public CanonicalInternalKieModule( ReleaseId releaseId, KieModuleModel kModuleModel) {
        super(releaseId, kModuleModel);
    }

    @Override
    public Collection<String> getFileNames() {
        return Collections.emptyList();
    }

    @Override
    public byte[] getBytes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InternalResource getResource( String fileName ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAvailable( String pResourceName ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getBytes( String pResourceName ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getFile() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getCreationTimestamp() {
        throw new UnsupportedOperationException();
    }
}
