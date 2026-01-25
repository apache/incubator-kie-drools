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
package org.kie.dmn.efesto.compiler.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.efesto.compiler.utils.DmnCompilerUtils.CLEANABLE_PATTERNS;

class DmnCompilerUtilsTest {

    @Test
    void getCleanedFileNameForURIByFile() {
        String fileNameWithPrefixAndSuffix = File.separator + Path.of("path", "to", "File.dmn");
        String expected = "path/to/File";
        CLEANABLE_PATTERNS.forEach(patternToClean -> {
            File fileToClean = Path.of(patternToClean, fileNameWithPrefixAndSuffix).toFile();
            assertThat(DmnCompilerUtils.getCleanedFilenameForURI(fileToClean)).isEqualTo(expected);
            fileToClean = Path.of("..", "..", patternToClean, fileNameWithPrefixAndSuffix).toFile();
            assertThat(DmnCompilerUtils.getCleanedFilenameForURI(fileToClean)).isEqualTo(expected);
        });
    }

    @Test
    void getCleanedFileNameForURIByStringOSDependent() {
        String fileNameWithPrefixAndSuffix = File.separator + Path.of("path", "to", "File.dmn");
        String expected = "path/to/File";
        CLEANABLE_PATTERNS.forEach(patternToClean -> {
            String filenameToClean = Path.of(patternToClean, fileNameWithPrefixAndSuffix).toString();
            assertThat(DmnCompilerUtils.getCleanedFilenameForURI(filenameToClean)).isEqualTo(expected);
            filenameToClean = Path.of("..", "..", patternToClean, fileNameWithPrefixAndSuffix).toString();
            assertThat(DmnCompilerUtils.getCleanedFilenameForURI(filenameToClean)).isEqualTo(expected);
        });
    }

    @Test
    void getCleanedFileNameForURIByStringOSAgnostic() {
        String fileNameWithPrefixAndSuffix = File.separator + Path.of("path", "to", "File.dmn");
        String expected = "path/to/File";
        CLEANABLE_PATTERNS.forEach(patternToClean -> {
            String filenameToClean = Path.of(patternToClean, fileNameWithPrefixAndSuffix).toString().replace(File.separator, "/");
            assertThat(DmnCompilerUtils.getCleanedFilenameForURI(filenameToClean)).isEqualTo(expected);
            filenameToClean = Path.of("..", "..", patternToClean, fileNameWithPrefixAndSuffix).toString().replace(File.separator, "/");
            assertThat(DmnCompilerUtils.getCleanedFilenameForURI(filenameToClean)).isEqualTo(expected);
        });
    }

    @Test
    void getCleanedFileNameForURIByPatternForURIOSDependent() {
        String expected = (File.separator + Path.of("path", "to", "File")).replace(File.separator, "/");
        String fileNameWithPrefixAndSuffix = File.separator + Path.of("path", "to", "File.dmn");
        CLEANABLE_PATTERNS.forEach(patternToClean -> {
            String filenameToClean = Path.of(patternToClean, fileNameWithPrefixAndSuffix).toString();
            assertThat(DmnCompilerUtils.getCleanedFileNameForURIByPattern(filenameToClean, patternToClean)).isEqualTo(expected);
            filenameToClean = Path.of("..", "..", patternToClean, fileNameWithPrefixAndSuffix).toString();
            assertThat(DmnCompilerUtils.getCleanedFileNameForURIByPattern(filenameToClean, patternToClean)).isEqualTo(expected);
        });
    }

    @Test
    void getCleanedFileNameForURIByPatternForURIOSAgnostic() {
        String expected = (File.separator + Path.of("path", "to", "File")).replace(File.separator, "/");
        String fileNameWithPrefixAndSuffix = File.separator + Path.of("path", "to", "File.dmn");
        CLEANABLE_PATTERNS.forEach(patternToClean -> {
            String filenameToClean = Path.of(patternToClean, fileNameWithPrefixAndSuffix).toString().replace(File.separator, "/");
            assertThat(DmnCompilerUtils.getCleanedFileNameForURIByPattern(filenameToClean, patternToClean)).isEqualTo(expected);
            filenameToClean = Path.of("..", "..", patternToClean, fileNameWithPrefixAndSuffix).toString().replace(File.separator, "/");
            assertThat(DmnCompilerUtils.getCleanedFileNameForURIByPattern(filenameToClean, patternToClean)).isEqualTo(expected);
        });
    }


}