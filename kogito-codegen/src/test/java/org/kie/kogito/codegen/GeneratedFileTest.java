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

package org.kie.kogito.codegen;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class GeneratedFileTest {

    private static final GeneratedFile.Type TEST_TYPE = GeneratedFile.Type.RULE;
    private static final String TEST_RELATIVE_PATH = "relativePath";
    private static final byte[] TEST_CONTENTS = "testContents".getBytes(StandardCharsets.UTF_8);

    private static GeneratedFile testFile;

    @BeforeAll
    public static void createTestFile() {
        testFile = new GeneratedFile(TEST_TYPE, TEST_RELATIVE_PATH, TEST_CONTENTS);
    }

    @Test
    public void relativePath() {
        assertThat(testFile.relativePath()).isEqualTo(TEST_RELATIVE_PATH);
    }

    @Test
    public void contents() {
        assertThat(testFile.contents()).isEqualTo(TEST_CONTENTS);
    }

    @Test
    public void getType() {
        assertThat(testFile.getType()).isEqualTo(TEST_TYPE);
    }
}
