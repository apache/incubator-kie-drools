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
package org.kie.kogito.codegen.prediction;

import java.util.Collection;

import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.template.InvalidTemplateException;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;
import org.kie.kogito.codegen.core.AbstractApplicationSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.utils.StringEscapeUtils;

public class PredictionModelsGenerator extends AbstractApplicationSection {

    private static final Logger LOGGER = LoggerFactory.getLogger(PredictionModelsGenerator.class);
    private static final String SECTION_CLASS_NAME = "PredictionModels";

    protected final Collection<PMMLResource> resources;
    protected final String applicationCanonicalName;
    protected final TemplatedGenerator templatedGenerator;

    public PredictionModelsGenerator(KogitoBuildContext context, String applicationCanonicalName, Collection<PMMLResource> resources) {
        super(context, SECTION_CLASS_NAME);
        this.applicationCanonicalName = applicationCanonicalName;
        this.resources = resources;

        this.templatedGenerator = TemplatedGenerator.builder().build(context, SECTION_CLASS_NAME);
    }

    @Override
    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit = templatedGenerator.compilationUnitOrThrow("Invalid Template: No CompilationUnit");
        populateStaticKieRuntimeFactoryFunctionInit(compilationUnit);
        return compilationUnit;
    }

    private void populateStaticKieRuntimeFactoryFunctionInit(CompilationUnit compilationUnit) {
        final InitializerDeclaration staticDeclaration = compilationUnit
                .findFirst(InitializerDeclaration.class)
                .orElseThrow(() -> new InvalidTemplateException(
                        templatedGenerator,
                        "Missing static block"));
        final MethodCallExpr initMethod = staticDeclaration
                .findFirst(MethodCallExpr.class, mtd -> "init".equals(mtd.getNameAsString()))
                .orElseThrow(() -> new InvalidTemplateException(
                        templatedGenerator,
                        "Missing init() method"));

        for (PMMLResource resource : resources) {
            StringLiteralExpr getResAsStream = getReadResourceMethod(resource);
            initMethod.addArgument(getResAsStream);
        }
    }

    private StringLiteralExpr getReadResourceMethod(PMMLResource resource) {
        String source = resource.getModelPath();
        LOGGER.trace("Original source path: {}", source);
        source = StringEscapeUtils.escapeJava(source);
        LOGGER.trace("Escaped source path: {}", source);
        return new StringLiteralExpr(source);
    }
}
