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
package org.drools.ruleunits.codegen;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.common.ReteEvaluator;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.conf.DefaultEntryPoint;
import org.drools.ruleunits.api.conf.EntryPoint;
import org.drools.ruleunits.codegen.context.KogitoBuildContext;
import org.drools.ruleunits.impl.EntryPointDataProcessor;
import org.drools.ruleunits.impl.KieSessionBasedRuleUnitInstance;
import org.drools.ruleunits.impl.ReteEvaluatorBasedRuleUnitInstance;
import org.kie.api.runtime.KieSession;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.internal.ruleunit.RuleUnitVariable;

import java.lang.reflect.Field;
import java.util.List;

import static org.drools.ruleunits.codegen.RuleCodegen.RULE_TYPE;
import static org.drools.ruleunits.codegen.RuleUnitGenerator.useLegacySession;

public class RuleUnitInstanceGenerator implements RuleFileGenerator {

    private static String ENTRY_POINT_DEFAULT_NAME = "DEFAULT";

    private final String targetTypeName;
    private final String targetCanonicalName;
    private final String generatedFilePath;
    private final RuleUnitDescription ruleUnitDescription;
    private final RuleUnitHelper ruleUnitHelper;
    private final List<String> queryClasses;

    private final Class<?> unitInstanceAbstractClass;
    private final Class<?> unitEvaluatorClass;

    public static String qualifiedName(String packageName, String typeName) {
        return packageName + "." + typeName + "RuleUnitInstance";
    }

    public RuleUnitInstanceGenerator(KogitoBuildContext context, RuleUnitDescription ruleUnitDescription, RuleUnitHelper ruleUnitHelper, List<String> queryClasses) {
        this.ruleUnitDescription = ruleUnitDescription;
        this.targetTypeName = ruleUnitDescription.getSimpleName() + "RuleUnitInstance";
        this.targetCanonicalName = ruleUnitDescription.getPackageName() + "." + targetTypeName;
        this.generatedFilePath = targetCanonicalName.replace('.', '/') + ".java";
        this.ruleUnitHelper = ruleUnitHelper;
        this.queryClasses = queryClasses;

        if (useLegacySession(context)) {
            unitInstanceAbstractClass = KieSessionBasedRuleUnitInstance.class;
            unitEvaluatorClass = KieSession.class;
        } else {
            unitInstanceAbstractClass = ReteEvaluatorBasedRuleUnitInstance.class;
            unitEvaluatorClass = ReteEvaluator.class;
        }
    }

    @Override
    public String generatedFilePath() {
        return generatedFilePath;
    }

    @Override
    public GeneratedFile generate() {
        return new GeneratedFile(RULE_TYPE,
                generatedFilePath(),
                compilationUnit().toString());
    }

    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit = new CompilationUnit(ruleUnitDescription.getPackageName());
        compilationUnit.getTypes().add(classDeclaration());
        return compilationUnit;
    }

    private MethodDeclaration bindMethod() {
        MethodDeclaration methodDeclaration = new MethodDeclaration();

        BlockStmt methodBlock = new BlockStmt();
        methodDeclaration.setName("bind")
                .addAnnotation("Override")
                .addModifier(Modifier.Keyword.PROTECTED)
                .addParameter(unitEvaluatorClass.getCanonicalName(), "evaluator")
                .addParameter(ruleUnitDescription.getRuleUnitName(), "value")
                .setType(void.class)
                .setBody(methodBlock);

        try {

            for (RuleUnitVariable m : ruleUnitDescription.getUnitVarDeclarations()) {
                String methodName = m.getter();
                String propertyName = m.getName();

                if (m.isDataSource()) {

                    if (m.setter() != null) { // if writable and DataSource is null create and set a new one
                        Expression nullCheck = new BinaryExpr(new MethodCallExpr(new NameExpr("value"), methodName), new NullLiteralExpr(), BinaryExpr.Operator.EQUALS);
                        Expression createDataSourceExpr = new MethodCallExpr(new NameExpr(DataSource.class.getCanonicalName()), ruleUnitHelper.createDataSourceMethodName(m.getBoxedVarType()));
                        Expression dataSourceSetter = new MethodCallExpr(new NameExpr("value"), m.setter(), new NodeList<>(createDataSourceExpr));
                        methodBlock.addStatement(new IfStmt(nullCheck, new BlockStmt().addStatement(dataSourceSetter), null));
                    }

                    //  value.$method())
                    Expression fieldAccessor =
                            new MethodCallExpr(new NameExpr("value"), methodName);

                    // .subscribe( new EntryPointDataProcessor(runtime.getEntryPoint()) )

                    String entryPointName = getEntryPointName(ruleUnitDescription, propertyName);
                    MethodCallExpr drainInto = new MethodCallExpr(fieldAccessor, "subscribe")
                            .addArgument(new ObjectCreationExpr(null, StaticJavaParser.parseClassOrInterfaceType(EntryPointDataProcessor.class.getName()), NodeList.nodeList(
                                    new MethodCallExpr(
                                            new NameExpr("evaluator"), "getEntryPoint",
                                            NodeList.nodeList(new StringLiteralExpr(entryPointName))))));

                    methodBlock.addStatement(drainInto);
                }

                MethodCallExpr setGlobalCall = new MethodCallExpr(new NameExpr("evaluator"), "setGlobal");
                setGlobalCall.addArgument(new StringLiteralExpr(propertyName));
                setGlobalCall.addArgument(new MethodCallExpr(new NameExpr("value"), methodName));
                methodBlock.addStatement(setGlobalCall);
            }

        } catch (Exception e) {
            throw new Error(e);
        }

        return methodDeclaration;
    }

    private MethodDeclaration createQueryMethod() {
        MethodDeclaration methodDeclaration = new MethodDeclaration();

        BlockStmt methodBlock = new BlockStmt();
        methodDeclaration.setName("createRuleUnitQuery")
                .addAnnotation("Override")
                .addModifier(Modifier.Keyword.PROTECTED)
                .addTypeParameter("Q")
                .addParameter("Class<? extends org.drools.ruleunits.api.RuleUnitQuery<Q>>", "query")
                .setType("org.kie.kogito.rules.RuleUnitQuery<Q>")
                .setBody(methodBlock);

        String statement = "if (@@@.class.equals( query )) return (org.kie.kogito.rules.RuleUnitQuery<Q>) new @@@(this);";
        for (String queryClass : queryClasses) {
            methodBlock.addStatement(statement.replaceAll("@@@", queryClass));
        }
        methodBlock.addStatement("throw new IllegalArgumentException(\"Unknown query: \" + query.getCanonicalName());");

        return methodDeclaration;
    }

    private String getEntryPointName(RuleUnitDescription ruleUnitDescription, String propertyName) {
        Class<?> ruleUnitClass = ruleUnitDescription.getRuleUnitClass();
        if (ruleUnitClass == null) {
            return propertyName;
        }
        try {
            // fixme should transfer this config to RuleUnitVariable
            Field dataSourceField = ruleUnitClass.getDeclaredField(propertyName);
            if (dataSourceField.getAnnotation(DefaultEntryPoint.class) != null) {
                return ENTRY_POINT_DEFAULT_NAME;
            }
            EntryPoint epAnn = dataSourceField.getAnnotation(EntryPoint.class);
            if (epAnn != null) {
                return epAnn.value();
            }
        } catch (NoSuchFieldException e) {
        }
        return propertyName;
    }

    public ClassOrInterfaceDeclaration classDeclaration() {
        String canonicalName = ruleUnitDescription.getRuleUnitName();
        ClassOrInterfaceDeclaration classDecl = new ClassOrInterfaceDeclaration()
                .setName(targetTypeName)
                .addModifier(Modifier.Keyword.PUBLIC);
        classDecl
                .addExtendedType(
                        new ClassOrInterfaceType(null, unitInstanceAbstractClass.getCanonicalName())
                                .setTypeArguments(new ClassOrInterfaceType(null, canonicalName)))
                .addImplementedType(
                        new ClassOrInterfaceType(null, RuleUnitInstance.class.getCanonicalName())
                                .setTypeArguments(new ClassOrInterfaceType(null, canonicalName)))
                .addConstructor(Modifier.Keyword.PUBLIC)
                .addParameter(RuleUnitGenerator.ruleUnitType(canonicalName), "unit")
                .addParameter(canonicalName, "value")
                .addParameter(unitEvaluatorClass.getCanonicalName(), "evaluator")
                .setBody(new BlockStmt().addStatement(new MethodCallExpr(
                        "super",
                        new NameExpr("unit"),
                        new NameExpr("value"),
                        new NameExpr("evaluator"))));
        classDecl.addMember(bindMethod());
        if (!queryClasses.isEmpty()) {
            classDecl.addMember(createQueryMethod());
        }
        classDecl.getMembers().sort(new BodyDeclarationComparator());
        return classDecl;
    }
}
