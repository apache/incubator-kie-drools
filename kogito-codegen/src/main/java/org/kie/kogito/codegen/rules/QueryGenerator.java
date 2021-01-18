/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.util.Map;
import java.util.NoSuchElementException;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.modelcompiler.builder.QueryModel;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.FileGenerator;
import org.kie.kogito.codegen.TemplatedGenerator;
import org.kie.kogito.codegen.context.JavaKogitoBuildContext;
import org.kie.kogito.codegen.context.KogitoBuildContext;

import static org.drools.core.util.StringUtils.ucFirst;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.classToReferenceType;
import static org.kie.kogito.codegen.rules.IncrementalRuleCodegen.TEMPLATE_RULE_FOLDER;
import static org.kie.kogito.codegen.rules.QueryEndpointGenerator.setGeneric;

public class QueryGenerator implements FileGenerator {

    private final TemplatedGenerator generator;
    private final RuleUnitDescription ruleUnit;
    private final QueryModel query;

    private final String targetClassName;

    public QueryGenerator(KogitoBuildContext context, RuleUnitDescription ruleUnit, QueryModel query, String name ) {
        this.ruleUnit = ruleUnit;
        this.query = query;

        this.targetClassName = ruleUnit.getSimpleName() + "Query" + name;
        this.generator = TemplatedGenerator.builder()
                .withPackageName(query.getNamespace())
                .withFallbackContext(JavaKogitoBuildContext.CONTEXT_NAME)
                .withTemplateBasePath(TEMPLATE_RULE_FOLDER)
                .withTargetTypeName(targetClassName)
                .build(context, "RuleUnitQuery");

    }

    public String getQueryClassName() {
        return generator.targetTypeName();
    }

    @Override
    public String generatedFilePath() {
        return generator.generatedFilePath();
    }

    @Override
    public String generate() {
        CompilationUnit cu = generator.compilationUnitOrThrow();

        ClassOrInterfaceDeclaration clazz = cu
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));
        clazz.setName(targetClassName);

        cu.findAll(StringLiteralExpr.class).forEach(this::interpolateStrings);

        FieldDeclaration ruleUnitDeclaration = clazz
                .getFieldByName("instance")
                .orElseThrow(() -> new NoSuchElementException("ClassOrInterfaceDeclaration doesn't contain a field named ruleUnit!"));
        setGeneric(ruleUnitDeclaration.getElementType(), ruleUnit);

        String returnType = getReturnType(clazz);
        setGeneric( clazz.getImplementedTypes( 0 ).getTypeArguments().get().get(0), returnType );
        generateConstructors(clazz);
        generateQueryMethod(cu, clazz, returnType);
        clazz.getMembers().sort(new BodyDeclarationComparator());
        return cu.toString();
    }

    private void generateConstructors(ClassOrInterfaceDeclaration clazz) {
        for (ConstructorDeclaration c : clazz.getConstructors()) {
            c.setName(targetClassName);
            if (!c.getParameters().isEmpty()) {
                setGeneric(c.getParameter(0).getType(), ruleUnit);
            }
        }
    }

    private void generateQueryMethod(CompilationUnit cu, ClassOrInterfaceDeclaration clazz, String returnType) {
        MethodDeclaration queryMethod = clazz.getMethodsByName("execute").get(0);
        setGeneric(queryMethod.getType(), returnType);
    }

    private String getReturnType(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration toResultMethod = clazz.getMethodsByName("toResult").get(0);
        String returnType;
        if (query.getBindings().size() == 1) {
            Map.Entry<String, Class<?>> binding = query.getBindings().entrySet().iterator().next();
            String name = binding.getKey();
            returnType = binding.getValue().getCanonicalName();

            Statement statement = toResultMethod
                    .getBody()
                    .orElseThrow(() -> new NoSuchElementException("A method declaration doesn't contain a body!"))
                    .getStatement(0);

            statement.findFirst(CastExpr.class).orElseThrow(() -> new NoSuchElementException("CastExpr not found in template.")).setType(returnType);
            statement.findFirst(StringLiteralExpr.class).orElseThrow(() -> new NoSuchElementException("StringLiteralExpr not found in template.")).setString(name);
        } else {
            returnType = targetClassName + ".Result";
            generateResultClass(clazz, toResultMethod);
        }

        toResultMethod.setType(returnType);
        return returnType;
    }

    private void generateResultClass(ClassOrInterfaceDeclaration clazz, MethodDeclaration toResultMethod) {
        ClassOrInterfaceDeclaration resultClass = new ClassOrInterfaceDeclaration(new NodeList<Modifier>(Modifier.publicModifier(), Modifier.staticModifier()), false, "Result");
        clazz.addMember(resultClass);

        ConstructorDeclaration constructor = resultClass.addConstructor(Modifier.Keyword.PUBLIC);
        BlockStmt constructorBody = constructor.createBody();

        ObjectCreationExpr resultCreation = new ObjectCreationExpr();
        resultCreation.setType("Result");
        BlockStmt resultMethodBody = toResultMethod.createBody();
        resultMethodBody.addStatement(new ReturnStmt(resultCreation));

        query.getBindings().forEach((name, type) -> {
            resultClass.addField(type, name, Modifier.Keyword.PRIVATE, Modifier.Keyword.FINAL);

            MethodDeclaration getterMethod = resultClass.addMethod("get" + ucFirst(name), Modifier.Keyword.PUBLIC);
            getterMethod.setType(type);
            BlockStmt body = getterMethod.createBody();
            body.addStatement(new ReturnStmt(new NameExpr(name)));

            constructor.addAndGetParameter(type, name);
            constructorBody.addStatement(new AssignExpr(new NameExpr("this." + name), new NameExpr(name), AssignExpr.Operator.ASSIGN));

            MethodCallExpr callExpr = new MethodCallExpr(new NameExpr("tuple"), "get");
            callExpr.addArgument(new StringLiteralExpr(name));
            resultCreation.addArgument(new CastExpr(classToReferenceType(type), callExpr));
        });
    }

    private void interpolateStrings(StringLiteralExpr vv) {
        String interpolated = vv.getValue().replace("$queryName$", query.getName());
        vv.setString(interpolated);
    }
}
