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

package org.kie.kogito.codegen.decision;

import java.nio.file.Paths;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.GeneratedFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DecisionCodegenTest {

    @Test
    public void generateSingleFile() throws Exception {
        DecisionCodegen codegenerator = DecisionCodegen.ofPath(Paths.get("src/test/resources/decision").toAbsolutePath());

        List<GeneratedFile> generatedFiles = codegenerator.generate();
        assertEquals(1, generatedFiles.size());

        ClassOrInterfaceDeclaration classDeclaration = codegenerator.moduleGenerator().classDeclaration();
        assertNotNull(classDeclaration);
    }
}
