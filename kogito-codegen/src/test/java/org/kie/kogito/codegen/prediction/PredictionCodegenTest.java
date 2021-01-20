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

package org.kie.kogito.codegen.prediction;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.AbstractCodegenTest;
import org.kie.kogito.codegen.ApplicationSection;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.context.JavaKogitoBuildContext;
import org.kie.kogito.codegen.io.CollectedResource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PredictionCodegenTest extends AbstractCodegenTest {

    private static final String SOURCE = "prediction/test_regression.pmml";
    private static final Path BASE_PATH = Paths.get("src/test/resources/").toAbsolutePath();
    private static final Path FULL_SOURCE = BASE_PATH.resolve(SOURCE);

    @Test
    public void generateAllFiles() {
        PredictionCodegen codeGenerator = PredictionCodegen.ofCollectedResources(
                JavaKogitoBuildContext.builder().build(),
                CollectedResource.fromFiles(BASE_PATH, FULL_SOURCE.toFile()));

        List<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertEquals(4, generatedFiles.size());

        Optional<ApplicationSection> optionalApplicationSection = codeGenerator.section();
        assertTrue(optionalApplicationSection.isPresent());
        CompilationUnit compilationUnit = optionalApplicationSection.get().compilationUnit();
        assertNotNull(compilationUnit);
    }

}
