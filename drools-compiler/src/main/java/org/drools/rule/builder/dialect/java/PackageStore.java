package org.drools.rule.builder.dialect.java;

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

import org.apache.commons.jci.stores.ResourceStore;
import org.drools.rule.PackageCompilationData;

public class PackageStore
    implements
    ResourceStore {
    private PackageCompilationData packageCompilationData;

    public PackageStore() {
    }

    public PackageStore(final PackageCompilationData packageCompiationData) {
        this.packageCompilationData = packageCompiationData;
    }

    public void setPackageCompilationData(final PackageCompilationData packageCompiationData) {
        this.packageCompilationData = packageCompiationData;
    }

    public void write(final String resourceName,
                      final byte[] clazzData) {
        try {
            this.packageCompilationData.write( resourceName,
                                               clazzData );
        } catch ( final Exception e ) {

        }
    }

    public byte[] read(final String resourceName) {
        byte[] clazz = null;
        try {
            clazz = this.packageCompilationData.read( resourceName );
        } catch ( final Exception e ) {

        }
        return clazz;
    }

    public void remove(final String resourceName) {
        try {
            this.packageCompilationData.remove( resourceName );
        } catch ( final Exception e ) {

        }
    }
}