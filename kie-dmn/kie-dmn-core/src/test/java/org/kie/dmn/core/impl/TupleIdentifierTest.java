/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TupleIdentifierTest {

    @Test
    void createTupleIdentifierById() {
        String name = "name";
        String wrongName = "wrong-name";
        String retrieved = TupleIdentifier.generateIdFromName(name);
        assertEquals(retrieved, TupleIdentifier.generateIdFromName(name));
        assertNotEquals(retrieved, TupleIdentifier.generateIdFromName(wrongName));
    }

    @Test
    void createTupleIdentifierByName() {
        String id = "123124";
        TupleIdentifier retrieved = TupleIdentifier.createTupleIdentifierById(id);
        assertNotNull(retrieved);
        assertEquals(retrieved.getId(), id);
        assertNull(retrieved.getName());
    }

    @Test
    void generateIdFromName() {
        String name = "name";
        String wrongName = "wrong-name";
        String retrieved = TupleIdentifier.generateIdFromName(name);
        assertEquals(retrieved, TupleIdentifier.generateIdFromName(name));
        assertNotEquals(retrieved, TupleIdentifier.generateIdFromName(wrongName));
    }

    @Test
    void testTupleIdentifier() {
        String id = "123124";
        String wrongId = "3242342";
        String name = "name";
        String wrongName = "wrong-name";
        TupleIdentifier original = new TupleIdentifier(id, name);
        assertEquals(original, new TupleIdentifier(id, name));
        assertEquals(original, new TupleIdentifier(null, name));
        assertEquals(original, new TupleIdentifier(id, null));
        assertNotEquals(original, new TupleIdentifier(id, wrongName));
        assertNotEquals(original, new TupleIdentifier(wrongId, name));
        assertNotEquals(original, new TupleIdentifier(wrongId, wrongName));
        assertNotEquals(original, new TupleIdentifier(null, wrongName));
        assertNotEquals(original, new TupleIdentifier(wrongId, null));

    }
}