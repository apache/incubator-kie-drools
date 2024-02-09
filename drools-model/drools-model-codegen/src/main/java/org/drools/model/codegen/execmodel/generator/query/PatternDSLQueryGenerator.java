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

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;

import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.NodeList.nodeList;

public class PatternDSLQueryGenerator extends Generator {

    private final ClassOrInterfaceDeclaration clazz;

    PatternDSLQueryGenerator(ClassOrInterfaceDeclaration clazz, int arity) {
        super(arity);
        this.clazz = clazz;
    }

    public ClassOrInterfaceDeclaration generate() {

        queryFirstMethodOnlyClass(clazz);
        querySecondMethodOnlyClass(clazz);

        queryFirstMethod(clazz);
        querySecondMethod(clazz);

        return clazz;
    }

    private void queryFirstMethodOnlyClass(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration query1 = clazz.addMethod(QUERY, Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);

        addNameParameter(query1);

        BlockStmt stmts = new BlockStmt();
        NodeList<Type> typeArguments = nodeList();
        NodeList<TypeParameter> typeParameters = nodeList();

        NodeList<Expression> arguments = nodeList();
        ClassOrInterfaceType queryCallViewItemImpl = queryDefImplType();
        ObjectCreationExpr objCreationExpr = new ObjectCreationExpr(null, queryCallViewItemImpl, arguments);
        objCreationExpr.setDiamondOperator();
        stmts.addStatement(new ReturnStmt(objCreationExpr));
        objCreationExpr.addArgument(new NameExpr(VIEWBUILDER));
        objCreationExpr.addArgument("name");

        rangeArity().forEach(i -> {
            String genericTypeName = stringWithIndex("T", i);
            String type = stringWithIndex("type", i);

            typeArguments.add(parseType(genericTypeName));

            arguments.add(new NameExpr(type));

            String classGenericType = genericType(CLASS, genericTypeName);
            Type genericType = parseType(classGenericType);
            query1.addParameter(genericType, type);

            typeParameters.add(new TypeParameter(genericTypeName));

        });

        query1.setTypeParameters(typeParameters);


        query1.setBody(stmts);
        ClassOrInterfaceType type = queryDefType();
        type.setTypeArguments(typeArguments);
        query1.setType(type);

    }

    private void querySecondMethodOnlyClass(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration query1 = clazz.addMethod(QUERY, Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);

        query1.addParameter(STRINGLITERAL, "pkg");
        query1.addParameter(STRINGLITERAL, "name");


        BlockStmt stmts = new BlockStmt();
        NodeList<Type> typeArguments = nodeList();
        NodeList<TypeParameter> typeParameters = nodeList();
        NodeList<Expression> arguments = nodeList();
        ClassOrInterfaceType queryCallViewItemImpl = queryDefImplType();
        ObjectCreationExpr objCreationExpr = new ObjectCreationExpr(null, queryCallViewItemImpl, arguments);
        objCreationExpr.setDiamondOperator();
        stmts.addStatement(new ReturnStmt(objCreationExpr));
        objCreationExpr.addArgument(new NameExpr(VIEWBUILDER));
        objCreationExpr.addArgument("pkg");
        objCreationExpr.addArgument("name");

        rangeArity().forEach(i -> {
            String genericTypeName = stringWithIndex("T", i);
            String type = stringWithIndex("type", i);

            typeArguments.add(parseType(genericTypeName));

            arguments.add(new NameExpr(type));

            String classGenericType = genericType(CLASS, genericTypeName);
            Type genericType = parseType(classGenericType);
            query1.addParameter(genericType, type);

            typeParameters.add(new TypeParameter(genericTypeName));
        });

        query1.setTypeParameters(typeParameters);

        query1.setBody(stmts);
        ClassOrInterfaceType type = queryDefType();
        type.setTypeArguments(typeArguments);
        query1.setType(type);
    }

    private void queryFirstMethod(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration query1 = clazz.addMethod(QUERY, Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);


        query1.addParameter(STRINGLITERAL, "name");

        BlockStmt stmts = new BlockStmt();
        NodeList<Type> typeArguments = nodeList();
        NodeList<TypeParameter> typeParameters = nodeList();

        NodeList<Expression> arguments = nodeList();
        ClassOrInterfaceType queryCallViewItemImpl = queryDefImplType();
        ObjectCreationExpr objCreationExpr = new ObjectCreationExpr(null, queryCallViewItemImpl, arguments);
        objCreationExpr.setDiamondOperator();
        stmts.addStatement(new ReturnStmt(objCreationExpr));
        objCreationExpr.addArgument(new NameExpr(VIEWBUILDER));
        objCreationExpr.addArgument("name");

        rangeArity().forEach(i -> {
            String genericTypeName = stringWithIndex("T", i);
            String type = stringWithIndex("type", i);
            String name = argNameWithIndex(i);

            typeArguments.add(parseType(genericTypeName));

            arguments.add(new NameExpr(type));
            arguments.add(new NameExpr(name));

            String classGenericType = genericType("Class", genericTypeName);
            Type genericType = parseType(classGenericType);
            query1.addParameter(genericType, type);
            query1.addParameter(parseType(STRINGLITERAL), name);

            typeParameters.add(new TypeParameter(genericTypeName));

        });

        query1.setTypeParameters(typeParameters);


        query1.setBody(stmts);
        ClassOrInterfaceType type = queryDefType();
        type.setTypeArguments(typeArguments);
        query1.setType(type);

    }

    private void querySecondMethod(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration query1 = clazz.addMethod(QUERY, Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);

        query1.addParameter(STRINGLITERAL, "pkg");
        query1.addParameter(STRINGLITERAL, "name");


        BlockStmt stmts = new BlockStmt();
        NodeList<Type> typeArguments = nodeList();
        NodeList<TypeParameter> typeParameters = nodeList();
        NodeList<Expression> arguments = nodeList();
        ClassOrInterfaceType queryCallViewItemImpl = queryDefImplType();
        ObjectCreationExpr objCreationExpr = new ObjectCreationExpr(null, queryCallViewItemImpl, arguments);
        objCreationExpr.setDiamondOperator();
        stmts.addStatement(new ReturnStmt(objCreationExpr));
        objCreationExpr.addArgument(new NameExpr(VIEWBUILDER));
        objCreationExpr.addArgument("pkg");
        objCreationExpr.addArgument("name");

        rangeArity().forEach(i -> {
            String genericTypeName = stringWithIndex("T", i);
            String type = stringWithIndex("type", i);
            String name = argNameWithIndex(i);

            typeArguments.add(parseType(genericTypeName));

            arguments.add(new NameExpr(type));
            arguments.add(new NameExpr(name));

            String classGenericType = genericType("Class", genericTypeName);
            Type genericType = parseType(classGenericType);
            query1.addParameter(genericType, type);
            query1.addParameter(parseType(STRINGLITERAL), name);

            typeParameters.add(new TypeParameter(genericTypeName));
        });

        query1.setTypeParameters(typeParameters);

        query1.setBody(stmts);
        ClassOrInterfaceType type = queryDefType();
        type.setTypeArguments(typeArguments);
        query1.setType(type);
    }

}
