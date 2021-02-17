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

import org.kie.dmn.model.api.InputClause;

public class TableIndex {

    private final int row;
    private final int column;

    public TableIndex(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public TableIndex previousColumn() {
        return new TableIndex(row, column - 1);
    }

    public String appendTableIndexSuffix(String sourceString) {
        // DMN DTable are 1Based
        return String.format("%sR%sC%s", sourceString, row + 1, column + 1);
    }

    public InputClause getColumn(List<InputClause> columns) {
        return columns.get(column);
    }

    public boolean isFirstColumn() {
        return column == 0;
    }

    public int columnIndex() {
        return column;
    }

    public void addToCells(TableCell[][] cells, TableCell tableCell) {
        cells[row][column] = tableCell;
    }

}
