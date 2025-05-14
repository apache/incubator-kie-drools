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

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.efesto.compiler.utils.DmnCompilerUtils.CLEANABLE_PATTERNS;

class DmnCompilerUtilsTest {

    @Test
    public void getCleanedFileName() {
        String fileName = "path/to/File";
        String fileNameWithPrefixAndSuffix = String.format("/%s.dmn", fileName);
        CLEANABLE_PATTERNS.forEach(patternToClean -> {
            String filenameToClean = String.format("%s%s", patternToClean, fileNameWithPrefixAndSuffix);
            assertThat(DmnCompilerUtils.getCleanedFilename(filenameToClean)).isEqualTo(fileName);
            filenameToClean = String.format("../../%s%s", patternToClean, fileNameWithPrefixAndSuffix);
            assertThat(DmnCompilerUtils.getCleanedFilename(filenameToClean)).isEqualTo(fileName);
        });
    }

    @Test
    public void getCleanedFileNameByPattern() {
        String fileName = "path/to/File";
        String fileNameWithPrefixAndSuffix = String.format("/%s.dmn", fileName);
        CLEANABLE_PATTERNS.forEach(patternToClean -> {
            String filenameToClean = String.format("%s%s", patternToClean, fileNameWithPrefixAndSuffix);
            assertThat(DmnCompilerUtils.getCleanedFileNameByPattern(filenameToClean, patternToClean)).isEqualTo(fileName);
            filenameToClean = String.format("../../%s%s", patternToClean, fileNameWithPrefixAndSuffix);
            assertThat(DmnCompilerUtils.getCleanedFileNameByPattern(filenameToClean, patternToClean)).isEqualTo(fileName);
        });
    }
}