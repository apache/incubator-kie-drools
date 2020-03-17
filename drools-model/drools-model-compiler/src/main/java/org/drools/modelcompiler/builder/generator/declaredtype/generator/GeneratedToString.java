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
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;

import static java.text.MessageFormat.format;

import static com.github.javaparser.StaticJavaParser.parseStatement;
import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.modelcompiler.builder.generator.declaredtype.generator.GeneratedClassDeclaration.OVERRIDE;
import static org.drools.modelcompiler.builder.generator.declaredtype.POJOGenerator.quote;

public class GeneratedToString {

    private static final String TO_STRING = "toString";

    private final String generatedClassName;
    private List<String> toStringStatements = new ArrayList<>();

    GeneratedToString(String generatedClassName) {
        this.generatedClassName = generatedClassName;
    }

    public void add(String toStringStatement) {
        toStringStatements.add(toStringStatement);
    }

    public MethodDeclaration method() {
        final String header = format("return {0} + {1}", quote(generatedClassName), quote("( "));
        final String body = String.join(format("+ {0}", quote(", ")), toStringStatements);
        final String close = format("+{0};", quote(" )"));

        final Statement toStringStatement = parseStatement(header + body + close);

        final Type returnType = parseType(String.class.getSimpleName());
        final MethodDeclaration equals = new MethodDeclaration(nodeList(Modifier.publicModifier()), returnType, TO_STRING);
        equals.addAnnotation(OVERRIDE);
        equals.setBody(new BlockStmt(nodeList(toStringStatement)));
        return equals;
    }
}
