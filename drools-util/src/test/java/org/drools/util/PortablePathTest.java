/*
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
package org.drools.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PortablePathTest {

    private PortablePath base;

    @BeforeEach
    void setUp() {
        base = PortablePath.of("src/test/resources/META-INF/processSVG");
    }

    @Test
    void testResolveAcceptsValidPath() {
        PortablePath result = base.resolve("travels.svg");
        assertThat(result.asString()).startsWith(base.asString());
    }

    @Test
    void testResolveNestedPathIsValid() {
        PortablePath result = base.resolve("subdir/file.svg");
        assertThat(result.asString()).startsWith(base.asString());
        assertThat(result.getFileName()).isEqualTo("file.svg");
    }

    @Test
    void testResolveCleansUpRedundantNavigation() {
        PortablePath result = base.resolve("subdir/../file.svg");
        assertThat(result.asString()).startsWith(base.asString());
        assertThat(result.getFileName()).isEqualTo("file.svg");
    }

    @Test
    void testResolvePathWithTrailingSlash() {
        PortablePath result = base.resolve("subdir/");
        assertThat(result.asString()).isEqualTo(base.asString() + "/subdir");
    }

    @Test
    void testResolveWithValidDeepNestedPath() {
        PortablePath result = base.resolve("deep/nested/subdir/file.svg");
        assertThat(result.asString()).startsWith(base.asString());
    }

    @Test
    void testResolveWithRedundantCurrentDirectory() {
        PortablePath result = base.resolve("subdir/./file.svg");
        assertThat(result.asString()).startsWith(base.asString());
        assertThat(result.getFileName()).isEqualTo("file.svg");
    }

    @Test
    void testResolveWithRedundantParentDirectory() {
        PortablePath result = base.resolve("subdir/../subdir/file.svg");
        assertThat(result.asString()).startsWith(base.asString());
        assertThat(result.getFileName()).isEqualTo("file.svg");
    }


    @Test
    void testResolveInternalAcceptsValidPath() {
        Path result = PortablePath.resolveInternal("src/test/resources", "META-INF/file.svg").toPath();
        PortablePath p = PortablePath.of(result.toString());
        assertThat(p.getFileName()).isEqualTo("file.svg");
    }

    @Test
    void testResolveInternalRejectsPathTraversal() {
        assertThatExceptionOfType(SecurityException.class)
                .isThrownBy(() -> PortablePath.resolveInternal("src/test/resources", "../outside/file.svg").toPath())
                .withMessageContaining("Path traversal attempt detected");
    }

    @Test
    void testResolveInternalRejectsDeepTraversal() {
        assertThatExceptionOfType(SecurityException.class)
                .isThrownBy(() -> PortablePath.resolveInternal("src/test/resources", "../../../etc/passwd").toPath())
                .withMessageContaining("Path traversal attempt detected");
    }

    @Test
    void testResolveInternalRejectsAbsolutePath() {
        String absPath = Paths.get("/etc/passwd").toAbsolutePath().toString();
        assertThatExceptionOfType(SecurityException.class)
                .isThrownBy(() -> PortablePath.resolveInternal("src/test/resources", absPath))
                .withMessageContaining("Path traversal attempt detected");
    }

    @Test
    void testResolveInternalNormalizesWindowsSeparators() {
        Path result = PortablePath.resolveInternal("src/test/resources", "META-INF\\file.svg").toPath();
        PortablePath p = PortablePath.of(result.toString());
        assertThat(p.getFileName()).isEqualTo("file.svg");                                                                           
    }

    @Test
    void testResolveInternalHandlesTrailingSlash() {
        Path result = PortablePath.resolveInternal("src/test/resources", "META-INF/").toPath();
        PortablePath p = PortablePath.of(result.toString());
        assertThat(p.getFileName()).isEqualTo("META-INF");
    }

    @Test
    void testResolveInternalStripsRedundantDots() {
        Path result = PortablePath.resolveInternal("src/test/resources", "./META-INF/./file.svg").toPath();
        PortablePath p = PortablePath.of(result.toString());
        assertThat(p.getFileName()).isEqualTo("file.svg");
    }

    @Test
    void testResolveInternalNormalizesParentDirectory() {
        Path result = PortablePath.resolveInternal("src/test/resources", "META-INF/../META-INF/file.svg").toPath();
        PortablePath p = PortablePath.of(result.toString());
        assertThat(p.getFileName()).isEqualTo("file.svg");
    }

}
