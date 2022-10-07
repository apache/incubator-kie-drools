/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.io;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory.readAllBytes;
import static org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory.runtimeLoader;

class URIContentLoaderTest {

    @Test
    void testExistingFile() throws IOException {
        assertThat(new String(readAllBytes(runtimeLoader("file:/pepe.txt")))).isEqualTo("my name is javierito");
    }

    @Test
    void testExistingClasspath() throws IOException {
        assertThat(new String(readAllBytes(runtimeLoader("classpath:/pepe.txt")))).isEqualTo("my name is javierito");
    }

    @Test
    void testNotExistingFile() {
        assertThatExceptionOfType(UncheckedIOException.class).isThrownBy(() -> readAllBytes(runtimeLoader("file:/noPepe.txt")));
    }

    @Test
    void testNotExistingClasspath() {
        assertThatIllegalArgumentException().isThrownBy(() -> readAllBytes(runtimeLoader("classpath:/noPepe.txt")));
    }

}
