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

import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.ObjectTypeNode;

import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.parseJavaClassTemplateFromResources;

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
                cells[rowIndex][columnIndex].crateUnaryTestAndAddTo(allGeneratedTestClasses);
            }

            // Generate output cells
            for (int columnIndex = 0; columnIndex < numOutputColumns; columnIndex++) {
                outputCells[rowIndex][columnIndex].compiledFeelExpressionAndAddTo(allGeneratedTestClasses);
            }
        }
        return allGeneratedTestClasses;
    }

    public ObjectTypeNode createRete(ReteBuilderContext reteBuilderContext) {
        AlphaNetworkCreation alphaNetworkCreation = new AlphaNetworkCreation(reteBuilderContext.buildContext);

        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {

            AlphaNode lastAlphaNodeCreated = null;
            for (int columnIndex = 0; columnIndex < numColumns; columnIndex++) {
                TableCell tableCell = cells[rowIndex][columnIndex];
                lastAlphaNodeCreated = tableCell.createAlphaNode(alphaNetworkCreation, reteBuilderContext, lastAlphaNodeCreated);
            }

            for (int outputColumnIndex = 0; outputColumnIndex < numOutputColumns; outputColumnIndex++) {
                TableCell tableOutputCell = outputCells[rowIndex][outputColumnIndex];
                tableOutputCell.addOutputNode(alphaNetworkCreation, lastAlphaNodeCreated);
            }

        }
        return reteBuilderContext.otn;
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