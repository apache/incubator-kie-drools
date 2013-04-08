/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.examples.nqueens.persistence;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.nqueens.domain.Column;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.domain.Queen;
import org.optaplanner.examples.nqueens.domain.Row;

public class NQueensGenerator extends LoggingMain {

    private static final File outputDir = new File("data/nqueens/unsolved/");

    protected SolutionDao solutionDao;

    public static void main(String[] args) {
        new NQueensGenerator().generate();
    }

    public void generate() {
        solutionDao = new NQueensDao();
        writeNQueens(4);
        writeNQueens(8);
        writeNQueens(16);
        writeNQueens(32);
        writeNQueens(64);
        writeNQueens(256);
    }

    private void writeNQueens(int n) {
        String outputFileName = "unsolvedNQueens" + (n < 10 ? "0" : "") + n + ".xml";
        File outputFile = new File(outputDir, outputFileName);
        NQueens nQueens = createNQueens(n);
        solutionDao.writeSolution(nQueens, outputFile);
    }

    public NQueens createNQueens(int n) {
        NQueens nQueens = new NQueens();
        nQueens.setId(0L);
        nQueens.setN(n);
        nQueens.setColumnList(createColumnList(nQueens));
        nQueens.setRowList(createRowList(nQueens));
        nQueens.setQueenList(createQueenList(nQueens));
        BigInteger possibleSolutionSize = BigInteger.valueOf(nQueens.getN()).pow(nQueens.getN());
        String flooredPossibleSolutionSize = "10^" + (possibleSolutionSize.toString().length() - 1);
        logger.info("NQueens {} has {} queens with a search space of {}.",
                n, nQueens.getN(),
                possibleSolutionSize.compareTo(BigInteger.valueOf(1000L)) < 0
                ? possibleSolutionSize : flooredPossibleSolutionSize);
        return nQueens;
    }

    private List<Column> createColumnList(NQueens nQueens) {
        int n = nQueens.getN();
        List<Column> columnList = new ArrayList<Column>(n);
        for (int i = 0; i < n; i++) {
            Column column = new Column();
            column.setId((long) i);
            column.setIndex(i);
            columnList.add(column);
        }
        return columnList;
    }

    private List<Row> createRowList(NQueens nQueens) {
        int n = nQueens.getN();
        List<Row> rowList = new ArrayList<Row>(n);
        for (int i = 0; i < n; i++) {
            Row row = new Row();
            row.setId((long) i);
            row.setIndex(i);
            rowList.add(row);
        }
        return rowList;
    }

    private List<Queen> createQueenList(NQueens nQueens) {
        int n = nQueens.getN();
        List<Queen> queenList = new ArrayList<Queen>(n);
        long id = 0;
        for (Column column : nQueens.getColumnList()) {
            Queen queen = new Queen();
            queen.setId(id);
            id++;
            queen.setColumn(column);
            // Notice that we leave the PlanningVariable properties on null
            queenList.add(queen);
        }
        return queenList;
    }

}
