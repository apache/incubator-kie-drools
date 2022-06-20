/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.compilation.commons.codegenfactories;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.Row;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compilation.commons.utils.JavaParserUtils;

import java.util.Map;

import static org.kie.pmml.commons.Constants.*;
import static org.kie.pmml.compilation.api.utils.ModelUtils.getRowDataMap;
import static org.kie.pmml.compilation.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compilation.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compilation.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLRow</code> code-generators
 * out of <code>Row</code>s
 */
public class KiePMMLRowFactory {

    private KiePMMLRowFactory() {
        // Avoid instantiation
    }

    static final String KIE_PMML_ROW_TEMPLATE_JAVA = "KiePMMLRowTemplate.tmpl";
    static final String KIE_PMML_ROW_TEMPLATE = "KiePMMLRowTemplate";
    static final String GETKIEPMMLROW = "getKiePMMLRow";
    static final String ROW = "row";
    static final String COLUMN_VALUES = "columnValues";
    static final ClassOrInterfaceDeclaration ROW_TEMPLATE;


    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_ROW_TEMPLATE_JAVA);
        ROW_TEMPLATE = cloneCU.getClassByName(KIE_PMML_ROW_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_ROW_TEMPLATE));
        ROW_TEMPLATE.getMethodsByName(GETKIEPMMLROW).get(0).clone();
    }

    static BlockStmt getRowVariableDeclaration(final String variableName, final Row row) {
        final MethodDeclaration methodDeclaration = ROW_TEMPLATE.getMethodsByName(GETKIEPMMLROW).get(0).clone();
        final BlockStmt toReturn = methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));

        final String columnValuesVariableName = String.format(VARIABLE_NAME_TEMPLATE, variableName, COLUMN_VALUES);
        final VariableDeclarator columnValuesVariableDeclarator =
                getVariableDeclarator(toReturn, COLUMN_VALUES).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, ROW, toReturn)));
        columnValuesVariableDeclarator.setName(columnValuesVariableName);
        final MethodCallExpr columnValuesVariableInit = columnValuesVariableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, COLUMN_VALUES, toReturn)))
                .asMethodCallExpr();
        final MethodCallExpr columnValuesVariableScope = columnValuesVariableInit.getScope()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, COLUMN_VALUES, toReturn)))
                .asMethodCallExpr();
        final ArrayCreationExpr columnValuesVariableArray = columnValuesVariableScope.getArguments().get(0).asArrayCreationExpr();

        final ArrayInitializerExpr columnValuesVariableArrayInit = columnValuesVariableArray.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, COLUMN_VALUES, toReturn)))
                .asArrayInitializerExpr();
        Map<String, Object> rowDataMap = getRowDataMap(row);
        NodeList<Expression> arguments = new NodeList<>();
        rowDataMap.entrySet().forEach(entry -> {
            ArrayInitializerExpr argument = new ArrayInitializerExpr();
            NodeList<Expression> values = NodeList.nodeList(new StringLiteralExpr(entry.getKey()), getExpressionForObject(entry.getValue()));
            argument.setValues(values);
            arguments.add(argument);
        });
        columnValuesVariableArrayInit.setValues(arguments);

        final VariableDeclarator variableDeclarator = getVariableDeclarator(toReturn, ROW).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, ROW, toReturn)));
        variableDeclarator.setName(variableName);
        final ObjectCreationExpr objectCreationExpr = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, ROW, toReturn)))
                .asObjectCreationExpr();
        final NameExpr nameExpr = new NameExpr(columnValuesVariableName);
        objectCreationExpr.getArguments().set(0, nameExpr);
        return toReturn;
    }
}
