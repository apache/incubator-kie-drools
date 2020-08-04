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

import org.kie.dmn.core.compiler.execmodelbased.DTableModel;

public class TableCellParser {
    TableCell.TableCellFactory tableCellFactory;

    public TableCellParser(TableCell.TableCellFactory tableCellFactory) {
        this.tableCellFactory = tableCellFactory;
    }

    public TableCells parseCells(DTableModel dTableModel) {

        List<DTableModel.DRowModel> rows = dTableModel.getRows();
        List<DTableModel.DColumnModel> columns = dTableModel.getColumns();
        TableCells tableCells = new TableCells(rows.size(), columns.size());

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            DTableModel.DRowModel row = rows.get(rowIndex);

            for (int inputColumnIndex = 0; inputColumnIndex < row.getInputs().size(); inputColumnIndex++) {
                String input = row.getInputs().get(inputColumnIndex);
                TableIndex tableIndex = new TableIndex(rowIndex, inputColumnIndex);
                DTableModel.DColumnModel column = tableIndex.getColumn(columns);
                TableCell cell = tableCellFactory.createInputCell(tableIndex,
                                                                  input,
                                                                  column.getName(),
                                                                  column.getType());

                if(inputColumnIndex == row.getInputs().size() - 1) { // last column
                    cell.setOutput(row.getOutputs().get(0)); // assume only one output
                }

                tableCells.add(cell);
            }


        }
        return tableCells;
    }
}
