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
package org.kie.kogito.codegen.rules;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.drools.drl.parser.DroolsError;
import org.drools.model.codegen.execmodel.QueryModel;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;
import org.kie.kogito.codegen.core.BodyDeclarationComparator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;

import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.classToReferenceType;
import static org.drools.util.StringUtils.ucFirst;
import static org.kie.kogito.codegen.rules.RuleCodegen.TEMPLATE_RULE_FOLDER;
import static org.kie.kogito.codegen.rules.RuleCodegenUtils.setGeneric;
import static org.kie.kogito.codegen.rules.RuleCodegenUtils.toCamelCase;

public class QueryGenerator implements RuleFileGenerator {

    private static final GeneratedFileType QUERY_TYPE = GeneratedFileType.of("QUERY", GeneratedFileType.Category.SOURCE, true, true);

    private final TemplatedGenerator generator;
    private final KogitoBuildContext context;
    private final RuleUnitDescription ruleUnit;
    private final QueryModel queryModel;

    private final String targetClassName;
    private final String name;

    public QueryGenerator(KogitoBuildContext context, RuleUnitDescription ruleUnit, QueryModel queryModel) {
        this.context = context;
        this.ruleUnit = ruleUnit;
        this.queryModel = queryModel;
        this.name = toCamelCase(queryModel.getName());

        this.targetClassName = ruleUnit.getSimpleName() + "Query" + name;
        this.generator = TemplatedGenerator.builder()
                .withPackageName(queryModel.getNamespace())
                .withFallbackContext(JavaKogitoBuildContext.CONTEXT_NAME)
                .withTemplateBasePath(TEMPLATE_RULE_FOLDER)
                .withTargetTypeName(targetClassName)
                .build(context, "RuleUnitQuery");
    }

    public String name() {
        return name;
    }

    public RuleUnitDescription ruleUnit() {
        return ruleUnit;
    }

    public QueryModel model() {
        return queryModel;
    }

    public KogitoBuildContext context() {
        return context;
    }

    public String className() {
        return generator.targetTypeName();
    }

    @Override
    public boolean validate() {
        return !queryModel.getBindings().isEmpty();
    }

    @Override
    public DroolsError getError() {
        if (queryModel.getBindings().isEmpty()) {
            return new NoBindingQuery(queryModel);
        }
        return null;
    }

    @Override
    public String generatedFilePath() {
        return generator.generatedFilePath();
    }

    @Override
    public GeneratedFile generate() {
        CompilationUnit cu = generator.compilationUnitOrThrow();

        ClassOrInterfaceDeclaration clazz = cu
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));
        clazz.setName(targetClassName);

        cu.findAll(StringLiteralExpr.class).forEach(this::interpolateStrings);

        String returnType = getReturnType(clazz);
        generateQueryMethod(clazz, returnType);
        clazz.getMembers().sort(new BodyDeclarationComparator());

        return new GeneratedFile(QUERY_TYPE,
                generatedFilePath(),
                cu.toString());
    }

    private void generateQueryMethod(ClassOrInterfaceDeclaration clazz, String returnType) {
        MethodDeclaration queryMethod = clazz.getMethodsByName("execute").get(0);
        setGeneric(queryMethod.getType(), returnType);
        setGeneric(queryMethod.getParameter(0).getType(), ruleUnit);
        queryMethod.findAll(MethodReferenceExpr.class).forEach(mr -> mr.setScope(new NameExpr(targetClassName)));
    }

    private String getReturnType(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration toResultMethod = clazz.getMethodsByName("toResult").get(0);
        String returnType;
        if (queryModel.getBindings().size() == 1) {
            Map.Entry<String, Class<?>> binding = queryModel.getBindings().entrySet().iterator().next();
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
        ClassOrInterfaceDeclaration resultClass = new ClassOrInterfaceDeclaration(new NodeList<>(Modifier.publicModifier(), Modifier.staticModifier()), false, "Result");
        clazz.addMember(resultClass);

        ConstructorDeclaration constructor = resultClass.addConstructor(Modifier.Keyword.PUBLIC);
        BlockStmt constructorBody = constructor.createBody();

        ObjectCreationExpr resultCreation = new ObjectCreationExpr();
        resultCreation.setType("Result");
        BlockStmt resultMethodBody = toResultMethod.createBody();
        resultMethodBody.addStatement(new ReturnStmt(resultCreation));

        queryModel.getBindings().forEach((name, type) -> {
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
        String interpolated = vv.getValue().replace("$queryName$", queryModel.getName());
        vv.setString(interpolated);
    }

    public static class NoBindingQuery extends DroolsError {

        private static final int[] ERROR_LINES = new int[0];

        private final QueryModel query;

        public NoBindingQuery(QueryModel query) {
            this.query = query;
        }

        @Override
        public String getMessage() {
            return "Query " + query.getName() + " has no bound variable. At least one binding is required to determine the value returned by this query";
        }

        @Override
        public int[] getLines() {
            return ERROR_LINES;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            NoBindingQuery that = (NoBindingQuery) o;
            return Objects.equals(query, that.query);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), query);
        }
    }
}
