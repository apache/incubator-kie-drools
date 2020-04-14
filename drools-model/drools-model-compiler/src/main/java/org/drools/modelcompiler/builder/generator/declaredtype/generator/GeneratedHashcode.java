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
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator.declaredtype.generator;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;

import static java.text.MessageFormat.format;

import static com.github.javaparser.StaticJavaParser.parseStatement;
import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.modelcompiler.builder.generator.declaredtype.generator.GeneratedClassDeclaration.OVERRIDE;

class GeneratedHashcode {

    private static final String HASH_CODE = "hashCode";
    private List<Statement> hashCodeFieldStatement = new ArrayList<>();

    private final boolean hasSuper;

    GeneratedHashcode(boolean hasSuper) {
        this.hasSuper = hasSuper;
    }

    MethodDeclaration method() {
        final Statement header = parseStatement(hasSuper ? "int result = super.hashCode();" : "int result = 1;");
        NodeList<Statement> hashCodeStatements = nodeList(header);
        hashCodeStatements.addAll(hashCodeFieldStatement);
        hashCodeStatements.add(parseStatement("return result;"));

        final Type returnType = parseType(int.class.getSimpleName());
        final MethodDeclaration equals = new MethodDeclaration(nodeList(Modifier.publicModifier()), returnType, HASH_CODE);
        equals.addAnnotation(OVERRIDE);
        equals.setBody(new BlockStmt(hashCodeStatements));
        return equals;
    }

    public void addHashCodeForField(String fieldName, Type type) {
        Statement hashCodeStatement = generateHashCodeForField(fieldName, type);
        hashCodeFieldStatement.add(hashCodeStatement);
    }

    private static Statement generateHashCodeForField(String fieldName, Type type) {
        if (type.isClassOrInterfaceType()) {
            return hashCodeClass(fieldName);
        } else if (type.isArrayType()) {
            return hashCodeArrayType(fieldName, (ArrayType) type);
        } else if (type.isPrimitiveType()) {
            return hashCodePrimitiveType(fieldName, (PrimitiveType) type);
        } else {
            throw new RuntimeException("Unknown type");
        }
    }

    private static Statement hashCodeClass(String fieldName) {
        return parseStatement(format("result = 31 * result + ({0} != null ? {0}.hashCode() : 0);", fieldName));
    }

    private static Statement hashCodeArrayType(String fieldName, ArrayType type) {
        Type componentType = type.getComponentType();
        if (componentType instanceof PrimitiveType) {
            return parseStatement(format("result = 31 * result + ({0} != null ? java.util.Arrays.hashCode(({1}[]){0}) : 0);", fieldName, componentType));
        } else {
            return parseStatement(format("result = 31 * result + ({0} != null ? java.util.Arrays.hashCode((Object[]){0}) : 0);", fieldName));
        }
    }

    private static Statement hashCodePrimitiveType(String fieldName, PrimitiveType type) {
        String primitiveToInt = fieldName;
        PrimitiveType.Primitive primitiveType = type.getType();
        switch (primitiveType) {
            case BOOLEAN:
                primitiveToInt = format("({0} ? 1231 : 1237)", fieldName);
                break;
            case DOUBLE:
                primitiveToInt = format("(int) (Double.doubleToLongBits({0}) ^ (Double.doubleToLongBits({0}) >>> 32))", fieldName);
                break;
            case FLOAT:
                primitiveToInt = format("Float.floatToIntBits({0})", fieldName);
                break;
            case LONG:
                primitiveToInt = format("(int) ({0} ^ ({0} >>> 32))", fieldName);
                break;
        }
        return parseStatement(format("result = 31 * result + {0};", primitiveToInt));
    }
}
