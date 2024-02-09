/**
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
package org.drools.model.codegen.execmodel.generator.query;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.TypeParameter;
import org.drools.model.view.QueryCallViewItem;

import static com.github.javaparser.StaticJavaParser.parseImport;
import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;

public class QueryDefGenerator extends Generator {

    private String className;

    QueryDefGenerator(int arity) {
        super(arity);
        className = String.format("Query%dDef", arity);
    }

    public String getClassName() {
        return className;
    }

    public CompilationUnit generate() {

        CompilationUnit cu = new CompilationUnit("org.drools.model");

        cu.setImports(nodeList(
                parseImport("import org.drools.model.view.QueryCallViewItem;")
        ));

        ClassOrInterfaceDeclaration clazz = classDeclaration(cu);
        callMethod(clazz);
        callMethodInterface(clazz);
        getters(clazz);

        return cu;
    }

    private ClassOrInterfaceDeclaration classDeclaration(CompilationUnit cu) {
        ClassOrInterfaceDeclaration clazz = cu.addInterface(className, Modifier.Keyword.PUBLIC);

        clazz.addExtendedType("QueryDef");


        rangeArity().forEach(i -> {
            String genericTypeName = stringWithIndex("T", i);

            clazz.addTypeParameter(new TypeParameter(genericTypeName));
        });

        return clazz;
    }

    private void callMethod(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration method = clazz.addMethod("call", Modifier.Keyword.DEFAULT);
        method.setType(toClassOrInterfaceType(QueryCallViewItem.class));

        BlockStmt stmts = new BlockStmt();
        NodeList<Expression> arguments = nodeList();
        MethodCallExpr objCreationExpr = new MethodCallExpr(null, "call", arguments);
        stmts.addStatement(new ReturnStmt(objCreationExpr));
        objCreationExpr.addArgument(new BooleanLiteralExpr(true));

        rangeArity().forEach(i -> {
            String varWithIndex = stringWithIndex("var", i);
            String genericTypeName = stringWithIndex("T", i);

            method.addParameter(genericType("Argument", genericTypeName), varWithIndex);

            objCreationExpr.addArgument(varWithIndex);
        });

        method.setBody(stmts);
    }


    private void callMethodInterface(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration method = new MethodDeclaration(nodeList(), toClassOrInterfaceType(QueryCallViewItem.class), "call");
        method.addParameter("boolean", "open");
        method.setBody(null);

        rangeArity().forEach(i -> {
            String varWithIndex = stringWithIndex("var", i);
            String genericTypeName = stringWithIndex("T", i);

            method.addParameter(genericType("Argument", genericTypeName), varWithIndex);

        });

        clazz.addMember(method);

    }

    private void getters(ClassOrInterfaceDeclaration clazz) {
        rangeArity().forEach(i -> {
            String genericTypeName = stringWithIndex("T", i);
            String methodName = stringWithIndex("getArg", i);

            MethodDeclaration methodDeclaration = clazz.addMethod(methodName);
            methodDeclaration.setBody(null);
            methodDeclaration.setType(genericType("Variable", genericTypeName));
        });
    }
}
