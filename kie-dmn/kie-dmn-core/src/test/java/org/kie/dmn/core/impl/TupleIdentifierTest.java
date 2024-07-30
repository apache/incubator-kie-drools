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
    void createTupleIdentifierById() {
        String name = "name";
        String wrongName = "wrong-name";
        String retrieved = TupleIdentifier.generateIdFromName(name);
        assertThat(retrieved).isEqualTo(TupleIdentifier.generateIdFromName(name));
        assertThat(retrieved).isNotEqualTo(TupleIdentifier.generateIdFromName(wrongName));
    }

    @Test
    void createTupleIdentifierByName() {
        String id = "123124";
        TupleIdentifier retrieved = TupleIdentifier.createTupleIdentifierById(id);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getId()).isEqualTo(id);
        assertThat(retrieved.getName()).isNotNull();
    }

    @Test
    void generateIdFromName() {
        String name = "name";
        String wrongName = "wrong-name";
        String retrieved = TupleIdentifier.generateIdFromName(name);
        assertThat(retrieved).isEqualTo(TupleIdentifier.generateIdFromName(name));
        assertThat(retrieved).isNotEqualTo(TupleIdentifier.generateIdFromName(wrongName));
    }

    @Test
    void testTupleIdentifier() {
        String id = "123124";
        String wrongId = "3242342";
        String name = "name";
        String wrongName = "wrong-name";
        TupleIdentifier original = new TupleIdentifier(id, name);
        assertThat(original).isEqualTo(new TupleIdentifier(id, name));
        assertThat(original).isEqualTo(new TupleIdentifier(null, name));
        assertThat(original).isEqualTo(new TupleIdentifier(id, null));
        assertThat(original).isNotEqualTo(new TupleIdentifier(id, wrongName));
        assertThat(original).isNotEqualTo(new TupleIdentifier(wrongId, name));
        assertThat(original).isNotEqualTo(new TupleIdentifier(wrongId, wrongName));
        assertThat(original).isNotEqualTo(new TupleIdentifier(null, wrongName));
        assertThat(original).isNotEqualTo(new TupleIdentifier(wrongId, null));
    }
}