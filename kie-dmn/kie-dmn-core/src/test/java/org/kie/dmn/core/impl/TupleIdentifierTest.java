/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core.impl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TupleIdentifierTest {

    @Test
    void createTupleIdentifier() {
        String id = "123124";
        String name = "name";
        TupleIdentifier retrieved = TupleIdentifier.createTupleIdentifier(id, name);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getId()).isEqualTo(id);
        assertThat(retrieved.getName()).isEqualTo(name);
    }

    @Test
    void createTupleIdentifierById() {
        String id = "123124";
        TupleIdentifier retrieved = TupleIdentifier.createTupleIdentifierById(id);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getId()).isEqualTo(id);
        assertThat(retrieved.getName()).isNotNull();
    }

    @Test
    void createTupleIdentifierByName() {
        String name = "name";
        TupleIdentifier retrieved = TupleIdentifier.createTupleIdentifierByName(name);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo(name);
        assertThat(retrieved.getId()).isNotNull();
    }

    @Test
    void generateIdFromName() {
        String name = "name";
        String wrongName = "wrong-name";
        String retrieved = TupleIdentifier.generateIdFromName(name);
        assertThat(retrieved).isEqualTo(TupleIdentifier.generateIdFromName(name))
                .isNotEqualTo(TupleIdentifier.generateIdFromName(wrongName));
    }

    @Test
    void generateNameFromId() {
        String id = "123124";
        String wrongId = "423423";
        String retrieved = TupleIdentifier.generateNameFromId(id);
        assertThat(retrieved).isEqualTo(TupleIdentifier.generateNameFromId(id))
                .isNotEqualTo(TupleIdentifier.generateNameFromId(wrongId));
    }

    @Test
    void testTupleIdentifierEquality() {
        String id = "123124";
        String wrongId = "3242342";
        String name = "name";
        String wrongName = "wrong-name";
        TupleIdentifier original = new TupleIdentifier(id, name);
        commonTestEquality(original, new TupleIdentifier(id, name), true);
        commonTestEquality(original, new TupleIdentifier(id, name), true);
        commonTestEquality(original, new TupleIdentifier(null, name), true);
        commonTestEquality(original, new TupleIdentifier(id, null), true);

        commonTestEquality(original, new TupleIdentifier(id, wrongName), false);
        commonTestEquality(original, new TupleIdentifier(wrongId, name), false);
        commonTestEquality(original, new TupleIdentifier(wrongId, wrongName), false);
        commonTestEquality(original, new TupleIdentifier(null, wrongName), false);
        commonTestEquality(original, new TupleIdentifier(wrongId, null), false);
    }

    private void commonTestEquality(TupleIdentifier original, TupleIdentifier comparison, boolean shouldBeEqual) {
        if (shouldBeEqual) {
            assertThat(original).isEqualTo(comparison);
            assertThat(comparison).isEqualTo(original);
        } else {
            assertThat(original).isNotEqualTo(comparison);
            assertThat(comparison).isNotEqualTo(original);
        }
    }
}