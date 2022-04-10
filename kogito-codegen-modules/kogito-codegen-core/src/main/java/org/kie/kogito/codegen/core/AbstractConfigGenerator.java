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
package org.kie.kogito.codegen.core;

import org.drools.codegen.common.GeneratedFile;
import org.kie.kogito.codegen.api.ConfigGenerator;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;

import com.github.javaparser.ast.CompilationUnit;

import static org.kie.kogito.codegen.core.ApplicationConfigGenerator.TEMPLATE_CONFIG_FOLDER;

public abstract class AbstractConfigGenerator implements ConfigGenerator {

    protected final TemplatedGenerator templatedGenerator;
    protected final KogitoBuildContext context;
    protected final String configClassName;

    public AbstractConfigGenerator(KogitoBuildContext context, String targetTypeName) {
        this.context = context;
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
        return new GeneratedFile(APPLICATION_CONFIG_TYPE,
                templatedGenerator.generatedFilePath(),
                toCompilationUnit().toString());
    }

    protected CompilationUnit toCompilationUnit() {
        return templatedGenerator.compilationUnitOrThrow();
    }
}
