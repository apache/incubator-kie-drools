/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.scenariosimulation.api.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FactIdentifierTest {

    @Test
    public void getClassNameWithoutPackage() {
        commonGetClassNameWithoutPackage("test", "com.Test", "Test");
    }

    @Test
    public void getClassNameWithoutPackage_LongPackage() {
        commonGetClassNameWithoutPackage("test", "com.project.Test", "Test");
    }

    @Test
    public void getClassNameWithoutPackage_NoPackage() {
        commonGetClassNameWithoutPackage("test", "Test", "Test");
    }

    private void commonGetClassNameWithoutPackage(String name, String className, String expectedClassName) {
        FactIdentifier factIdentifier = new FactIdentifier(name, className);
        assertEquals(expectedClassName, factIdentifier.getClassNameWithoutPackage());
    }

    @Test
    public void getPackageWithoutClassName() {
        commonGetPackageWithoutClassName("test", "com.Test", "com");
    }

    @Test
    public void getPackageWithoutClassName_LongPackage() {
        commonGetPackageWithoutClassName("test", "com.project.Test", "com.project");
    }

    @Test
    public void getPackageWithoutClassName_NoPackage() {
        commonGetPackageWithoutClassName("test", "Test", "");
    }

    private void commonGetPackageWithoutClassName(String name, String className, String expectedPackage) {
        FactIdentifier factIdentifier = new FactIdentifier(name, className);
        assertEquals(expectedPackage, factIdentifier.getPackageWithoutClassName());
    }
    
}
