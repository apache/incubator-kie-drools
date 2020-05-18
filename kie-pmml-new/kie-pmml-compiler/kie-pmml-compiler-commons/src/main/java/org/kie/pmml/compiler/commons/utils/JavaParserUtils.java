/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.compiler.commons.utils;

import java.io.InputStream;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;

public class JavaParserUtils {

    private JavaParserUtils() {
    }

    public static final String MAIN_CLASS_NOT_FOUND = "Main class not found";

    public static CompilationUnit getFromFileName(String fileName) {
        try {
            final InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            return StaticJavaParser.parse(resource);
        } catch (Exception e) {
            throw new KiePMMLInternalException(String.format("Failed to parse %s due to %s", fileName, e.getMessage()), e);
        }
    }

    public static void setModelName(String modelName, ClassOrInterfaceDeclaration modelTemplate) {
        modelTemplate.getFieldByName("MODEL_NAME").ifPresent(fieldDeclaration -> fieldDeclaration.getVariable(0).setInitializer(new StringLiteralExpr(modelName)));
    }
}
