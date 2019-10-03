/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.metadata;

import java.io.File;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PersistenceLabelerTest {

    @Test
    void testGenerateLabels() throws URISyntaxException {
        final PersistenceLabeler labeler = new PersistenceLabeler();
        final File protoFile = new File(this.getClass().getResource("/kogito-types.proto").toURI());

        assertThat(protoFile).isNotNull();

        labeler.processProto(protoFile);

        final Map<String, String> labels = labeler.generateLabels();

        assertThat(labels).size().isEqualTo(1);
        assertThat(labels).containsKey(labeler.generateKey(protoFile));
    }

    @Test
    void testGenerateLabelsIOException() throws URISyntaxException {
        final PersistenceLabeler labeler = new PersistenceLabeler();
        Assertions.assertThrows(UncheckedIOException.class, () -> labeler.processProto(new File("/does/not/exist")));
    }

}
