/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model.codegen.execmodel;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
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
import org.drools.codegen.common.context.JavaDroolsModelBuildContext;
import org.drools.model.codegen.project.template.TemplatedGenerator;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.internal.ruleunit.RuleUnitVariable;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static org.drools.model.codegen.execmodel.JavaParserCompiler.getPrettyPrinter;

public class RuleUnitWriter {

    private static final String TEMPLATE_RULE_UNITS_FOLDER = "/class-templates/ruleunits/";

    private final TemplatedGenerator.Builder templateBuilder;

    private final RuleUnitDescription ruleUnitDescr;
    private final PackageModel.RuleSourceResult ruleSourceResult;
    private final PackageModel pkgModel;

    public RuleUnitWriter(PackageModel pkgModel, PackageModel.RuleSourceResult ruleSourceResult, RuleUnitDescription ruleUnitDescr) {
        this.pkgModel = pkgModel;
        this.ruleSourceResult = ruleSourceResult;
        this.ruleUnitDescr = ruleUnitDescr;
        this.templateBuilder = TemplatedGenerator.builder()
                .withTemplateBasePath(TEMPLATE_RULE_UNITS_FOLDER)
                .withPackageName(pkgModel.getName())
                .withFallbackContext(JavaDroolsModelBuildContext.CONTEXT_NAME);
    }

    public String getUnitName() {
        return pkgModel.getPathName() + "/" + getRuleUnitSimpleClassName() + ".java";
    }

    public String getInstanceName() {
        return pkgModel.getPathName() + "/" + getRuleUnitInstanceSimpleClassName() + ".java";
    }

    public String getRuleUnitClassName() {
        return pkgModel.getName() + "." + getRuleUnitSimpleClassName();
    }

    private String getRuleUnitSimpleClassName() {
        return ruleUnitDescr.getSimpleName() + "RuleUnit";
    }

    private String getRuleUnitInstanceSimpleClassName() {
        return ruleUnitDescr.getSimpleName() + "RuleUnitInstance";
    }

    public String getUnitSource() {
        CompilationUnit cu = templateBuilder.build(pkgModel.getContext(), "RuleUnit").compilationUnitOrThrow("Could not create CompilationUnit");

        ClassOrInterfaceDeclaration parsedClass = cu
                .getClassByName("CLASS_NAME")
                .orElseThrow(RuntimeException::new);

        cu.setPackageDeclaration(pkgModel.getName());

        parsedClass.setName(getRuleUnitSimpleClassName());
        parsedClass.findAll(ConstructorDeclaration.class)
                .forEach(c -> c.setName(getRuleUnitSimpleClassName()));
        parsedClass.findAll(ClassOrInterfaceType.class, c -> "CLASS_NAME".equals(c.asString()))
                .forEach(c -> c.setName(getRuleUnitSimpleClassName()));
        parsedClass.findAll(ClassOrInterfaceType.class, c -> "RULE_UNIT_CLASS".equals(c.asString()))
                .forEach(c -> c.setName(ruleUnitDescr.getRuleUnitName()));

        parsedClass.findAll(ClassOrInterfaceType.class, c -> "RULE_UNIT_INSTANCE_CLASS".equals(c.asString()))
                .forEach(c -> c.setName(getRuleUnitInstanceSimpleClassName()));
        parsedClass.findAll(ObjectCreationExpr.class, c -> "RULE_UNIT_INSTANCE_CLASS".equals(c.getTypeAsString()))
                .forEach(c -> c.setType(getRuleUnitInstanceSimpleClassName()));

        parsedClass.findAll(ObjectCreationExpr.class, c -> "RULE_UNIT_MODEL".equals(c.getTypeAsString()))
                .forEach(c -> c.setType(ruleSourceResult.getModelsByUnit().get(ruleUnitDescr.getRuleUnitName())));

        parsedClass.findFirst(NameExpr.class, e -> e.getNameAsString().equals("$ClockType$"))
                .ifPresent(e -> e.replace(clockConfigExpression(ruleUnitDescr.getClockType())));

        return getPrettyPrinter().print(cu);
    }

    public String getInstanceSource() {
        CompilationUnit cu;
        try {
            cu = StaticJavaParser.parseResource("RuleUnitInstanceTemplate.java");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        ClassOrInterfaceDeclaration parsedClass = cu
                        .getClassByName("CLASS_NAME")
                        .orElseThrow(RuntimeException::new);

        cu.setPackageDeclaration(pkgModel.getName());

        parsedClass.setName(getRuleUnitInstanceSimpleClassName());
        parsedClass.findAll(ConstructorDeclaration.class)
                .forEach(c -> c.setName(getRuleUnitInstanceSimpleClassName()));
        parsedClass.findAll(ClassOrInterfaceType.class, c -> "RULE_UNIT_CLASS".equals(c.asString()))
                .forEach(c -> c.setName(ruleUnitDescr.getRuleUnitName()));

        MethodDeclaration bindMethod = parsedClass.findAll(MethodDeclaration.class, c -> "bind".equals(c.getNameAsString())).get(0);
        BlockStmt methodBlock = new BlockStmt();
        bindMethod.setBody(methodBlock);

        for (RuleUnitVariable m : ruleUnitDescr.getUnitVarDeclarations()) {
            String methodName = m.getter();
            String propertyName = m.getName();

            if (m.isDataSource() && m.setter() != null) { // if writable and DataSource is null create and set a new one
                Expression nullCheck = new BinaryExpr(new MethodCallExpr(new NameExpr("ruleUnit"), methodName), new NullLiteralExpr(), BinaryExpr.Operator.EQUALS);
                Expression createDataSourceExpr = createDataSourceMethodCallExpr(m.getBoxedVarType());
                Expression dataSourceSetter = new MethodCallExpr(new NameExpr("ruleUnit"), m.setter(), new NodeList<>(createDataSourceExpr));
                methodBlock.addStatement(new IfStmt(nullCheck, new BlockStmt().addStatement(dataSourceSetter), null));
            }

            if (m.isDataSource()) {

                //  ruleUnit.$method())
                Expression fieldAccessor =
                        new MethodCallExpr(new NameExpr("ruleUnit"), methodName);

                // .subscribe( new EntryPointDataProcessor(runtime.getEntryPoint()) )

                MethodCallExpr drainInto = new MethodCallExpr(fieldAccessor, "subscribe")
                        .addArgument(new ObjectCreationExpr(null, StaticJavaParser.parseClassOrInterfaceType("org.drools.ruleunits.impl.EntryPointDataProcessor"), NodeList.nodeList(
                                new MethodCallExpr(
                                        new NameExpr("evaluator"), "getEntryPoint",
                                        NodeList.nodeList(new StringLiteralExpr(propertyName))))));

                methodBlock.addStatement(drainInto);
            }

            MethodCallExpr setGlobalCall = new MethodCallExpr(new NameExpr("evaluator"), "setGlobal");
            setGlobalCall.addArgument(new StringLiteralExpr(propertyName));
            setGlobalCall.addArgument(new MethodCallExpr(new NameExpr("ruleUnit"), methodName));
            methodBlock.addStatement(setGlobalCall);
        }

        return getPrettyPrinter().print(cu);
    }

    private MethodCallExpr createDataSourceMethodCallExpr(Class<?> dsClass) {
        MethodCallExpr methodCallExpr = new MethodCallExpr();
        methodCallExpr.setScope(new NameExpr("org.drools.ruleunits.api.DataSource"));

        if (dsClass.getSimpleName().equals("DataStream")) {
            return methodCallExpr
                    .setName("createBufferedStream")
                    .addArgument("16");
        }
        if (dsClass.getSimpleName().equals("DataStore")) {
            return methodCallExpr
                    .setName("createStore");
        }
        if (dsClass.getSimpleName().equals("SingletonStore")) {
            return methodCallExpr
                    .setName("createSingleton");
        }
        throw new IllegalArgumentException("Unknown data source type " + dsClass.getCanonicalName());
    }

    private Expression clockConfigExpression(ClockTypeOption clockType) {
        Expression replacement =
                (clockType == ClockTypeOption.PSEUDO) ? parseExpression("org.drools.core.ClockType.PSEUDO_CLOCK") : parseExpression("org.drools.core.ClockType.REALTIME_CLOCK");
        return replacement;
    }
}
