/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.api.identifiers;

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.LocalId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;

class LocalPredictionIdTest {

    private static final String fileName = "fileName";
    private static final String name = "name";

    @Test
    void prefix() {
        String retrieved = new LocalPredictionId(fileName, name).asLocalUri().toUri().getPath();
        String expected = SLASH + LocalPredictionId.PREFIX + SLASH;
        assertThat(retrieved).startsWith(expected);
    }

    @Test
    void getFileName() {
        LocalPredictionId retrieved = new LocalPredictionId(fileName, name);
        assertThat(retrieved.getFileName()).isEqualTo(fileName);
    }

    @Test
    void name() {
        LocalPredictionId retrieved = new LocalPredictionId(fileName, name);
        assertThat(retrieved.name()).isEqualTo(name);
    }

    @Test
    void toLocalId() {
        LocalPredictionId localPredictionId = new LocalPredictionId(fileName, name);
        LocalId retrieved = localPredictionId.toLocalId();
        assertThat(retrieved).isEqualTo(localPredictionId);
    }
}