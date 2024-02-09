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
package org.drools.model.codegen.execmodel.generator.declaredtype.generator;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;

import static com.github.javaparser.StaticJavaParser.parseStatement;
import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.createSimpleAnnotation;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.model.codegen.execmodel.generator.declaredtype.generator.GeneratedClassDeclaration.replaceFieldName;

class GeneratedEqualsMethod {

    private static final Statement referenceEquals = parseStatement("if (this == o) { return true; }");
    private static final Statement classCheckEquals = parseStatement("if (o == null || getClass() != o.getClass()) { return false; }");

    private final String generatedClassName;
    private final boolean hasSuper;
    private List<Statement> equalsFieldStatement = new ArrayList<>();
    private static final String EQUALS = "equals";

    GeneratedEqualsMethod(String generatedClassName, boolean hasSuper) {
        this.generatedClassName = generatedClassName;
        this.hasSuper = hasSuper;
    }

    void add(MethodDeclaration getter, String fieldName) {
        equalsFieldStatement.add(generateEqualsForField(getter, fieldName));
    }

    private Statement classCastStatement(String className) {
        Statement statement = parseStatement("__className that = (__className) o;");
        statement.findAll(ClassOrInterfaceType.class)
                .stream()
                .filter(n1 -> n1.getName().toString().equals("__className"))
                .forEach(n -> n.replace(toClassOrInterfaceType(className)));
        return statement;
    }

    private Statement generateEqualsForField(MethodDeclaration getter, String fieldName) {

        Type type = getter.getType();
        Statement statement;
        if (type instanceof ClassOrInterfaceType) {
            statement = parseStatement(" if( __fieldName != null ? !__fieldName.equals(that.__fieldName) : that.__fieldName != null) { return false; }");
        } else if (type instanceof ArrayType) {
            Type componentType = ((ArrayType) type).getComponentType();
            if (componentType instanceof PrimitiveType) {
                statement = parseStatement(" if( !java.util.Arrays.equals((" + componentType + "[])__fieldName, (" + componentType + "[])that.__fieldName)) { return false; }");
            } else {
                statement = parseStatement(" if( !java.util.Arrays.equals((Object[])__fieldName, (Object[])that.__fieldName)) { return false; }");
            }
        } else if (type instanceof PrimitiveType) {
            statement = parseStatement(" if( __fieldName != that.__fieldName) { return false; }");
        } else {
            throw new RuntimeException("Unknown type");
        }
        return replaceFieldName(statement, fieldName);
    }

    public MethodDeclaration method() {
        NodeList<Statement> equalsStatements = nodeList(referenceEquals, classCheckEquals);
        equalsStatements.add(classCastStatement(generatedClassName));
        if (hasSuper) {
            equalsStatements.add(parseStatement("if ( !super.equals( o ) ) return false;"));
        }
        equalsStatements.addAll(equalsFieldStatement);
        equalsStatements.add(parseStatement("return true;"));

        final Type returnType = toClassOrInterfaceType(boolean.class);
        final MethodDeclaration equals = new MethodDeclaration(nodeList(Modifier.publicModifier()), returnType, EQUALS);
        equals.addParameter(Object.class, "o");
        equals.addAnnotation(createSimpleAnnotation(Override.class));
        equals.setBody(new BlockStmt(equalsStatements));
        return equals;
    }
}
