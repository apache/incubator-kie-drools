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
package org.kie.efesto.common.api.identifiers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ModelLocalUriIdTest {

    @Test
    void getFirstLocalUriPathComponent() {
        String path = "/example/some-id/instances/some-instance-id";
        LocalUri parsed = LocalUri.parse(path);
        LocalUri retrieved = ModelLocalUriId.getFirstLocalUriPathComponent(parsed);
        LocalUri expected = LocalUri.parse("/example");
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void asModelLocalUriIdWithModelLocalUriId() {
        String path = "/example/some-id/instances/some-instance-id";
        LocalUri parsed = LocalUri.parse(path);
        ModelLocalUriId retrieved = new ModelLocalUriId(parsed);
        ModelLocalUriId modelLocalUriId = retrieved.asModelLocalUriId();
        assertThat(modelLocalUriId).isEqualTo(retrieved);
        assertThat(modelLocalUriId == retrieved).isTrue();
    }

    @Test
    void asModelLocalUriIdWithExtendingModelLocalUriId() {
        String path = "/example/some-id/instances/some-instance-id";
        LocalUri parsed = LocalUri.parse(path);
        ExtendingModelLocalUriId retrieved = new ExtendingModelLocalUriId(parsed);
        ModelLocalUriId modelLocalUriId = retrieved.asModelLocalUriId();
        assertThat(modelLocalUriId).isEqualTo(retrieved);
        assertThat(modelLocalUriId == retrieved).isFalse();
    }

    @Test
    void model() {
        String path = "/example/some-id/instances/some-instance-id";
        LocalUri parsed = LocalUri.parse(path);
        ModelLocalUriId retrieved = new ModelLocalUriId(parsed);
        assertThat(retrieved.model()).isEqualTo("example");
    }

    @ParameterizedTest
    @ValueSource(strings = {"/example/some-id/instances/some-instance-id", "/example"})
    void basePath(String path) {
        LocalUri parsed = LocalUri.parse(path);
        ModelLocalUriId retrieved = new ModelLocalUriId(parsed);
        String expected = path.replace("/example", "");
        assertThat(retrieved.basePath()).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/example/some-id/instances/some-instance-id", "/example"})
    void fullPath(String path) {
        LocalUri parsed = LocalUri.parse(path);
        ModelLocalUriId retrieved = new ModelLocalUriId(parsed);
        assertThat(retrieved.fullPath()).isEqualTo(path);
    }

    private static class ExtendingModelLocalUriId extends ModelLocalUriId {

        private static final long serialVersionUID = 640021472211924827L;

        public ExtendingModelLocalUriId(LocalUri path) {
            super(path);
        }
    }
}