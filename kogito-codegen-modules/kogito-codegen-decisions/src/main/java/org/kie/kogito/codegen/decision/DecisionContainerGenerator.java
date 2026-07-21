/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.codegen.decision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.api.template.InvalidTemplateException;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;
import org.kie.kogito.codegen.core.AbstractApplicationSection;
import org.kie.kogito.codegen.core.CodegenUtils;
import org.kie.kogito.dmn.DmnExecutionIdSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static org.kie.kogito.codegen.core.CodegenUtils.newObject;
import static org.kie.kogito.dmn.AbstractDecisionModels.DMN_MODEL_PATHS_FILE;

public class DecisionContainerGenerator extends AbstractApplicationSection {

    private static final Logger LOG = LoggerFactory.getLogger(DecisionContainerGenerator.class);
    protected static final String PMML_ABSTRACT_CLASS = "org.kie.kogito.pmml.AbstractPredictionModels";
    protected static final String PMML_FUNCTION = PMML_ABSTRACT_CLASS + ".kieRuntimeFactoryFunction";
    static final String MONITORED_DECISIONMODEL_TRANSFORMER = "org.kie.kogito.monitoring.core.common.decision.MonitoredDecisionModelTransformer";
    private static final String SECTION_CLASS_NAME = "DecisionModels";

    private final String applicationCanonicalName;
    private final Collection<CollectedResource> resources;
    private final TemplatedGenerator templatedGenerator;
    private final List<String> classesForManualReflection = new ArrayList<>();
    private final Set<DMNProfile> customDMNProfiles = new HashSet<>();
    private final boolean enableRuntimeTypeCheckOption;

    public DecisionContainerGenerator(KogitoBuildContext context, String applicationCanonicalName, Collection<CollectedResource> cResources, List<String> classesForManualReflection,
            Set<DMNProfile> customDMNProfiles,
            boolean enableRuntimeTypeCheckOption) {
        super(context, SECTION_CLASS_NAME);
        this.applicationCanonicalName = applicationCanonicalName;
        this.resources = cResources;
        this.templatedGenerator = TemplatedGenerator.builder()
                .withTargetTypeName(SECTION_CLASS_NAME)
                .build(context, "DecisionContainer");
        this.classesForManualReflection.addAll(classesForManualReflection);
        this.customDMNProfiles.addAll(customDMNProfiles);
        this.enableRuntimeTypeCheckOption = enableRuntimeTypeCheckOption;
    }

    @Override
    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit = templatedGenerator.compilationUnitOrThrow("Invalid Template: No CompilationUnit");
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

        setupExecIdSupplierVariable(initMethod, context.getAddonsConfig().useTracing());
        setupDecisionModelTransformerVariable(initMethod, context.getAddonsConfig().useMonitoring());
        setupCustomDMNProfiles(initMethod, customDMNProfiles);
        setupEnableRuntimeTypeCheckOption(initMethod, enableRuntimeTypeCheckOption);
        setupModelPathsFile(initMethod, applicationCanonicalName);
        return compilationUnit;
    }

    static void setupExecIdSupplierVariable(MethodCallExpr initMethod, boolean useTracing) {
        Expression execIdSupplier = useTracing ? newObject(DmnExecutionIdSupplier.class) : new NullLiteralExpr();
        initMethod.addArgument(execIdSupplier);
    }

    static void setupDecisionModelTransformerVariable(MethodCallExpr initMethod, boolean useMonitoring) {
        Expression decisionModelTransformerExpr = useMonitoring ? newObject(MONITORED_DECISIONMODEL_TRANSFORMER) : new NullLiteralExpr();
        initMethod.addArgument(decisionModelTransformerExpr);
    }

    static void setupCustomDMNProfiles(MethodCallExpr initMethod, Set<DMNProfile> customDMNProfiles) {
        NodeList<Expression> customDMNProfileArguments = new NodeList<>();
        customDMNProfiles.stream()
                .map(profile -> profile.getClass().getCanonicalName())
                .map(CodegenUtils::newObject)
                .forEach(customDMNProfileArguments::add);

        MethodCallExpr setOfExpression = new MethodCallExpr();
        SimpleName setName = new SimpleName(Set.class.getName());
        setOfExpression.setScope(new NameExpr(setName));
        setOfExpression.setName(new SimpleName("of"));
        setOfExpression.setArguments(customDMNProfileArguments);
        initMethod.addArgument(setOfExpression);
    }

    static void setupEnableRuntimeTypeCheckOption(MethodCallExpr initMethod, boolean enableRuntimeTypeCheckOption) {
        Expression toAdd = new BooleanLiteralExpr(enableRuntimeTypeCheckOption);
        initMethod.addArgument(toAdd);
    }

    static void setupModelPathsFile(MethodCallExpr initMethod, String applicationCanonicalName) {
        ClassOrInterfaceType applicationClass = StaticJavaParser.parseClassOrInterfaceType(applicationCanonicalName);
        FieldAccessExpr fieldAccessExpr = new FieldAccessExpr(applicationClass.getNameAsExpression(), "class");
        MethodCallExpr methodCallExpr = new MethodCallExpr(fieldAccessExpr, "getResource")
                .addArgument(new StringLiteralExpr("/" + DMN_MODEL_PATHS_FILE));
        initMethod.addArgument(methodCallExpr);
    }

    public List<String> getClassesForManualReflection() {
        return classesForManualReflection;
    }
}
