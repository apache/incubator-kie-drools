/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.lang.descr.GlobalDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.builder.ReleaseId;

import static org.drools.core.util.StringUtils.generateUUID;
import static org.drools.core.util.StringUtils.getPkgUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ModelBuilderImplTest {

    private static final ReleaseId RELEASE_ID = new ReleaseIdImpl("group:artifact:version");
    private  static final  KnowledgeBuilderConfigurationImpl CONFIGURATION = new KnowledgeBuilderConfigurationImpl(ModelBuilderImplTest.class.getClassLoader());
    private InternalKnowledgePackage internalKnowledgePackage;
    private PackageRegistry packageRegistry;
    private ModelBuilderImpl<PackageSources> modelBuilder;

    @Before
    public void setup() {
        internalKnowledgePackage = new KnowledgePackageImpl("apackage");
        modelBuilder = new ModelBuilderImpl<>(PackageSources::dumpSources, CONFIGURATION, RELEASE_ID, false);
        packageRegistry = new PackageRegistry(ModelBuilderImplTest.class.getClassLoader(), CONFIGURATION, internalKnowledgePackage);
    }

    @Test
    public void getPackageModelWithPkgUUID() {
        String pkgUUID = generateUUID();
        PackageDescr packageDescr = getPackageDescr(pkgUUID);
        PackageModel retrieved =  modelBuilder.getPackageModel(packageDescr, packageRegistry, internalKnowledgePackage.getName());
        assertNotNull(retrieved);
        assertEquals(pkgUUID, retrieved.getPackageUUID());
    }

    @Test
    public void getPackageModelWithoutPkgUUID() {
        PackageDescr packageDescr = getPackageDescr(null);
        PackageModel retrieved =  modelBuilder.getPackageModel(packageDescr, packageRegistry, internalKnowledgePackage.getName());
        assertNotNull(retrieved);
        String expected = getPkgUUID(RELEASE_ID, internalKnowledgePackage.getName());
        assertEquals(expected, retrieved.getPackageUUID());
    }

    private PackageDescr getPackageDescr(String pkgUUID) {
        PackageDescr packageDescr = new PackageDescr();
        packageDescr.addGlobal(getGlobalDescr());
        if (pkgUUID != null) {
            packageDescr.setPreferredPkgUUID(pkgUUID);
        }

        return packageDescr;
    }

    private GlobalDescr getGlobalDescr() {
        GlobalDescr toReturn = new GlobalDescr();
        toReturn.setType(Double.class.getName());
        toReturn.setIdentifier("DOUBLE_GLOBAL");
        return toReturn;
    }

}