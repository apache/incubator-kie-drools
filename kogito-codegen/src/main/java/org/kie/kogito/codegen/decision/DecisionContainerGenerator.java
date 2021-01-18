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

import java.util.Collection;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.kogito.codegen.AbstractApplicationSection;
import org.kie.kogito.codegen.InvalidTemplateException;
import org.kie.kogito.codegen.TemplatedGenerator;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.codegen.io.CollectedResource;
import org.kie.kogito.dmn.DmnExecutionIdSupplier;

import static org.kie.kogito.codegen.CodegenUtils.newObject;
import static org.kie.kogito.codegen.decision.ReadResourceUtil.getReadResourceMethod;

public class DecisionContainerGenerator extends AbstractApplicationSection {

    private static final String SECTION_CLASS_NAME = "DecisionModels";

    private final String applicationCanonicalName;
    private final Collection<CollectedResource> resources;
    private final TemplatedGenerator templatedGenerator;

    public DecisionContainerGenerator(KogitoBuildContext context, String applicationCanonicalName, Collection<CollectedResource> cResources) {
        super(context, SECTION_CLASS_NAME);
        this.applicationCanonicalName = applicationCanonicalName;
        this.resources = cResources;
        this.templatedGenerator = TemplatedGenerator.builder()
                .withTargetTypeName(SECTION_CLASS_NAME)
                .build(context, "DecisionContainer");
    }

    @Override
    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit = templatedGenerator.compilationUnitOrThrow("Invalid Template: No CompilationUnit");


        ClassOrInterfaceType applicationClass = StaticJavaParser.parseClassOrInterfaceType(applicationCanonicalName);

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

        setupExecIdSupplierVariable(initMethod);
        setupDecisionModelTransformerVariable(initMethod);

        for (CollectedResource resource : resources) {
            MethodCallExpr getResAsStream = getReadResourceMethod(applicationClass, resource);
            MethodCallExpr isr = new MethodCallExpr("readResource").addArgument(getResAsStream);
            initMethod.addArgument(isr);
        }

        return compilationUnit;
    }

    private void setupExecIdSupplierVariable(MethodCallExpr initMethod) {
        Expression execIdSupplier = context.getAddonsConfig().useTracing() ?
                newObject(DmnExecutionIdSupplier.class) :
                new NullLiteralExpr();
        initMethod.addArgument(execIdSupplier);
    }

    private void setupDecisionModelTransformerVariable(MethodCallExpr initMethod) {
        Expression decisionModelTransformerExpr = context.getAddonsConfig().useMonitoring() ?
                newObject("org.kie.kogito.monitoring.core.common.decision.MonitoredDecisionModelTransformer") :
                new NullLiteralExpr();
        initMethod.addArgument(decisionModelTransformerExpr);
    }
}
