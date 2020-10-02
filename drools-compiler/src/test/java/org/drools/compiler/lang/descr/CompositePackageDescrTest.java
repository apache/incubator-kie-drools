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

package org.drools.compiler.lang.descr;

import org.drools.core.io.impl.ByteArrayResource;
import org.junit.Before;
import org.junit.Test;

import static org.drools.core.util.StringUtils.generateUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class CompositePackageDescrTest {

    private static final String NAMESPACE = "namespace";
    private  CompositePackageDescr compositePackageDescr;

    @Before
    public void setup() {
        compositePackageDescr = new CompositePackageDescr(new ByteArrayResource(), new PackageDescr(NAMESPACE));
    }

    @Test
    public void addPackageDescrSamePkgUUID() {
        String pkgUUID = generateUUID();
        PackageDescr toAdd = new PackageDescr(NAMESPACE);
        toAdd.setPreferredPkgUUID(pkgUUID);
        compositePackageDescr.addPackageDescr(new ByteArrayResource(), toAdd);
        assertTrue(compositePackageDescr.getPreferredPkgUUID().isPresent());
        assertEquals(pkgUUID, compositePackageDescr.getPreferredPkgUUID().get());
        toAdd = new PackageDescr(NAMESPACE);
        toAdd.setPreferredPkgUUID(pkgUUID);
        compositePackageDescr.addPackageDescr(new ByteArrayResource(), toAdd);
        assertEquals(pkgUUID, compositePackageDescr.getPreferredPkgUUID().get());
    }

    @Test(expected = RuntimeException.class)
    public void addPackageDescrDifferentPkgUUID() {
        String pkgUUID = generateUUID();
        PackageDescr first = new PackageDescr(NAMESPACE);
        first.setPreferredPkgUUID(pkgUUID);
        assertTrue(first.getPreferredPkgUUID().isPresent());
        compositePackageDescr.addPackageDescr(new ByteArrayResource(), first);
        assertTrue(compositePackageDescr.getPreferredPkgUUID().isPresent());
        assertEquals(pkgUUID, compositePackageDescr.getPreferredPkgUUID().get());
        pkgUUID = generateUUID();
        PackageDescr second = new PackageDescr(NAMESPACE);
        second.setPreferredPkgUUID(pkgUUID);
        assertTrue(second.getPreferredPkgUUID().isPresent());
        assertNotEquals(first.getPreferredPkgUUID().get(), second.getPreferredPkgUUID().get());
        compositePackageDescr.addPackageDescr(new ByteArrayResource(), second);
    }
}