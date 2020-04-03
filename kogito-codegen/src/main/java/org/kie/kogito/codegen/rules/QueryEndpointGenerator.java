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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.Type;
import org.drools.compiler.compiler.DroolsError;
import org.drools.modelcompiler.builder.QueryModel;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.FileGenerator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;

import static com.github.javaparser.StaticJavaParser.parse;
import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseStatement;
import static org.drools.core.util.StringUtils.ucFirst;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.classNameToReferenceType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.classToReferenceType;

public class QueryEndpointGenerator implements FileGenerator {

    private final RuleUnitDescription ruleUnit;
    private final QueryModel query;
    private final DependencyInjectionAnnotator annotator;

    private final String name;
    private final String endpointName;
    private final String targetCanonicalName;
    private final String generatedFilePath;
    private final boolean useMonitoring;

    public QueryEndpointGenerator(RuleUnitDescription ruleUnit, QueryModel query, DependencyInjectionAnnotator annotator, boolean useMonitoring) {
        this.ruleUnit = ruleUnit;
        this.query = query;
        this.name = toCamelCase(query.getName());
        this.endpointName = toKebabCase(name);
        this.annotator = annotator;

        this.targetCanonicalName = ruleUnit.getSimpleName() + "Query" + name + "Endpoint";
        this.generatedFilePath = (query.getNamespace() + "." + targetCanonicalName).replace('.', '/') + ".java";
        this.useMonitoring = useMonitoring;
    }

    @Override
    public String generatedFilePath() {
        return generatedFilePath;
    }

    @Override
    public boolean validate() {
        return !query.getBindings().isEmpty();
    }

    @Override
    public DroolsError getError() {
        if (query.getBindings().isEmpty()) {
            return new NoBindingQuery( query );
        }
        return null;
    }

    public static class NoBindingQuery extends DroolsError {
        private static final int[] ERROR_LINES = new int[0];

        private final QueryModel query;

        public NoBindingQuery( QueryModel query ) {
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
    }

    @Override
    public String generate() {
        CompilationUnit cu = parse(
                this.getClass().getResourceAsStream("/class-templates/rules/RestQueryTemplate.java"));
        cu.setPackageDeclaration(query.getNamespace());

        ClassOrInterfaceDeclaration clazz = cu
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));
        clazz.setName(targetCanonicalName);

        cu.findAll(StringLiteralExpr.class).forEach(this::interpolateStrings);

        FieldDeclaration ruleUnitDeclaration = clazz
                .getFieldByName("ruleUnit")
                .orElseThrow(() -> new NoSuchElementException("ClassOrInterfaceDeclaration doesn't contain a field named ruleUnit!"));
        setUnitGeneric(ruleUnitDeclaration.getElementType());
        if (annotator != null) {
            annotator.withInjection(ruleUnitDeclaration);
        }

        String returnType = getReturnType(clazz);
        generateConstructors(clazz);
        generateQueryMethods(cu, clazz, returnType);
        clazz.getMembers().sort(new BodyDeclarationComparator());
        return cu.toString();
    }

    public String getEndpointName() {
        return endpointName;
    }

    private void generateConstructors(ClassOrInterfaceDeclaration clazz) {
        for (ConstructorDeclaration c : clazz.getConstructors()) {
            c.setName(targetCanonicalName);
            if (!c.getParameters().isEmpty()) {
                setUnitGeneric(c.getParameter(0).getType());
            }
        }
    }

    private void generateQueryMethods(CompilationUnit cu, ClassOrInterfaceDeclaration clazz, String returnType) {
        MethodDeclaration queryMethod = clazz.getMethodsByName("executeQuery").get(0);
        queryMethod.getParameter(0).setType(ruleUnit.getCanonicalName() + "DTO");
        setGeneric(queryMethod.getType(), returnType);

        Statement statement = queryMethod
                .getBody()
                .orElseThrow(() -> new NoSuchElementException("A method declaration doesn't contain a body!"))
                .getStatement(0);
        statement.findAll(VariableDeclarator.class).forEach(decl -> setUnitGeneric(decl.getType()));

        Statement returnStatement = queryMethod
                .getBody()
                .orElseThrow(() -> new NoSuchElementException("A method declaration doesn't contain a body!"))
                .getStatement(1);
        returnStatement.findAll(VariableDeclarator.class).forEach(decl -> setGeneric(decl.getType(), returnType));

        MethodDeclaration queryMethodSingle = clazz.getMethodsByName("executeQueryFirst").get(0);
        queryMethodSingle.getParameter(0).setType(ruleUnit.getCanonicalName() + "DTO");
        queryMethodSingle.setType(toNonPrimitiveType(returnType));

        Statement statementSingle = queryMethodSingle
                .getBody()
                .orElseThrow(() -> new NoSuchElementException("A method declaration doesn't contain a body!"))
                .getStatement(0);
        statementSingle.findAll(VariableDeclarator.class).forEach(decl -> setGeneric(decl.getType(), returnType));

        Statement returnMethodSingle = queryMethodSingle
                .getBody()
                .orElseThrow(() -> new NoSuchElementException("A method declaration doesn't contain a body!"))
                .getStatement(1);
        returnMethodSingle.findAll(VariableDeclarator.class).forEach(decl -> decl.setType(toNonPrimitiveType(returnType)));

        if (useMonitoring) {
            addMonitoringToResource(cu, new MethodDeclaration[]{queryMethod, queryMethodSingle}, endpointName);
        }
    }

    private void addMonitoringToResource(CompilationUnit cu, MethodDeclaration[] methods, String nameURL) {
        cu.addImport(new ImportDeclaration(new Name("org.kie.kogito.monitoring.system.metrics.SystemMetricsCollector"), false, false));

        for (MethodDeclaration md : methods) {
            BlockStmt body = md.getBody().orElseThrow(() -> new NoSuchElementException("A method declaration doesn't contain a body!"));
            NodeList<Statement> statements = body.getStatements();
            ReturnStmt returnStmt = body.findFirst(ReturnStmt.class).orElseThrow(() -> new NoSuchElementException("A method declaration doesn't contain a return statement!"));
            statements.addFirst(parseStatement("double startTime = System.nanoTime();"));
            statements.addBefore(parseStatement("double endTime = System.nanoTime();"), returnStmt);
            statements.addBefore(parseStatement("SystemMetricsCollector.registerElapsedTimeSampleMetrics(\"" + nameURL + "\", endTime - startTime);"), returnStmt);
            md.setBody(wrapBodyAddingExceptionLogging(body, nameURL));
        }
    }

    private BlockStmt wrapBodyAddingExceptionLogging(BlockStmt body, String nameURL) {
        TryStmt ts = new TryStmt();
        ts.setTryBlock(body);
        CatchClause cc = new CatchClause();
        String exceptionName = "e";
        cc.setParameter(new Parameter().setName(exceptionName).setType(Exception.class));
        BlockStmt cb = new BlockStmt();
        cb.addStatement(parseStatement(
                String.format(
                        "SystemMetricsCollector.registerException(\"%s\", %s.getStackTrace()[0].toString());",
                        nameURL,
                        exceptionName)
        ));
        cb.addStatement(new ThrowStmt(new NameExpr(exceptionName)));
        cc.setBody(cb);
        ts.setCatchClauses(new NodeList<>(cc));
        return new BlockStmt(new NodeList<>(ts));
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
            returnType = "Result";
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

    private void setUnitGeneric(Type type) {
        setGeneric(type, ruleUnit);
    }

    private void setGeneric(Type type, RuleUnitDescription ruleUnit) {
        type.asClassOrInterfaceType().setTypeArguments(classNameToReferenceType(ruleUnit.getCanonicalName()));
    }

    private void setGeneric(Type type, Class<?> typeArgument) {
        type.asClassOrInterfaceType().setTypeArguments(classToReferenceType(typeArgument));
    }

    private void setGeneric(Type type, String typeArgument) {
        type.asClassOrInterfaceType().setTypeArguments(parseClassOrInterfaceType(toNonPrimitiveType(typeArgument)));
    }

    private static String toNonPrimitiveType(String type) {
        switch (type) {
            case "int":
                return "Integer";
            case "long":
                return "Long";
            case "double":
                return "Double";
            case "float":
                return "Float";
            case "short":
                return "Short";
            case "byte":
                return "Byte";
            case "char":
                return "Character";
            case "boolean":
                return "Boolean";
        }
        return type;
    }

    private void interpolateStrings(StringLiteralExpr vv) {
        String interpolated = vv.getValue()
                .replace("$name$", name)
                .replace("$endpointName$", endpointName)
                .replace("$queryName$", query.getName())
                .replace("$prometheusName$", endpointName);
        vv.setString(interpolated);
    }

    private static String toCamelCase(String inputString) {
        return Stream.of(inputString.split(" "))
                .map(s -> s.length() > 1 ? s.substring(0, 1).toUpperCase() + s.substring(1) : s.substring(0, 1).toUpperCase())
                .collect(Collectors.joining());
    }

    private static String toKebabCase(String inputString) {
        return inputString.replaceAll("(.)(\\p{Upper})", "$1-$2").toLowerCase();
    }
}
