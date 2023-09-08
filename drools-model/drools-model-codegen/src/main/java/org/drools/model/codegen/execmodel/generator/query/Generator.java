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

import java.util.stream.IntStream;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithParameters;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import static com.github.javaparser.StaticJavaParser.parseType;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.createSimpleAnnotation;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;

abstract class Generator {

    final int arity;

    static final String QUERY = "query";
    static final String STRINGLITERAL = "String";
    static final String CLASS = "Class";
    static final String VIEWBUILDER = "VIEW_BUILDER";

    Generator(int arity) {
        this.arity = arity;
    }

    String stringWithIndex(String pre, int i) {
        return pre + i;
    }

    String argNameWithIndex(int i) {
        return "arg" + i + "name";
    }

    IntStream rangeArity() {
        return IntStream.range(1, arity + 1);
    }

    String genericType(String typeName, String genericTypeName) {
        return typeName + "<" + genericTypeName + ">";
    }

    String typeWithIndex(int i) {
        return stringWithIndex("type", i);
    }

    void addPackageParameter(NodeWithParameters declaration) {
        declaration.addParameter(STRINGLITERAL, "pkg");
    }

    String genericTypeName(int i) {
        return stringWithIndex("T", i);
    }

    void addPkgValue(MethodCallExpr constructor) {
        constructor.addArgument("pkg");
    }

    void addViewBuilderValue(MethodCallExpr constructor) {
        constructor.addArgument("viewBuilder");
    }

    void addViewBuilderParameter(NodeWithParameters constructorDeclaration2) {
        constructorDeclaration2.addParameter("ViewBuilder", "viewBuilder");
    }

    void addNameValue(MethodCallExpr constructor) {
        constructor.addArgument("name");
    }

    String argIndex(int i) {
        return stringWithIndex("arg", i);
    }

    String getArgIndex(int i) {
        return stringWithIndex("getArg", i);
    }

    String classGenericName(int i) {
        return classGenericParameter(genericTypeName(i));
    }

    void addDefaultPackageValue(MethodCallExpr constructor) {
        constructor.addArgument("DEFAULT_PACKAGE");
    }

    void addNameParameter(NodeWithParameters constructor) {
        constructor.addParameter(STRINGLITERAL, "name");
    }

    void addOverride(MethodDeclaration method) {
        method.addAnnotation( createSimpleAnnotation(Override.class) );
    }

    String classGenericParameter(String genericTypeName) {
        return genericType(CLASS, genericTypeName);
    }

    String stringWithIndexInside(int i) {
        return "arg" + i + "name";
    }

    String genericType(String genericTypeName) {
        return CLASS + "<" + genericTypeName + ">";
    }

    Type stringType() {
        return parseType(STRINGLITERAL);
    }

    ClassOrInterfaceType queryDefImplType() {
        return toClassOrInterfaceType("Query" + arity + "DefImpl<>");
    }

    ClassOrInterfaceType queryDefType() {
        return toClassOrInterfaceType("Query" + arity + "Def");
    }
}
