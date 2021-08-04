/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.decision;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.Properties;
import java.util.function.Consumer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DecisionValidationTest {

    private DecisionCodegen codeGenerator(String path, Consumer<Properties> codeGenContextProperties) throws Exception {
        Properties props = new Properties();
        codeGenContextProperties.accept(props);

        KogitoBuildContext context = JavaKogitoBuildContext.builder()
                .withApplicationProperties(props)
                .build();

        return DecisionCodegen.ofPath(context, Paths.get(path).toAbsolutePath());
    }

    @Test
    public void testDefault() throws Exception {
        DecisionCodegen codeGenerator = codeGenerator("src/test/resources/decision-validation-duplicateName",
                p -> {
                });
        assertThrows(RuntimeException.class,
                () -> {
                    codeGenerator.generate();
                },
                "Expected Validation would have failed for defaults.");
    }

    @Test
    public void testIgnore() throws Exception {
        DecisionCodegen codeGenerator = codeGenerator("src/test/resources/decision-validation-duplicateName",
                p -> p.setProperty(DecisionCodegen.VALIDATION_CONFIGURATION_KEY, "IGNORE"));
        Collection<GeneratedFile> files = codeGenerator.generate();
        Assertions.assertThat(files).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    public void testDisabled() throws Exception {
        DecisionCodegen codeGenerator = codeGenerator("src/test/resources/decision-validation-duplicateName",
                p -> p.setProperty(DecisionCodegen.VALIDATION_CONFIGURATION_KEY, "DISABLED"));
        Collection<GeneratedFile> files = codeGenerator.generate();
        Assertions.assertThat(files).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    public void testDTAnalysisDefault() throws Exception {
        DecisionCodegen codeGenerator = codeGenerator("src/test/resources/decision-validation-DTsemanticError",
                p -> {
                });
        assertThrows(RuntimeException.class,
                () -> {
                    codeGenerator.generate();
                },
                "Expected Validation would have failed for defaults.");
    }

    @Test
    public void testDTAnalysisIgnore() throws Exception {
        DecisionCodegen codeGenerator = codeGenerator("src/test/resources/decision-validation-DTsemanticError",
                p -> p.setProperty(DecisionCodegen.VALIDATION_CONFIGURATION_KEY, "IGNORE"));
        Collection<GeneratedFile> files = codeGenerator.generate();
        Assertions.assertThat(files).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    public void testDTAnalysisDisabled() throws Exception {
        DecisionCodegen codeGenerator = codeGenerator("src/test/resources/decision-validation-DTsemanticError",
                p -> p.setProperty(DecisionCodegen.VALIDATION_CONFIGURATION_KEY, "DISABLED"));
        Collection<GeneratedFile> files = codeGenerator.generate();
        Assertions.assertThat(files).hasSizeGreaterThanOrEqualTo(1);
    }
}
