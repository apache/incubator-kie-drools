/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.template;

import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;
import org.kie.kogito.codegen.core.context.JavaKogitoBuildContext;
import org.kie.kogito.codegen.core.context.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.core.context.SpringBootKogitoBuildContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;
import static org.kie.kogito.codegen.api.template.TemplatedGenerator.createTemplatePath;

class TemplateSanityCheckTest {

    static final List<String> validTemplateNames = Arrays.asList(
            createTemplatePath("", "", JavaKogitoBuildContext.CONTEXT_NAME),
            createTemplatePath("", "", QuarkusKogitoBuildContext.CONTEXT_NAME),
            createTemplatePath("", "", SpringBootKogitoBuildContext.CONTEXT_NAME));

    @Test
    public void templateNameSanityCheck() throws IOException {
        Path path = Paths.get("src/main/resources" + TemplatedGenerator.DEFAULT_TEMPLATE_BASE_PATH);
        List<String> invalidTemplates = Files.walk(path)
                .filter(Files::isRegularFile)
                .filter(this::invalidTemplateName)
                .map(Path::toString)
                .collect(Collectors.toList());

        if(!invalidTemplates.isEmpty()) {
            fail("Found templates with invalid name:\n" + String.join("\n", invalidTemplates));
        }
    }

    private boolean invalidTemplateName(Path path) {
        return validTemplateNames.stream()
                .noneMatch(templateSuffix -> path.toString().endsWith(templateSuffix));
    }
}