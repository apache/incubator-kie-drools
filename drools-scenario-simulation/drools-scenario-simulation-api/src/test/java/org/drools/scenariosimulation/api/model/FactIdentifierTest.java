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
        FactIdentifier factIdentifier = new FactIdentifier("test", "com.Test");
        assertEquals(factIdentifier.getClassNameWithoutPackage(), "Test");
    }

    @Test
    public void getClassNameWithoutPackage_LongPackage() {
        FactIdentifier factIdentifier = new FactIdentifier("test", "com.project.Test");
        assertEquals(factIdentifier.getClassNameWithoutPackage(), "Test");
    }

    @Test
    public void getClassNameWithoutPackage_NoPackage() {
        FactIdentifier factIdentifier = new FactIdentifier("test", "Test");
        assertEquals(factIdentifier.getClassNameWithoutPackage(), "Test");
    }

    @Test
    public void getPackageWithoutClassName() {
        FactIdentifier factIdentifier = new FactIdentifier("test", "com.Test");
        assertEquals(factIdentifier.getPackageWithoutClassName(), "com");
    }

    @Test
    public void getPackageWithoutClassName_LongPackage() {
        FactIdentifier factIdentifier = new FactIdentifier("test", "com.project.Test");
        assertEquals(factIdentifier.getPackageWithoutClassName(), "com.project");
    }

    @Test
    public void getPackageWithoutClassName_NoPackage() {
        FactIdentifier factIdentifier = new FactIdentifier("test", "Test");
        assertEquals(factIdentifier.getPackageWithoutClassName(), "");
    }
}
