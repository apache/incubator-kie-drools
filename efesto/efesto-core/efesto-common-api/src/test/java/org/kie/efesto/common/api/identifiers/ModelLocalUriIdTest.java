/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.efesto.common.api.identifiers;

import org.junit.jupiter.api.Test;

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
    void testModel() {
        String path = "/example/some-id/instances/some-instance-id";
        LocalUri parsed = LocalUri.parse(path);
        ModelLocalUriId retrieved = new ModelLocalUriId(parsed);
        assertThat(retrieved.model()).isEqualTo("example");
    }

    @Test
    void testBasePath() {
        String path = "/example/some-id/instances/some-instance-id";
        LocalUri parsed = LocalUri.parse(path);
        ModelLocalUriId retrieved = new ModelLocalUriId(parsed);
        assertThat(retrieved.basePath()).isEqualTo("/some-id/instances/some-instance-id");
        path = "/example";
        parsed = LocalUri.parse(path);
        retrieved = new ModelLocalUriId(parsed);
        assertThat(retrieved.basePath()).isEqualTo("");
    }

    @Test
    void testFullPath() {
        String path = "/example/some-id/instances/some-instance-id";
        LocalUri parsed = LocalUri.parse(path);
        ModelLocalUriId retrieved = new ModelLocalUriId(parsed);
        assertThat(retrieved.fullPath()).isEqualTo(path);
        path = "/example";
        parsed = LocalUri.parse(path);
        retrieved = new ModelLocalUriId(parsed);
        assertThat(retrieved.fullPath()).isEqualTo(path);
    }
}