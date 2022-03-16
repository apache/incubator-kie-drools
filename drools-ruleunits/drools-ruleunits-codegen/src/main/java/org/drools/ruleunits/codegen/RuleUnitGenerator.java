/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.drools.ruleunits.codegen;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;
import org.drools.modelcompiler.builder.QueryModel;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitConfig;
import org.drools.ruleunits.api.conf.ClockType;
import org.drools.ruleunits.api.conf.EventProcessingType;
import org.drools.ruleunits.codegen.context.KogitoBuildContext;
import org.drools.ruleunits.codegen.context.impl.JavaKogitoBuildContext;
import org.drools.ruleunits.codegen.template.InvalidTemplateException;
import org.drools.ruleunits.codegen.template.TemplatedGenerator;
import org.drools.ruleunits.impl.AbstractRuleUnitDescription;
import org.drools.ruleunits.impl.GeneratedRuleUnitDescription;
import org.drools.ruleunits.impl.factory.AbstractRuleUnit;
import org.kie.internal.ruleunit.RuleUnitDescription;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.ast.NodeList.nodeList;
import static java.util.stream.Collectors.toList;
import static org.drools.ruleunits.codegen.RuleCodegen.RULE_TYPE;
import static org.drools.ruleunits.codegen.RuleCodegen.TEMPLATE_RULE_FOLDER;

public class RuleUnitGenerator implements RuleFileGenerator {

    public static final String KOGITO_USE_LEGACY_SESSION = "kogito.uselegacysession";

    private final RuleUnitDescription ruleUnit;
    private final String ruleUnitPackageName;
    private final String typeName;
    private final String generatedSourceFile;
    private final TemplatedGenerator generator;
    private final KogitoBuildContext context;
    private final String targetCanonicalName;
    private final String targetTypeName;
    private RuleUnitConfig config;
    private List<QueryGenerator> queryGenerators;

    public RuleUnitGenerator(KogitoBuildContext context, RuleUnitDescription ruleUnit, String generatedSourceFile) {
        this.ruleUnit = ruleUnit;
        this.ruleUnitPackageName = ruleUnit.getPackageName();
        this.typeName = ruleUnit.getSimpleName();
        this.generatedSourceFile = generatedSourceFile;
        this.context = context;
        this.targetTypeName = typeName + "RuleUnit";
        this.targetCanonicalName = ruleUnitPackageName + "." + targetTypeName;
        // merge config from the descriptor with configs from application.conf
        // application.conf overrides any other config
        this.config = ((AbstractRuleUnitDescription) ruleUnit).getConfig();
        this.generator = TemplatedGenerator.builder()
                .withPackageName(ruleUnitPackageName)
                .withTemplateBasePath(TEMPLATE_RULE_FOLDER)
                .withTargetTypeName(targetTypeName)
                .withFallbackContext(JavaKogitoBuildContext.CONTEXT_NAME)
                .build(context, "RuleUnitOn" + (useLegacySession(context) ? "KieSession" : "ReteEvaluator"));
    }

    static boolean useLegacySession(KogitoBuildContext context) {
        return "true".equalsIgnoreCase(context.getApplicationProperty(KOGITO_USE_LEGACY_SESSION).orElse("true"));
    }

    // override config for this rule unit with the given values
    public RuleUnitGenerator mergeConfig(RuleUnitConfig ruleUnitConfig) {
        this.config = config.merged(ruleUnitConfig);
        return this;
    }

    public RuleUnitInstanceGenerator instance(RuleUnitHelper ruleUnitHelper) {
        return new RuleUnitInstanceGenerator(context, ruleUnit, ruleUnitHelper, queries().stream().map(QueryGenerator::className).collect(Collectors.toList()));
    }

    public Collection<QueryGenerator> queries() {
        return this.queryGenerators;
    }

    @Override
    public String generatedFilePath() {
        return generator.generatedFilePath();
    }

    public String targetCanonicalName() {
        return targetCanonicalName;
    }

    public String typeName() {
        return typeName;
    }

    @Override
    public GeneratedFile generate() {
        return new GeneratedFile(RULE_TYPE,
                generatedFilePath(),
                compilationUnit().toString());
    }

    public Optional<RuleUnitPojoGenerator> pojo(RuleUnitHelper ruleUnitHelper) {
        if (ruleUnit instanceof GeneratedRuleUnitDescription) {
            return Optional.of(new RuleUnitPojoGenerator((GeneratedRuleUnitDescription) ruleUnit, ruleUnitHelper));
        } else {
            return Optional.empty();
        }
    }

    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit = generator.compilationUnitOrThrow();

        classDeclaration(
                compilationUnit.findFirst(ClassOrInterfaceDeclaration.class)
                        .orElseThrow(() -> new InvalidTemplateException(
                                generator,
                                "Compilation unit doesn't contain a class or interface declaration!")));
        return compilationUnit;
    }

    public static ClassOrInterfaceType ruleUnitType(String canonicalName) {
        return new ClassOrInterfaceType(null, RuleUnit.class.getCanonicalName())
                .setTypeArguments(new ClassOrInterfaceType(null, canonicalName));
    }

    public static ClassOrInterfaceType abstractRuleUnitType(String canonicalName) {
        return new ClassOrInterfaceType(null, AbstractRuleUnit.class.getCanonicalName())
                .setTypeArguments(new ClassOrInterfaceType(null, canonicalName));
    }

    public void classDeclaration(ClassOrInterfaceDeclaration cls) {
        cls.setName(targetTypeName)
                .setModifiers(Modifier.Keyword.PUBLIC)
                .getExtendedTypes().get(0).setTypeArguments(nodeList(new ClassOrInterfaceType(null, typeName)));

        if (context.hasDI()) {

//            context.getDependencyInjectionAnnotator().withSingletonComponent(cls);
//            cls.findFirst(ConstructorDeclaration.class, c -> !c.getParameters().isEmpty()) // non-empty constructor
//                    .ifPresent(context.getDependencyInjectionAnnotator()::withInjection);
        }

        String ruleUnitInstanceFQCN = RuleUnitInstanceGenerator.qualifiedName(ruleUnitPackageName, typeName);
        cls.findAll(ConstructorDeclaration.class).forEach(this::setClassName);
        cls.findAll(ObjectCreationExpr.class, o -> o.getType().getNameAsString().equals("$InstanceName$"))
                .forEach(o -> o.setType(ruleUnitInstanceFQCN));
        cls.findAll(ObjectCreationExpr.class, o -> o.getType().getNameAsString().equals("$Application$"))
                .forEach(o -> o.setType(context.getPackageName() + ".Application"));
        cls.findAll(ObjectCreationExpr.class, o -> o.getType().getNameAsString().equals("$RuleModelName$"))
                .forEach(o -> o.setType(ruleUnitPackageName + "." + generatedSourceFile + "_" + typeName));
        cls.findAll(MethodDeclaration.class, m -> m.getType().asString().equals("$InstanceName$"))
                .stream()
                .map(m -> m.setType(ruleUnitInstanceFQCN))
                .flatMap(m -> m.getParameters().stream())
                .filter(p -> p.getType().asString().equals("$ModelName$"))
                .forEach(o -> o.setType(typeName));

        cls.findAll(ClassExpr.class, e -> e.getType().toString().equals("$ModelName$"))
                .forEach(ne -> ne.setType(typeName));

        cls.findAll(TypeParameter.class)
                .forEach(tp -> tp.setName(typeName));

        cls.findFirst(NameExpr.class, e -> e.getNameAsString().equals("$SessionPoolSize$"))
                .ifPresent(e -> e.replace(new IntegerLiteralExpr(config.getDefaultedSessionPool().orElse(-1))));

        cls.findFirst(NameExpr.class, e -> e.getNameAsString().equals("$EventProcessingMode$"))
                .ifPresent(e -> e.replace(eventProcessingConfigExpression(config.getDefaultedEventProcessingType())));

        cls.findFirst(NameExpr.class, e -> e.getNameAsString().equals("$ClockType$"))
                .ifPresent(e -> e.replace(clockConfigExpression(config.getDefaultedClockType())));
    }

    private Expression eventProcessingConfigExpression(EventProcessingType eventProcessingType) {
        Expression replacement =
                (eventProcessingType == EventProcessingType.STREAM) ? parseExpression("org.kie.api.conf.EventProcessingOption.STREAM")
                        : parseExpression("org.kie.api.conf.EventProcessingOption.CLOUD");
        return replacement;
    }

    private Expression clockConfigExpression(ClockType clockType) {
        Expression replacement =
                (clockType == ClockType.PSEUDO) ? parseExpression("org.drools.core.ClockType.PSEUDO_CLOCK") : parseExpression("org.drools.core.ClockType.REALTIME_CLOCK");
        return replacement;
    }

    private void setClassName(ConstructorDeclaration constructorDeclaration) {
        constructorDeclaration.setName(targetTypeName);
    }

    public RuleUnitGenerator withQueries(Collection<QueryModel> queries) {
        this.queryGenerators = queries.stream()
                .filter(query -> !query.hasParameters())
                .map(query -> new QueryGenerator(context, ruleUnit, query))
                .collect(toList());

        return this;
    }

    public RuleUnitDescription getRuleUnitDescription() {
        return ruleUnit;
    }
}
