/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.nqueens.app.NQueensApp;
import org.optaplanner.examples.nqueens.domain.Column;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.domain.Queen;
import org.optaplanner.examples.nqueens.domain.Row;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class NQueensGenerator extends LoggingMain {

    public static void main(String[] args) {
        NQueensGenerator generator = new NQueensGenerator();
        generator.writeNQueens(4);
        //        generator.writeNQueens(5);
        //        generator.writeNQueens(6);
        //        generator.writeNQueens(7);
        generator.writeNQueens(8);
        //        generator.writeNQueens(9);
        //        generator.writeNQueens(10);
        //        generator.writeNQueens(11);
        //        generator.writeNQueens(12);
        //        generator.writeNQueens(13);
        //        generator.writeNQueens(14);
        //        generator.writeNQueens(15);
        generator.writeNQueens(16);
        //        generator.writeNQueens(17);
        //        generator.writeNQueens(18);
        //        generator.writeNQueens(19);
        //        generator.writeNQueens(20);
        //        generator.writeNQueens(21);
        //        generator.writeNQueens(22);
        //        generator.writeNQueens(23);
        //        generator.writeNQueens(24);
        //        generator.writeNQueens(25);
        //        generator.writeNQueens(26);
        //        generator.writeNQueens(27);
        //        generator.writeNQueens(28);
        //        generator.writeNQueens(29);
        //        generator.writeNQueens(30);
        //        generator.writeNQueens(31);
        generator.writeNQueens(32);
        generator.writeNQueens(64);
        generator.writeNQueens(256);
        //        generator.writeNQueens(10000);
        //        generator.writeNQueens(100000);
        //        generator.writeNQueens(1000000);
    }

    protected final SolutionFileIO<NQueens> solutionFileIO;
    protected final File outputDir;

    public NQueensGenerator() {
        solutionFileIO = new XStreamSolutionFileIO<>(NQueens.class);
        outputDir = new File(CommonApp.determineDataDir(NQueensApp.DATA_DIR_NAME), "unsolved");
    }

    public NQueensGenerator(boolean withoutDao) {
        if (!withoutDao) {
            throw new IllegalArgumentException("The parameter withoutDao (" + withoutDao + ") must be true.");
        }
        solutionFileIO = null;
        outputDir = null;
    }

    private void writeNQueens(int n) {
        String outputFileName = n + "queens.xml";
        File outputFile = new File(outputDir, outputFileName);
        NQueens nQueens = createNQueens(n);
        solutionFileIO.write(nQueens, outputFile);
        logger.info("Saved: {}", outputFile);
    }

    public NQueens createNQueens(int n) {
        NQueens nQueens = new NQueens();
        nQueens.setId(0L);
        nQueens.setN(n);
        nQueens.setColumnList(createColumnList(nQueens));
        nQueens.setRowList(createRowList(nQueens));
        nQueens.setQueenList(createQueenList(nQueens));
        BigInteger possibleSolutionSize = BigInteger.valueOf(nQueens.getN()).pow(nQueens.getN());
        logger.info("NQueens {} has {} queens with a search space of {}.",
                n, nQueens.getN(),
                AbstractSolutionImporter.getFlooredPossibleSolutionSize(possibleSolutionSize));
        return nQueens;
    }

    private List<Column> createColumnList(NQueens nQueens) {
        int n = nQueens.getN();
        List<Column> columnList = new ArrayList<>(n);
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
        List<Row> rowList = new ArrayList<>(n);
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
        List<Queen> queenList = new ArrayList<>(n);
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
