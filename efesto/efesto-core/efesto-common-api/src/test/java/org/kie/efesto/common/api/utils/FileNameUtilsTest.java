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
package org.kie.efesto.common.api.utils;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class FileNameUtilsTest {


    @Test
    void getFileName() {
        String fileName = "file_name.txt";
        String source = fileName;
        assertThat(FileNameUtils.getFileName(source)).isEqualTo(fileName);
        source = File.separator + "dir" + File.separator + fileName;
        assertThat(FileNameUtils.getFileName(source)).isEqualTo(fileName);
    }

    @Test
    void getSuffix() {
        String fileName = "file_name.model_json";
        String expected = "model_json";
        String source = fileName;
        assertThat(FileNameUtils.getSuffix(source)).isEqualTo(expected);
        source = File.separator + "dir" + File.separator + fileName;
        assertThat(FileNameUtils.getSuffix(source)).isEqualTo(expected);
    }

}