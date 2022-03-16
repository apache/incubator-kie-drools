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
package org.drools.ruleunits.codegen.config;

import com.github.javaparser.ast.CompilationUnit;
import org.drools.ruleunits.codegen.GeneratedFile;
import org.drools.ruleunits.codegen.GeneratedFileType;
import org.drools.ruleunits.codegen.context.KogitoBuildContext;
import org.drools.ruleunits.codegen.template.TemplatedGenerator;

public abstract class AbstractConfigGenerator {
    public static final String TEMPLATE_CONFIG_FOLDER = "/class-templates/config/";
    GeneratedFileType APPLICATION_CONFIG_TYPE = GeneratedFileType.of("APPLICATION_CONFIG", GeneratedFileType.Category.SOURCE);

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

    public String configClassName() {
        return configClassName;
    }

    public GeneratedFile generate() {
        return new GeneratedFile(APPLICATION_CONFIG_TYPE,
                templatedGenerator.generatedFilePath(),
                toCompilationUnit().toString());
    }

    protected CompilationUnit toCompilationUnit() {
        return templatedGenerator.compilationUnitOrThrow();
    }
}
