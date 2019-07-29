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

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.modelcompiler.builder.QueryModel;
import org.kie.kogito.codegen.FileGenerator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.impl.AbstractRuleUnit;

import static java.util.stream.Collectors.toList;

import static com.github.javaparser.ast.NodeList.nodeList;

public class RuleUnitSourceClass implements FileGenerator {

    private final Class<?> ruleUnit;
    private final String packageName;
    private final String typeName;
    private final String generatedSourceFile;
    private final String generatedFilePath;
    private final String canonicalName;
    private final String targetCanonicalName;
    private String targetTypeName;
    private DependencyInjectionAnnotator annotator;
    private Collection<QueryModel> queries;

    public RuleUnitSourceClass(Class<?> ruleUnit, String generatedSourceFile) {
        this.ruleUnit = ruleUnit;
        this.packageName = ruleUnit.getPackage().getName();
        this.typeName = ruleUnit.getSimpleName();
        this.generatedSourceFile = generatedSourceFile;
        this.canonicalName = packageName + "." + typeName;
        this.targetTypeName = typeName + "RuleUnit";
        this.targetCanonicalName = packageName + "." + targetTypeName;
        this.generatedFilePath = targetCanonicalName.replace('.', '/') + ".java";
    }

    public RuleUnitInstanceSourceClass instance(ClassLoader classLoader) {
        return new RuleUnitInstanceSourceClass(packageName, typeName, classLoader);
    }

    public List<QueryEndpointSourceClass> queries() {
        return queries.stream()
                .filter( query -> !query.hasParameters() )
                .map( query -> new QueryEndpointSourceClass( ruleUnit, query, annotator ) )
                .collect( toList() );
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

    @Override
    public String generate() {
        return compilationUnit().toString();
    }

    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit = new CompilationUnit(packageName);
        compilationUnit.getTypes().add(classDeclaration());
        return compilationUnit;
    }

    private MethodDeclaration createInstanceMethod(String ruleUnitInstanceFQCN) {
        MethodDeclaration methodDeclaration = new MethodDeclaration();

        ReturnStmt returnStmt = new ReturnStmt(
                new ObjectCreationExpr()
                        .setType(ruleUnitInstanceFQCN)
                        .setArguments(nodeList(
                                new ThisExpr(),
                                new NameExpr("value"),
                                newKieSession())));

        methodDeclaration.setName("createInstance")
                .addModifier(Modifier.Keyword.PUBLIC)
                .addParameter(canonicalName, "value")
                .setType(ruleUnitInstanceFQCN)
                .setBody(new BlockStmt()
                                 .addStatement(returnStmt));

        return methodDeclaration;
    }

    private MethodCallExpr newKieSession() {
        MethodCallExpr createKieBaseFromModel = createKieBaseFromModel();
        return new MethodCallExpr(createKieBaseFromModel, "newKieSession");
    }

    private MethodCallExpr createKieBaseFromModel() {
        return new MethodCallExpr(
                new NameExpr("org.drools.modelcompiler.builder.KieBaseBuilder"),
                "createKieBaseFromModel").addArgument(
                new ObjectCreationExpr().setType("org.drools.model.impl.UnitModelImpl")
                        .addArgument(new ObjectCreationExpr().setType(generatedSourceFile))
                        .addArgument(new StringLiteralExpr(canonicalName)));
    }

    public static ClassOrInterfaceType ruleUnitType(String canonicalName) {
        return new ClassOrInterfaceType(null, RuleUnit.class.getCanonicalName())
                .setTypeArguments(new ClassOrInterfaceType(null, canonicalName));
    }

    public static ClassOrInterfaceType abstractRuleUnitType(String canonicalName) {
        return new ClassOrInterfaceType(null, AbstractRuleUnit.class.getCanonicalName())
                .setTypeArguments(new ClassOrInterfaceType(null, canonicalName));
    }

    public ClassOrInterfaceDeclaration classDeclaration() {
        ClassOrInterfaceDeclaration cls = new ClassOrInterfaceDeclaration()
                .setName(targetTypeName)
                .setModifiers(Modifier.Keyword.PUBLIC);

        if (annotator != null) {
            annotator.withSingletonComponent(cls);
        }

        String ruleUnitInstanceFQCN = RuleUnitInstanceSourceClass.qualifiedName(packageName, typeName);

        MethodDeclaration methodDeclaration = createInstanceMethod(ruleUnitInstanceFQCN);
        cls.addExtendedType(abstractRuleUnitType(canonicalName))
                .addMember(methodDeclaration);

        return cls;
    }

    public RuleUnitSourceClass withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        return this;
    }

    public RuleUnitSourceClass withQueries( Collection<QueryModel> queries ) {
        this.queries = queries;
        return this;
    }
}
