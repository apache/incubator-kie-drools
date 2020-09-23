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

package org.kie.kogito.codegen.rules;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalInt;

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
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.FileGenerator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.conf.ClockType;
import org.kie.kogito.conf.EventProcessingType;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitConfig;
import org.kie.kogito.rules.units.GeneratedRuleUnitDescription;
import org.kie.kogito.rules.units.impl.AbstractRuleUnit;

import static com.github.javaparser.StaticJavaParser.parse;
import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.ast.NodeList.nodeList;
import static java.util.stream.Collectors.toList;
import static org.kie.kogito.codegen.metadata.ImageMetaData.LABEL_PREFIX;

public class RuleUnitGenerator implements FileGenerator {

    public static final String TEMPLATE = "/class-templates/rules/RuleUnitTemplate.java";

    private final RuleUnitDescription ruleUnit;
    private final String packageName;
    private final String typeName;
    private final String generatedSourceFile;
    private final String generatedFilePath;
    private final String targetCanonicalName;
    private RuleUnitConfig config;
    private String targetTypeName;
    private DependencyInjectionAnnotator annotator;
    private Collection<QueryModel> queries;
    private String applicationPackageName;
    private AddonsConfig addonsConfig = AddonsConfig.DEFAULT;

    public RuleUnitGenerator(RuleUnitDescription ruleUnit, String generatedSourceFile) {
        this.ruleUnit = ruleUnit;
        this.packageName = ruleUnit.getPackageName();
        this.typeName = ruleUnit.getSimpleName();
        this.generatedSourceFile = generatedSourceFile;
        this.targetTypeName = typeName + "RuleUnit";
        this.targetCanonicalName = packageName + "." + targetTypeName;
        this.generatedFilePath = targetCanonicalName.replace('.', '/') + ".java";
        this.applicationPackageName = ApplicationGenerator.DEFAULT_PACKAGE_NAME;
        // merge config from the descriptor with configs from application.conf
        // application.conf overrides any other config
        this.config = ruleUnit.getConfig();
    }

    // override config for this rule unit with the given values
    public RuleUnitGenerator mergeConfig(RuleUnitConfig ruleUnitConfig) {
        this.config = config.merged(ruleUnitConfig);
        return this;
    }

    public RuleUnitInstanceGenerator instance(RuleUnitHelper ruleUnitHelper, List<String> queryClasses) {
        return new RuleUnitInstanceGenerator(ruleUnit, ruleUnitHelper, queryClasses);
    }

    public List<QueryEndpointGenerator> queries() {
        return queries.stream()
                .filter(query -> !query.hasParameters())
                .map(query -> new QueryEndpointGenerator(ruleUnit, query, annotator, addonsConfig))
                .collect(toList());
    }

    @Override
    public String generatedFilePath() {
        return generatedFilePath;
    }

    public String targetCanonicalName() {
        return targetCanonicalName;
    }

    public String targetTypeName() {
        return targetTypeName;
    }

    public String typeName() {
        return typeName;
    }

    public String label() {
        return LABEL_PREFIX + typeName();
    }

    @Override
    public String generate() {
        return compilationUnit().toString();
    }

    public Optional<RuleUnitPojoGenerator> pojo(RuleUnitHelper ruleUnitHelper) {
        if (ruleUnit instanceof GeneratedRuleUnitDescription) {
            return Optional.of(new RuleUnitPojoGenerator((GeneratedRuleUnitDescription) ruleUnit, ruleUnitHelper));
        } else {
            return Optional.empty();
        }
    }

    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit = parse(getClass().getResourceAsStream(TEMPLATE));
        compilationUnit.setPackageDeclaration(packageName);

        classDeclaration(
                compilationUnit.findFirst(ClassOrInterfaceDeclaration.class)
                        .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!")));
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

        if (annotator != null) {
            annotator.withSingletonComponent(cls);
            cls.findFirst(ConstructorDeclaration.class, c -> !c.getParameters().isEmpty()) // non-empty constructor
                    .ifPresent(annotator::withInjection);
        }

        String ruleUnitInstanceFQCN = RuleUnitInstanceGenerator.qualifiedName(packageName, typeName);
        cls.findAll(ConstructorDeclaration.class).forEach(this::setClassName);
        cls.findAll(ObjectCreationExpr.class, o -> o.getType().getNameAsString().equals("$InstanceName$"))
                .forEach(o -> o.setType(ruleUnitInstanceFQCN));
        cls.findAll(ObjectCreationExpr.class, o -> o.getType().getNameAsString().equals("$Application$"))
                .forEach(o -> o.setType(applicationPackageName + ".Application"));
        cls.findAll(ObjectCreationExpr.class, o -> o.getType().getNameAsString().equals("$RuleModelName$"))
                .forEach(o -> o.setType(packageName + "." + generatedSourceFile + "_" + typeName));
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
                (eventProcessingType == EventProcessingType.STREAM) ?
                        parseExpression("org.kie.api.conf.EventProcessingOption.STREAM") :
                        parseExpression("org.kie.api.conf.EventProcessingOption.CLOUD");
        return replacement;
    }

    private Expression clockConfigExpression(ClockType clockType) {
        Expression replacement =
                (clockType == ClockType.PSEUDO) ?
                        parseExpression("org.drools.core.ClockType.PSEUDO_CLOCK") :
                        parseExpression("org.drools.core.ClockType.REALTIME_CLOCK");
        return replacement;
    }

    private void setClassName(ConstructorDeclaration constructorDeclaration) {
        constructorDeclaration.setName(targetTypeName);
    }

    public RuleUnitGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        return this;
    }

    public RuleUnitGenerator withQueries(Collection<QueryModel> queries) {
        this.queries = queries;
        return this;
    }

    public RuleUnitGenerator withAddons(AddonsConfig addonsConfig) {
        this.addonsConfig = addonsConfig;
        return this;
    }

    public RuleUnitDescription getRuleUnitDescription() {
        return ruleUnit;
    }

    public void setApplicationPackageName(String packageName) {
        if (packageName != null) {
            this.applicationPackageName = packageName;
        }
    }
}
