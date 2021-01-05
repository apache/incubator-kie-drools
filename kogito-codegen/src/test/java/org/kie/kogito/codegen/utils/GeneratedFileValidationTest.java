/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.GeneratedFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.kie.kogito.codegen.GeneratedFile.Type.CLASS;
import static org.kie.kogito.codegen.GeneratedFile.Type.GENERATED_CP_RESOURCE;
import static org.kie.kogito.codegen.GeneratedFile.Type.RESOURCE;

class GeneratedFileValidationTest {

    @Test
    public void validateGeneratedFileTypes() {
        List<GeneratedFile> generatedFiles = Arrays.asList(
                new GeneratedFile(CLASS, "myPath1", ""),
                new GeneratedFile(RESOURCE, "myPath2", ""),
                new GeneratedFile(GENERATED_CP_RESOURCE, "myPath3", "")
        );

        GeneratedFileValidation.validateGeneratedFileTypes(generatedFiles, Arrays.asList(CLASS, RESOURCE, GENERATED_CP_RESOURCE));
        Set<GeneratedFile.Type> types = Collections.singleton(CLASS);
        Assertions.assertThatThrownBy(() -> GeneratedFileValidation.validateGeneratedFileTypes(generatedFiles, types))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("myPath2")
                .hasMessageContaining("myPath3");
    }

}