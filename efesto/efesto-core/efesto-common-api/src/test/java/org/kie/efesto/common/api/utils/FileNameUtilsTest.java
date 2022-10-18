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
package org.kie.efesto.common.api.utils;

import org.junit.jupiter.api.Test;


import static java.io.File.separator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.utils.FileNameUtils.getFileName;
import static org.kie.efesto.common.api.utils.FileNameUtils.getSuffix;
import static org.kie.efesto.common.api.utils.FileNameUtils.removeSuffix;

class FileNameUtilsTest {


    @Test
    void getFileName_noDirectories() {
        assertThat(getFileName("file_name.txt")).isEqualTo("file_name.txt");
    }
    
    @Test
    void getFileName_oneDirectory() {
        assertThat(getFileName("dir" + separator + "file_name.txt")).isEqualTo("file_name.txt");
    }

    
    @Test
    void getFileName_manyDirectories() {
        assertThat(getFileName("dir" + separator + "dir" + separator + "file_name.txt")).isEqualTo("file_name.txt");
    }

    @Test
    void getSuffix_noSuffix() {
        assertThat(getSuffix("file_name")).isEqualTo("file_name");
    }

    @Test
    void getSuffix_withSuffix() {
        assertThat(getSuffix("file_name.model_json")).isEqualTo("model_json");
    }
    
    @Test
    void getSuffix_pathIsIrrelevant() {
        assertThat(getSuffix("dir" + separator + "file_name.model_json")).isEqualTo("model_json");
    }

    @Test
    void removeSuffix_noSuffix() {
        assertThat(removeSuffix("file_name.model_json")).isEqualTo("file_name");
    }

    
}