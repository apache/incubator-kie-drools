package org.optaplanner.examples.nqueens.score;

import java.io.IOException;
import java.io.InputStream;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.examples.common.score.AbstractConstraintProviderTest;
import org.optaplanner.examples.common.score.ConstraintProviderTest;
import org.optaplanner.examples.nqueens.domain.Column;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.domain.Queen;
import org.optaplanner.examples.nqueens.domain.Row;
import org.optaplanner.examples.nqueens.persistence.NQueensSolutionFileIO;
import org.optaplanner.persistence.jackson.impl.domain.solution.JacksonSolutionFileIO;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

class NQueensConstraintProviderTest
        extends AbstractConstraintProviderTest<NQueensConstraintProvider, NQueens> {

    private final Row row1 = new Row(0);
    private final Row row2 = new Row(1);
    private final Row row3 = new Row(2);
    private final Column column1 = new Column(0);
    private final Column column2 = new Column(1);
    private final Column column3 = new Column(2);

    @ConstraintProviderTest
    void noHorizontalConflictWithOneQueen(ConstraintVerifier<NQueensConstraintProvider, NQueens> constraintVerifier) {
        Queen queen1 = new Queen(0, row1, column1);
        constraintVerifier.verifyThat(NQueensConstraintProvider::horizontalConflict)
                .given(queen1)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void horizontalConflictWithTwoQueens(ConstraintVerifier<NQueensConstraintProvider, NQueens> constraintVerifier) {
        Queen queen1 = new Queen(0, row1, column1);
        Queen queen2 = new Queen(1, row1, column2);
        constraintVerifier.verifyThat(NQueensConstraintProvider::horizontalConflict)
                .given(queen1, queen2)
                .penalizesBy(1);
    }

    @ConstraintProviderTest
    void horizontalConflictWithThreeQueens(ConstraintVerifier<NQueensConstraintProvider, NQueens> constraintVerifier) {
        Queen queen1 = new Queen(0, row1, column1);
        Queen queen2 = new Queen(1, row1, column2);
        Queen queen3 = new Queen(2, row1, column3);
        constraintVerifier.verifyThat(NQueensConstraintProvider::horizontalConflict)
                .given(queen1, queen2, queen3)
                .penalizesBy(3);
    }

    @ConstraintProviderTest
    void noAscendingDiagonalConflictWithOneQueen(ConstraintVerifier<NQueensConstraintProvider, NQueens> constraintVerifier) {
        Queen queen1 = new Queen(0, row1, column1);
        constraintVerifier.verifyThat(NQueensConstraintProvider::ascendingDiagonalConflict)
                .given(queen1)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void ascendingDiagonalConflictWithTwoQueens(ConstraintVerifier<NQueensConstraintProvider, NQueens> constraintVerifier) {
        Queen queen1 = new Queen(0, row1, column2);
        Queen queen2 = new Queen(1, row2, column1);
        constraintVerifier.verifyThat(NQueensConstraintProvider::ascendingDiagonalConflict)
                .given(queen1, queen2)
                .penalizesBy(1);
    }

    @ConstraintProviderTest
    void ascendingDiagonalConflictWithThreeQueens(ConstraintVerifier<NQueensConstraintProvider, NQueens> constraintVerifier) {
        Queen queen1 = new Queen(0, row1, column3);
        Queen queen2 = new Queen(1, row2, column2);
        Queen queen3 = new Queen(2, row3, column1);
        constraintVerifier.verifyThat(NQueensConstraintProvider::ascendingDiagonalConflict)
                .given(queen1, queen2, queen3)
                .penalizesBy(3);
    }

    @ConstraintProviderTest
    void noDescendingDiagonalConflictWithOneQueen(ConstraintVerifier<NQueensConstraintProvider, NQueens> constraintVerifier) {
        Queen queen1 = new Queen(0, row1, column1);
        constraintVerifier.verifyThat(NQueensConstraintProvider::descendingDiagonalConflict)
                .given(queen1)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void descendingDiagonalConflictWithTwoQueens(ConstraintVerifier<NQueensConstraintProvider, NQueens> constraintVerifier) {
        Queen queen1 = new Queen(0, row1, column1);
        Queen queen2 = new Queen(1, row2, column2);
        constraintVerifier.verifyThat(NQueensConstraintProvider::descendingDiagonalConflict)
                .given(queen1, queen2)
                .penalizesBy(1);
    }

    @ConstraintProviderTest
    void descendingDiagonalConflictWithThreeQueens(ConstraintVerifier<NQueensConstraintProvider, NQueens> constraintVerifier) {
        Queen queen1 = new Queen(0, row1, column1);
        Queen queen2 = new Queen(1, row2, column2);
        Queen queen3 = new Queen(2, row3, column3);
        constraintVerifier.verifyThat(NQueensConstraintProvider::descendingDiagonalConflict)
                .given(queen1, queen2, queen3)
                .penalizesBy(3);
    }

    private static NQueens readSolution(String resource) throws IOException {
        JacksonSolutionFileIO<NQueens> solutionFileIO = new NQueensSolutionFileIO();
        try (InputStream inputStream = NQueensConstraintProviderTest.class.getResourceAsStream(resource)) {
            return solutionFileIO.read(inputStream);
        }
    }

    @ConstraintProviderTest
    void givenSolutionMultipleConstraints(ConstraintVerifier<NQueensConstraintProvider, NQueens> constraintVerifier)
            throws IOException {
        constraintVerifier.verifyThat()
                .givenSolution(readSolution("256queensScore-29.json"))
                .scores(SimpleScore.of(-29));
    }

    @ConstraintProviderTest
    void givenFactsMultipleConstraints(ConstraintVerifier<NQueensConstraintProvider, NQueens> constraintVerifier) {
        Queen queen1 = new Queen(0, row1, column1);
        Queen queen2 = new Queen(1, row2, column2);
        Queen queen3 = new Queen(2, row3, column3);
        constraintVerifier.verifyThat()
                .given(queen1, queen2, queen3)
                .scores(SimpleScore.of(-3));
    }

    @Override
    protected ConstraintVerifier<NQueensConstraintProvider, NQueens> createConstraintVerifier() {
        return ConstraintVerifier.build(new NQueensConstraintProvider(), NQueens.class, Queen.class);
    }
}
