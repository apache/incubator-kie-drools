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

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PathUtilsTest {

    @DisplayName("getSecuredPath by String base path with valid paths")
    @ParameterizedTest
    @MethodSource("validPaths")
    void getSecuredPathByStringBasePathValidPaths(String[] parameters) {
        Path retrieved = PathUtils.getSecuredPath("src/test/resources", parameters[0]);
        assertThat(retrieved.toFile().getName()).isEqualTo(parameters[1]);
    }

    @DisplayName("getSecuredPath by Path base path with valid paths")
    @ParameterizedTest
    @MethodSource("validPaths")
    void getSecuredPathByPathBasePathValidPaths(String[] parameters) {
        Path basePath = Paths.get("src/test/resources");
        Path retrieved = PathUtils.getSecuredPath(basePath, parameters[0]);
        assertThat(retrieved.toFile().getName()).isEqualTo(parameters[1]);
    }

    @DisplayName("getSecuredPath by String base path with invalid paths")
    @ParameterizedTest
    @MethodSource("invalidPaths")
    void getSecuredPathByStringBasePathInvalidPaths(String invalidPath) {
        assertThatExceptionOfType(SecurityException.class)
                .isThrownBy(() -> PathUtils.getSecuredPath("src/test/resources", invalidPath))
                .withMessageContaining("Path traversal attempt detected");
    }

    @DisplayName("getSecuredPath by Path base path with invalid paths")
    @ParameterizedTest
    @MethodSource("invalidPaths")
    void getSecuredPathByPathBasePathInvalidPaths(String invalidPath) {
        Path basePath = Paths.get("src/test/resources");
        assertThatExceptionOfType(SecurityException.class)
                .isThrownBy(() -> PathUtils.getSecuredPath(basePath, invalidPath))
                .withMessageContaining("Path traversal attempt detected");
    }

    static Stream<Arguments> validPaths() {
        return Stream.of(
                Arguments.of(Named.of("ValidPath", new String[]{"META-INF/file.svg", "file.svg"})),
                Arguments.of(Named.of("WindowsSeparators", new String[]{"META-INF\\file.svg", "file.svg"})),
                Arguments.of(Named.of("TrailingSlash", new String[]{"META-INF/", "META-INF"})),
                Arguments.of(Named.of("RedundantDots", new String[]{"./META-INF/./file.svg", "file.svg"})),
                Arguments.of(Named.of("ParentDirectory", new String[]{"META-INF/../META-INF/file.svg", "file.svg"}))
        );
    }

    static Stream<Arguments> invalidPaths() {
        return Stream.of(
                Arguments.of(Named.of("PathTraversal", "../outside/file.svg")),
                Arguments.of(Named.of("DeepTraversal", "../../../etc/passwd")),
                Arguments.of(Named.of("AbsolutePath", "/etc/passwd"))
        );
    }

}
