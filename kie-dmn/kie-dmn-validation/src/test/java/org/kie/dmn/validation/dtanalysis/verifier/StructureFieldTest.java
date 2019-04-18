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
package org.kie.dmn.validation.dtanalysis.verifier;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class StructureFieldTest {

    @Test
    public void basic() {
        final AnalyzerConfiguration configuration = mock(AnalyzerConfiguration.class);
        final UUIDKey uuidKey = mock(UUIDKey.class);
        doReturn(uuidKey).when(configuration).getUUID(any());
        final StructureField structureField = new StructureField("name",
                                                                 "fieldType",
                                                                 configuration);

        assertEquals("name", structureField.getName());
        assertEquals("fieldType", structureField.getFieldType());
        assertEquals(uuidKey, structureField.getUuidKey());
        assertFalse(structureField.getRange().isPresent());
    }

    @Test
    public void equal() {
        StructureField a = new StructureField("name",
                                              "fieldType",
                                              mock(AnalyzerConfiguration.class));
        StructureField b = new StructureField("name",
                                              "fieldType",
                                              mock(AnalyzerConfiguration.class));

        assertEquals(0, a.compareTo(b));
        assertEquals(0, b.compareTo(a));
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualByName() {
        StructureField a = new StructureField("aaa",
                                              "fieldType",
                                              mock(AnalyzerConfiguration.class));
        StructureField b = new StructureField("bbb",
                                              "fieldType",
                                              mock(AnalyzerConfiguration.class));

        assertEquals(-1, a.compareTo(b));
        assertEquals(1, b.compareTo(a));
        assertFalse(a.equals(b));
        assertFalse(b.equals(a));
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualByFieldType() {
        StructureField a = new StructureField("name",
                                              "aaa",
                                              mock(AnalyzerConfiguration.class));
        StructureField b = new StructureField("name",
                                              "bbb",
                                              mock(AnalyzerConfiguration.class));

        assertEquals(-1, a.compareTo(b));
        assertEquals(1, b.compareTo(a));
        assertFalse(a.equals(b));
        assertFalse(b.equals(a));
        assertNotEquals(a.hashCode(), b.hashCode());
    }
}