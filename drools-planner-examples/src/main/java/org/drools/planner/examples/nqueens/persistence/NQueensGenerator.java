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

package org.drools.planner.examples.nqueens.persistence;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.drools.planner.examples.common.app.LoggingMain;
import org.drools.planner.examples.common.persistence.SolutionDao;
import org.drools.planner.examples.nqueens.domain.Column;
import org.drools.planner.examples.nqueens.domain.NQueens;
import org.drools.planner.examples.nqueens.domain.Queen;
import org.drools.planner.examples.nqueens.domain.Row;

public class NQueensGenerator extends LoggingMain {

    private static final File outputDir = new File("data/nqueens/unsolved/");

    protected SolutionDao solutionDao;

    public static void main(String[] args) {
        new NQueensGenerator().generate();
    }

    public void generate() {
        solutionDao = new NQueensDaoImpl();
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
        List<Column> columnList = new ArrayList<Column>(n);
        for (int i = 0; i < n; i++) {
            Column column = new Column();
            column.setId((long) i);
            column.setIndex(i);
            columnList.add(column);
        }
        nQueens.setColumnList(columnList);
        List<Row> rowList = new ArrayList<Row>(n);
        for (int i = 0; i < n; i++) {
            Row row = new Row();
            row.setId((long) i);
            row.setIndex(i);
            rowList.add(row);
        }
        nQueens.setRowList(rowList);
        List<Queen> queenList = new ArrayList<Queen>(n);
        long id = 0;
        for (Column column : columnList) {
            Queen queen = new Queen();
            queen.setId(id);
            id++;
            queen.setColumn(column);
            // Notice that we leave the PlanningVariable properties on null
            queenList.add(queen);
        }
        nQueens.setQueenList(queenList);
        logger.info("NQueens with {} queens.", nQueens.getN());
        BigInteger possibleSolutionSize = BigInteger.valueOf(nQueens.getN()).pow(nQueens.getN());
        String flooredPossibleSolutionSize = "10^" + (possibleSolutionSize.toString().length() - 1);
        logger.info("NQueens with flooredPossibleSolutionSize ({}) and possibleSolutionSize ({}).",
                flooredPossibleSolutionSize, possibleSolutionSize);
        return nQueens;
    }

}
