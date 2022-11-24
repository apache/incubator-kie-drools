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

public class NQueensGenerator extends LoggingMain {

    public static void main(String[] args) {
        NQueensGenerator generator = new NQueensGenerator();
        generator.writeNQueens(4);
        generator.writeNQueens(8);
        generator.writeNQueens(16);
        generator.writeNQueens(32);
        generator.writeNQueens(64);
        generator.writeNQueens(256);
    }

    protected final SolutionFileIO<NQueens> solutionFileIO;
    protected final File outputDir;

    public NQueensGenerator() {
        solutionFileIO = new NQueensSolutionFileIO();
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
        String outputFileName = n + "queens.json";
        File outputFile = new File(outputDir, outputFileName);
        NQueens nQueens = createNQueens(n);
        solutionFileIO.write(nQueens, outputFile);
        logger.info("Saved: {}", outputFile);
    }

    public NQueens createNQueens(int n) {
        NQueens nQueens = new NQueens(0L);
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
            Column column = new Column(i);
            columnList.add(column);
        }
        return columnList;
    }

    private List<Row> createRowList(NQueens nQueens) {
        int n = nQueens.getN();
        List<Row> rowList = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            Row row = new Row(i);
            rowList.add(row);
        }
        return rowList;
    }

    private List<Queen> createQueenList(NQueens nQueens) {
        int n = nQueens.getN();
        List<Queen> queenList = new ArrayList<>(n);
        long id = 0;
        for (Column column : nQueens.getColumnList()) {
            Queen queen = new Queen(id);
            id++;
            queen.setColumn(column);
            // Notice that we leave the PlanningVariable properties on null
            queenList.add(queen);
        }
        return queenList;
    }

}
