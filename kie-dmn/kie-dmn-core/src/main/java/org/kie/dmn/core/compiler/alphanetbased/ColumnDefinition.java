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
package org.kie.dmn.core.compiler.alphanetbased;

import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.model.api.UnaryTests;

import static org.kie.dmn.core.compiler.alphanetbased.TableCell.ALPHANETWORK_STATIC_PACKAGE;
import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.replaceIntegerLiteralExprWith;
import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.replaceSimpleNameWith;
import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.replaceStringLiteralExprWith;

/**
 * Used to generate Column Validators from ColumnValidatorTemplate.java
 */
public class ColumnDefinition {

    private final String feelClassName;
    private final String feelClassNameWithPackage;

    private final String className;
    private final String classNameWithPackage;

    private final DMNFEELHelper feel;
    private final CompilerContext compilerContext;

    protected int columnIndex;

    protected final Optional<UnaryTests> optionalInputValues;
    protected final Type type;
    protected final String validValues;
    protected final String columnName;
    protected final String decisionTableName;

    public ColumnDefinition(int columnIndex,
                            String decisionTableName,
                            String columnName,
                            UnaryTests inputValues,
                            Type type,
                            DMNFEELHelper feel,
                            CompilerContext compilerContext) {
        this.columnIndex = columnIndex;
        this.columnName = columnName;
        this.type = type;
        this.optionalInputValues = Optional.ofNullable(inputValues);
        this.feel = feel;
        this.compilerContext = compilerContext;
        this.validValues = optionalInputValues.map(UnaryTests::getText).orElse(null);
        this.decisionTableName = decisionTableName;

        feelClassName = String.format("UnaryTestColumnValidator%s", columnIndex);
        feelClassNameWithPackage = ALPHANETWORK_STATIC_PACKAGE + "." + feelClassName;

        className = String.format("ColumnValidator%s", columnIndex);
        classNameWithPackage = ALPHANETWORK_STATIC_PACKAGE + "." + className;
    }

    public void compileUnaryTestAndAddTo(Map<String, String> allGeneratedSources) {
        optionalInputValues.ifPresent(inputValues -> {
            UnaryTestClass unaryTestClass = new UnaryTestClass(inputValues.getText(), feel, compilerContext, type);
            unaryTestClass.compileUnaryTestAndAddTo(allGeneratedSources, feelClassName, feelClassNameWithPackage, ALPHANETWORK_STATIC_PACKAGE);
        });

    }

    public void initColumnValidatorTemplateAddToClasses(CompilationUnit columnValidatorTemplate, Map<String, String> validatorGeneratedClasses) {

        replaceSimpleNameWith(columnValidatorTemplate, "ColumnValidatorTemplate", className);

        replaceSimpleNameWith(columnValidatorTemplate, "ColumnValidatorX", feelClassNameWithPackage);

        // TODO DT-ANC check quoting
        replaceStringLiteralExprWith(columnValidatorTemplate, "VALID_VALUES", validValues.replace("\"", "\\\""));
        replaceStringLiteralExprWith(columnValidatorTemplate, "COLUMN_NAME", columnName);
        replaceStringLiteralExprWith(columnValidatorTemplate, "DECISION_TABLE_NAME", decisionTableName);

        validatorGeneratedClasses.put(classNameWithPackage, columnValidatorTemplate.toString());
    }

    public void initValidationStatement(BlockStmt newValidationStatement) {
        newValidationStatement.removeComment();

        replaceSimpleNameWith(newValidationStatement, "resultValidation0", "resultValidation" + columnIndex);
        replaceSimpleNameWith(newValidationStatement, "ValidatorC0", classNameWithPackage);
        replaceIntegerLiteralExprWith(newValidationStatement, 777, columnIndex);
    }
}
