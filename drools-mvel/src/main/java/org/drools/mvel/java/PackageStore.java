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

package org.drools.mvel.java;

import java.nio.file.Path;
import java.util.List;

import org.kie.memorycompiler.resources.ResourceStore;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.kie.internal.builder.KnowledgeBuilderResult;

public class PackageStore
    implements
    ResourceStore {
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

    public void write(String resourceName, final byte[] clazzData) {
        try {
            this.javaDialectRuntimeData.write( resourceName, clazzData );
        } catch ( final Exception e ) {
            this.errors.add( new JavaDialectError( "PackageStore was unable to write resourceName='" + resourceName + "'" ) );
        }
    }

    @Override
    public void write(Path resourcePath, final byte[] clazzData) {
        write(resourcePath.toString(), clazzData);
    }

    @Override
    public void write(Path resourcePath, byte[] clazzData, boolean createFolder) {
        write(resourcePath, clazzData);
    }

    @Override
    public byte[] read(Path resourcePath) {
        byte[] clazz = null;
        try {
            clazz = this.javaDialectRuntimeData.read( resourcePath.toString() );
        } catch ( final Exception e ) {
            this.errors.add( new JavaDialectError( "PackageStore was unable to read resourceName='" + resourcePath.toString() + "'" ) );
        }
        return clazz;
    }

    @Override
    public void remove(Path resourcePath) {
        try {
            this.javaDialectRuntimeData.remove( resourcePath.toString() );
        } catch ( final Exception e ) {
            this.errors.add( new JavaDialectError( "PackageStore was unable to remove resourceName='" + resourcePath.toString() + "'"  ) );
        }
    }

}
