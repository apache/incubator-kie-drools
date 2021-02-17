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

import java.util.List;

import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.model.api.DecisionRule;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.InputClause;

public class TableCellParser {
    TableCell.TableCellFactory tableCellFactory;

    public TableCellParser(TableCell.TableCellFactory tableCellFactory) {
        this.tableCellFactory = tableCellFactory;
    }

    public TableCells parseCells(DecisionTable decisionTable, DTQNameToTypeResolver resolver) {
        List<DecisionRule> rows = decisionTable.getRule();
        List<InputClause> columns = decisionTable.getInput();
        TableCells tableCells = new TableCells(rows.size(), columns.size());

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
                if (inputColumnIndex == row.getInputEntry().size() - 1) { // last column
                    cell.setOutput(row.getOutputEntry().get(0).getText()); // assume only one output
                }

                tableCells.add(cell);
            }


        }
        return tableCells;
    }
}
