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
package org.kie.kogito.codegen.rules;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.drools.codegen.common.GeneratedFile;
import org.kie.kogito.codegen.core.BodyDeclarationComparator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.Type;

import static com.github.javaparser.StaticJavaParser.parseStatement;
import static org.drools.codegen.common.GeneratedFileType.REST;
import static org.drools.model.codegen.execmodel.util.RuleCodegenUtils.setGeneric;
import static org.drools.model.codegen.execmodel.util.RuleCodegenUtils.toKebabCase;
import static org.drools.model.codegen.execmodel.util.RuleCodegenUtils.toNonPrimitiveType;

public class QueryEndpointGenerator extends AbstractQueryEntrypointGenerator {

    private final String endpointName;

    public QueryEndpointGenerator(QueryGenerator queryGenerator) {
        super(queryGenerator, "Endpoint", "RestQuery");
        this.endpointName = toKebabCase(queryName);
    }

    @Override
    public GeneratedFile generate() {
        CompilationUnit cu = generator.compilationUnitOrThrow("Could not create CompilationUnit");
        cu.setPackageDeclaration(query.model().getNamespace());

        ClassOrInterfaceDeclaration clazz = cu
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));
        clazz.setName(targetClassName);

        cu.findAll(StringLiteralExpr.class).forEach(this::interpolateStrings);

        FieldDeclaration ruleUnitDeclaration = clazz
                .getFieldByName("ruleUnit")
                .orElseThrow(() -> new NoSuchElementException("ClassOrInterfaceDeclaration doesn't contain a field named ruleUnit!"));
        setUnitGeneric(ruleUnitDeclaration.getElementType());

        String returnType = getReturnType();
        generateConstructors(clazz);
        generateQueryMethods(cu, clazz, returnType);
        clazz.getMembers().sort(new BodyDeclarationComparator());

        return new GeneratedFile(REST,
                generatedFilePath(),
                cu.toString());
    }

    public String getEndpointName() {
        return endpointName;
    }

    private void generateConstructors(ClassOrInterfaceDeclaration clazz) {
        for (ConstructorDeclaration c : clazz.getConstructors()) {
            c.setName(targetClassName);
            if (!c.getParameters().isEmpty()) {
                setUnitGeneric(c.getParameter(0).getType());
            }
        }
    }

    private void generateQueryMethods(CompilationUnit cu, ClassOrInterfaceDeclaration clazz, String returnType) {
        boolean hasDI = context.hasDI();
        MethodDeclaration queryMethod = clazz.getMethodsByName("executeQuery").get(0);
        queryMethod.getParameter(0).setType(ruleUnit.getCanonicalName() + (hasDI ? "" : "DTO"));
        setGeneric(queryMethod.getType(), returnType);

        Statement statement = queryMethod
                .getBody()
                .orElseThrow(() -> new NoSuchElementException("A method declaration doesn't contain a body!"))
                .getStatement(0);
        statement.findAll(VariableDeclarator.class).forEach(decl -> setUnitGeneric(decl.getType()));
        statement.findAll(MethodCallExpr.class).forEach(m -> m.addArgument(hasDI ? "unitDTO" : "unitDTO.get()"));

        Statement returnStatement = queryMethod
                .getBody()
                .orElseThrow(() -> new NoSuchElementException("A method declaration doesn't contain a body!"))
                .getStatement(1);
        returnStatement.findAll(VariableDeclarator.class).forEach(decl -> setGeneric(decl.getType(), returnType));
        returnStatement.findAll(MethodCallExpr.class).forEach(expr -> expr.setScope(new NameExpr(queryClassName)));

        MethodDeclaration queryMethodSingle = clazz.getMethodsByName("executeQueryFirst").get(0);
        queryMethodSingle.getParameter(0).setType(ruleUnit.getCanonicalName() + (hasDI ? "" : "DTO"));
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

        if (context.getAddonsConfig().useMonitoring()) {
            addMonitoringToResource(cu, clazz, new MethodDeclaration[] { queryMethod, queryMethodSingle }, endpointName);
        }
    }

    private void addMonitoringToResource(CompilationUnit cu, ClassOrInterfaceDeclaration clazz, MethodDeclaration[] methods, String nameURL) {
        cu.getImports().add(new ImportDeclaration(new Name("org.kie.kogito.monitoring.core.common.system.metrics.SystemMetricsCollectorProvider"), false, false));
        FieldDeclaration field = clazz.addField("SystemMetricsCollectorProvider", "systemMetricsCollectorProvider");
        if (context.hasDI()) {
            context.getDependencyInjectionAnnotator().withInjection(field);
        }

        for (MethodDeclaration md : methods) {
            BlockStmt body = md.getBody().orElseThrow(() -> new NoSuchElementException("A method declaration doesn't contain a body!"));
            NodeList<Statement> statements = body.getStatements();
            ReturnStmt returnStmt = body.findFirst(ReturnStmt.class).orElseThrow(() -> new NoSuchElementException("A method declaration doesn't contain a return statement!"));
            statements.addFirst(parseStatement("long startTime = System.nanoTime();"));
            statements.addBefore(parseStatement("long endTime = System.nanoTime();"), returnStmt);
            String endpoint = nameURL;
            if (context.hasDI()) {
                Optional<String> path = context.getRestAnnotator().getEndpointValue(md);
                if (path.isPresent()) {
                    endpoint += path.get();
                }
            }
            statements.addBefore(parseStatement("systemMetricsCollectorProvider.get().registerElapsedTimeSampleMetrics(\"" + endpoint + "\", endTime - startTime);"), returnStmt);
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
                        "systemMetricsCollectorProvider.get().registerException(\"%s\", %s.getStackTrace()[0].toString());",
                        nameURL,
                        exceptionName)));
        cb.addStatement(new ThrowStmt(new NameExpr(exceptionName)));
        cc.setBody(cb);
        ts.setCatchClauses(new NodeList<>(cc));
        return new BlockStmt(new NodeList<>(ts));
    }

    private String getReturnType() {
        if (query.model().getBindings().size() == 1) {
            Map.Entry<String, Class<?>> binding = query.model().getBindings().entrySet().iterator().next();
            return binding.getValue().getCanonicalName();
        }
        return queryClassName + ".Result";
    }

    private void interpolateStrings(StringLiteralExpr vv) {
        String interpolated = vv.getValue()
                .replace("$name$", queryName)
                .replace("$endpointName$", endpointName)
                .replace("$queryName$", query.model().getName())
                .replace("$prometheusName$", endpointName);
        vv.setString(interpolated);
    }

    private void setUnitGeneric(Type type) {
        setGeneric(type, ruleUnit);
    }
}
