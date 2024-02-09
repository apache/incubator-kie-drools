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

import java.util.List;
import java.util.Optional;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.model.api.DecisionRule;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.InputClause;
import org.kie.dmn.model.api.LiteralExpression;

import static org.kie.dmn.feel.lang.types.BuiltInType.determineTypeFromName;

public class TableCellParser {
    TableCell.TableCellFactory tableCellFactory;

    public TableCellParser(TableCell.TableCellFactory tableCellFactory) {
        this.tableCellFactory = tableCellFactory;
    }

    public TableCells parseCells(DecisionTable decisionTable, DTQNameToTypeResolver resolver, String decisionTableName) {
        List<DecisionRule> rows = decisionTable.getRule();
        List<InputClause> columns = decisionTable.getInput();
        TableCells tableCells = new TableCells(rows.size(), columns.size());

        parseColumnDefinition(decisionTableName, columns, tableCells);

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            DecisionRule row = rows.get(rowIndex);

            for (int inputColumnIndex = 0; inputColumnIndex < row.getInputEntry().size(); inputColumnIndex++) {
                String input = row.getInputEntry().get(inputColumnIndex).getText();
                TableIndex tableIndex = new TableIndex(rowIndex, inputColumnIndex);
                InputClause column = tableIndex.getColumn(columns);
                final String columnName = column.getInputExpression().getText();
                final Type columnType = resolver.resolve(column.getInputExpression().getTypeRef());
                TableCell cell = tableCellFactory.createInputCell(tableIndex,
                                                                  input,
                                                                  columnName,
                                                                  columnType);


                tableCells.add(cell);

                if (inputColumnIndex == row.getInputEntry().size() - 1) { // last column
                    tableCells.initialiseOutputColumnsCollection(row.getOutputEntry().size());
                    List<LiteralExpression> outputEntry = row.getOutputEntry();
                    for (int outputColumnIndex = 0; outputColumnIndex < outputEntry.size(); outputColumnIndex++) {

                        TableIndex outputColumnTableIndex = tableIndex.outputTableIndex(outputColumnIndex);

                        LiteralExpression outputExpression = outputEntry.get(outputColumnIndex);
                        String outputRawText = outputExpression.getText();

                        String outputColumnName = Optional.ofNullable(decisionTable.getOutput().get(outputColumnIndex).getName()).orElse("");

                        TableCell outputCell = tableCellFactory.createOutputCell(outputColumnTableIndex, outputRawText, outputColumnName, columnType);
                        tableCells.addOutputCell(outputCell);
                    }
                }
            }
        }
        return tableCells;
    }

    private void parseColumnDefinition(String decisionTableName, List<InputClause> columns, TableCells tableCells) {
        for (int columnIndex = 0, columnsSize = columns.size(); columnIndex < columnsSize; columnIndex++) {
            InputClause column = columns.get(columnIndex);

            Type type = determineTypeFromName(column.getInputExpression().getTypeRef() != null ? column.getInputExpression().getTypeRef().getLocalPart() : null);

            tableCells.addColumnCell(columnIndex, tableCellFactory.createColumnDefinition(columnIndex,
                                                                       decisionTableName,
                                                                       column.getInputExpression().getText(),
                                                                       column.getInputValues(),
                                                                                          type));
        }
    }

    public DMNType parseDMNType(DMNModelImpl model, LiteralExpression inputExpression) {
        if (inputExpression.getTypeRef() != null) {
            String exprTypeRefNS = inputExpression.getTypeRef().getNamespaceURI();
            if (exprTypeRefNS == null || exprTypeRefNS.isEmpty()) {
                exprTypeRefNS = model.getNamespace();
            }
            return model.getTypeRegistry().resolveType(exprTypeRefNS, inputExpression.getTypeRef().getLocalPart());
        } else {
            return null;
        }
    }
}
