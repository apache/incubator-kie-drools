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

package org.kie.kogito.codegen;

import com.github.javaparser.ast.CompilationUnit;
import org.kie.kogito.codegen.context.KogitoBuildContext;

import static org.kie.kogito.codegen.ApplicationConfigGenerator.TEMPLATE_CONFIG_FOLDER;

public abstract class AbstractConfigGenerator implements ConfigGenerator {

    private final TemplatedGenerator templatedGenerator;
    private final String configClassName;

    public AbstractConfigGenerator(KogitoBuildContext context, String targetTypeName) {
        configClassName = targetTypeName;
        this.templatedGenerator = TemplatedGenerator.builder()
                .withTemplateBasePath(TEMPLATE_CONFIG_FOLDER)
                .build(context, targetTypeName);
    }

    @Override
    public String configClassName() {
        return configClassName;
    }

    @Override
    public GeneratedFile generate() {
        CompilationUnit compilationUnit = templatedGenerator.compilationUnitOrThrow();
        return new GeneratedFile(ApplicationConfigGenerator.APPLICATION_CONFIG_TYPE,
                        templatedGenerator.generatedFilePath(),
                        compilationUnit.toString());
    }
}
