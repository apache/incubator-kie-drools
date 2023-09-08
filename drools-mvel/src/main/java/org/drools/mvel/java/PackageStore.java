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
package org.drools.mvel.java;

import java.util.List;

import org.drools.core.rule.JavaDialectRuntimeData;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.drools.util.PortablePath;
import org.kie.memorycompiler.resources.ResourceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PackageStore
    implements
    ResourceStore {
    private static final Logger LOG = LoggerFactory.getLogger(PackageStore.class);
    private JavaDialectRuntimeData       javaDialectRuntimeData;

    private List<KnowledgeBuilderResult> errors;

    public PackageStore() {
    }

    public PackageStore(final JavaDialectRuntimeData javaDialectRuntimeData,
                        final List<KnowledgeBuilderResult> errors) {
        this.javaDialectRuntimeData = javaDialectRuntimeData;
        this.errors = errors;
    }

    public void setPackageCompilationData(final JavaDialectRuntimeData javaDialectRuntimeData) {
        this.javaDialectRuntimeData = javaDialectRuntimeData;
    }

    public void write(final PortablePath resourceName,
                      final byte[] clazzData) {
        try {
            this.javaDialectRuntimeData.write( resourceName.asString(),
                                               clazzData );
        } catch ( final Exception e ) {
            LOG.error("Exception", e);
            this.errors.add( new JavaDialectError( "PackageStore was unable to write resourceName='" + resourceName.asString() + "'" ) );
        }
    }

    public void write(final PortablePath resourceName,
                      final byte[] clazzData,
                      boolean createFolder) {
        write(resourceName, clazzData);
    }

    public byte[] read(final PortablePath resourceName) {
        byte[] clazz = null;
        try {
            clazz = this.javaDialectRuntimeData.read( resourceName.asString() );
        } catch ( final Exception e ) {
            this.errors.add( new JavaDialectError( "PackageStore was unable to read resourceName='" + resourceName.asString() + "'" ) );
        }
        return clazz;
    }

    public void remove(final PortablePath resourceName) {
        try {
            this.javaDialectRuntimeData.remove( resourceName.asString() );
        } catch ( final Exception e ) {
            this.errors.add( new JavaDialectError( "PackageStore was unable to remove resourceName='" + resourceName.asString() + "'"  ) );
        }
    }

}
