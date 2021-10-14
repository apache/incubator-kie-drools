/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.compiler.alphanetbased;

import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;

import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.findMethodTemplate;
import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.parseJavaClassTemplateFromResources;
import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.replaceSimpleNameWith;

/**
    Definition of the decision table after the first round of parsing
    Produced by TableCellParser
    Will generate code to evaluate Decision Table using a Compiled Alpha Network (ANC)
 */
public class TableCells {

    private final int numRows;
    private final int numColumns;

    TableCell[][] cells;

    // Number of output columns is not written anywhere
    // First time we parse output column we initialise the collection
    private int numOutputColumns;
    TableCell[][] outputCells = null;

    ColumnDefinition[] columns;


    public TableCells(int numRows, int numColumns) {
        this.numRows = numRows;
        this.numColumns = numColumns;
        cells = new TableCell[numRows][numColumns];
        columns = new ColumnDefinition[numColumns];
    }

    public void initialiseOutputColumnsCollection(int numOutputColumns) {
        if(outputCells == null) {
            outputCells = new TableCell[numRows][numOutputColumns];
            this.numOutputColumns = numOutputColumns;
        }
    }

    public void add(TableCell cell) {
        cell.addToCells(cells);
    }

    public void addOutputCell(TableCell outputCell) {
        outputCell.addToOutputCells(outputCells);
    }

    public Map<String, String> createFEELSourceClasses() {
        Map<String, String> allGeneratedTestClasses = new HashMap<>();
        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < numColumns; columnIndex++) {
                cells[rowIndex][columnIndex].compileUnaryTestAndAddTo(allGeneratedTestClasses);
            }

            // Generate output cells
            for (int columnIndex = 0; columnIndex < numOutputColumns; columnIndex++) {
                outputCells[rowIndex][columnIndex].compiledFeelExpressionAndAddTo(allGeneratedTestClasses);
            }
        }
        return allGeneratedTestClasses;
    }

    public void addAlphaNetworkNode(BlockStmt alphaNetworkStatements, GeneratedSources generatedSources) {

        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {

            CompilationUnit alphaNetworkCreationCU = parseJavaClassTemplateFromResources(this.getClass(),
                                                                                         "/org/kie/dmn/core/alphasupport/AlphaNodeCreationTemplate.java");
            String methodName = String.format("AlphaNodeCreation%s", rowIndex);

            ClassOrInterfaceDeclaration alphaNodeCreationClass = alphaNetworkCreationCU.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow(RuntimeException::new);
            alphaNodeCreationClass.removeComment();

            replaceSimpleNameWith(alphaNodeCreationClass, "AlphaNodeCreationTemplate", methodName);

            ConstructorDeclaration constructorDeclaration = alphaNodeCreationClass.findFirst(ConstructorDeclaration.class).orElseThrow(RuntimeException::new);

            MethodDeclaration testMethodDefinitionTemplate = findMethodTemplate(alphaNodeCreationClass, "testRxCx");

            BlockStmt creationStatements = constructorDeclaration.getBody();
            String lastAlphaNodeName = "";
            for (int columnIndex = 0; columnIndex < numColumns; columnIndex++) {
                TableCell tableCell = cells[rowIndex][columnIndex];
                lastAlphaNodeName = tableCell.addNodeCreation(creationStatements, alphaNodeCreationClass, testMethodDefinitionTemplate);
            }

            MethodDeclaration outputMethodDefinitionTemplate = findMethodTemplate(alphaNodeCreationClass, "outputRxCx");
            for (int outputColumnIndex = 0; outputColumnIndex < numOutputColumns; outputColumnIndex++) {
                MethodDeclaration outputMethodDefinitionClone = outputMethodDefinitionTemplate.clone();
                TableCell tableOutputCell = outputCells[rowIndex][outputColumnIndex];
                tableOutputCell.addOutputNode(alphaNodeCreationClass, outputMethodDefinitionClone, creationStatements, lastAlphaNodeName);
            }
            outputMethodDefinitionTemplate.remove();


            String classNameWithPackage = TableCell.ALPHANETWORK_STATIC_PACKAGE + "." + methodName;
            generatedSources.addNewSourceClass(classNameWithPackage, alphaNetworkCreationCU.toString());

            String newAlphaNetworkClass = String.format(
                    "new %s(builderContext)", classNameWithPackage
            );


            alphaNetworkStatements.addStatement(StaticJavaParser.parseExpression(newAlphaNetworkClass));

        }
    }

    public void addColumnValidationStatements(BlockStmt validationStatements, GeneratedSources allGeneratedSources) {
        for (int columnIndex = 0; columnIndex < numColumns; columnIndex++) {

            ColumnDefinition column = columns[columnIndex];
            final Map<String, String> validatorGeneratedClasses = new HashMap<>();
            column.compileUnaryTestAndAddTo(validatorGeneratedClasses);

            if(validatorGeneratedClasses.size() > 0) {
                CompilationUnit columnValidatorTemplate = parseJavaClassTemplateFromResources(this.getClass(),
                                                                                              "/org/kie/dmn/core/alphasupport/ColumnValidatorTemplate.java");
                columnValidatorTemplate.removeComment();

                column.initColumnValidatorTemplateAddToClasses(columnValidatorTemplate, validatorGeneratedClasses);
                allGeneratedSources.putAllGeneratedFEELTestClasses(validatorGeneratedClasses);

                BlockStmt validationStatementsParent = (BlockStmt) validationStatements.getParentNode().orElseThrow(RuntimeException::new);
                BlockStmt newValidationStatement = validationStatements.clone();
                column.initValidationStatement(newValidationStatement);

                // Last statement is `return null` so don't put this at the very end
                validationStatementsParent.addStatement(validationStatementsParent.getStatements().size() - 1, newValidationStatement);
            }
        }
    }

    public void addColumnCell(int index, ColumnDefinition columnDefinition) {
        columns[index] = columnDefinition;
    }
}