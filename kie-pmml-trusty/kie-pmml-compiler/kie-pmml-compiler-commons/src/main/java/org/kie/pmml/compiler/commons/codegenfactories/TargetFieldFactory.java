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
package org.kie.pmml.compiler.commons.codegenfactories;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.kie.pmml.api.enums.CAST_INTEGER;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.models.TargetField;
import org.kie.pmml.api.models.TargetValue;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.compiler.commons.codegenfactories.TargetValueFactory.getTargetValueVariableInitializer;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>TargetField</code> code-generators
 * out of <code>TargetField</code>s
 */
public class TargetFieldFactory {

    static final String TARGET_FIELD_TEMPLATE_JAVA = "TargetFieldTemplate.tmpl";
    static final String TARGET_FIELD_TEMPLATE = "TargetFieldTemplate";
    static final String GETTARGET_FIELD = "getTargetField";
    static final String TARGET_FIELD = "targetField";
    static final ClassOrInterfaceDeclaration TARGET_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(TARGET_FIELD_TEMPLATE_JAVA);
        TARGET_TEMPLATE = cloneCU.getClassByName(TARGET_FIELD_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + TARGET_FIELD_TEMPLATE));
        TARGET_TEMPLATE.getMethodsByName(GETTARGET_FIELD).get(0).clone();
    }

    private TargetFieldFactory() {
        // Avoid instantiation
    }

    static ObjectCreationExpr getTargetFieldVariableInitializer(final TargetField targetField) {
        final MethodDeclaration methodDeclaration = TARGET_TEMPLATE.getMethodsByName(GETTARGET_FIELD).get(0).clone();
        final BlockStmt targetBody =
                methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(targetBody, TARGET_FIELD).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, TARGET_FIELD, targetBody)));
        variableDeclarator.setName(targetField.getName());
        final ObjectCreationExpr toReturn = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                      TARGET_FIELD,
                                                                      targetBody)))
                .asObjectCreationExpr();
        final NodeList<Expression> arguments = new NodeList<>();
        if (targetField.getTargetValues() != null) {
            for (TargetValue targetValue : targetField.getTargetValues()) {
                arguments.add(getTargetValueVariableInitializer(targetValue));
            }
        }
        toReturn.getArgument(0).asMethodCallExpr().setArguments(arguments);
        OP_TYPE oPT = targetField.getOpType();
        Expression opType = oPT != null ?
                new NameExpr(oPT.getClass().getName() + "." + oPT.name())
                : new NullLiteralExpr();
        toReturn.setArgument(1, opType);
        toReturn.setArgument(2, getExpressionForObject(targetField.getField()));
        CAST_INTEGER cstInt = targetField.getCastInteger();
        Expression castInteger = cstInt != null ?
                new NameExpr(cstInt.getClass().getName() + "." + cstInt.name())
                : new NullLiteralExpr();
        toReturn.setArgument(3, castInteger);
        toReturn.setArgument(4, getExpressionForObject(targetField.getMin()));
        toReturn.setArgument(5, getExpressionForObject(targetField.getMax()));
        toReturn.setArgument(6, getExpressionForObject(targetField.getRescaleConstant()));
        toReturn.setArgument(7, getExpressionForObject(targetField.getRescaleFactor()));
        return toReturn;
    }
}
