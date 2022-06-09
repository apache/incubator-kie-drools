package org.optaplanner.examples.nqueens.score;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.examples.nqueens.domain.Column;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.domain.Queen;
import org.optaplanner.examples.nqueens.domain.Row;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

class NQueensConstraintProviderTest {

    private final ConstraintVerifier<NQueensConstraintProvider, NQueens> constraintVerifier = ConstraintVerifier
            .build(new NQueensConstraintProvider(), NQueens.class, Queen.class);

    private final Row row1 = new Row(0);
    private final Row row2 = new Row(1);
    private final Row row3 = new Row(2);
    private final Column column1 = new Column(0);
    private final Column column2 = new Column(1);
    private final Column column3 = new Column(2);

    @Test
    void noHorizontalConflictWithOneQueen() {
        Queen queen1 = new Queen(0, row1, column1);
        constraintVerifier.verifyThat(NQueensConstraintProvider::horizontalConflict)
                .given(queen1)
                .penalizesBy(0);
    }

    @Test
    void horizontalConflictWithTwoQueens() {
        Queen queen1 = new Queen(0, row1, column1);
        Queen queen2 = new Queen(1, row1, column2);
        constraintVerifier.verifyThat(NQueensConstraintProvider::horizontalConflict)
                .given(queen1, queen2)
                .penalizesBy(1);
    }

    @Test
    void horizontalConflictWithThreeQueens() {
        Queen queen1 = new Queen(0, row1, column1);
        Queen queen2 = new Queen(1, row1, column2);
        Queen queen3 = new Queen(2, row1, column3);
        constraintVerifier.verifyThat(NQueensConstraintProvider::horizontalConflict)
                .given(queen1, queen2, queen3)
                .penalizesBy(3);
    }

    @Test
    void noAscendingDiagonalConflictWithOneQueen() {
        Queen queen1 = new Queen(0, row1, column1);
        constraintVerifier.verifyThat(NQueensConstraintProvider::ascendingDiagonalConflict)
                .given(queen1)
                .penalizesBy(0);
    }

    @Test
    void ascendingDiagonalConflictWithTwoQueens() {
        Queen queen1 = new Queen(0, row1, column2);
        Queen queen2 = new Queen(1, row2, column1);
        constraintVerifier.verifyThat(NQueensConstraintProvider::ascendingDiagonalConflict)
                .given(queen1, queen2)
                .penalizesBy(1);
    }

    @Test
    void ascendingDiagonalConflictWithThreeQueens() {
        Queen queen1 = new Queen(0, row1, column3);
        Queen queen2 = new Queen(1, row2, column2);
        Queen queen3 = new Queen(2, row3, column1);
        constraintVerifier.verifyThat(NQueensConstraintProvider::ascendingDiagonalConflict)
                .given(queen1, queen2, queen3)
                .penalizesBy(3);
    }

    @Test
    void noDescendingDiagonalConflictWithOneQueen() {
        Queen queen1 = new Queen(0, row1, column1);
        constraintVerifier.verifyThat(NQueensConstraintProvider::descendingDiagonalConflict)
                .given(queen1)
                .penalizesBy(0);
    }

    @Test
    void descendingDiagonalConflictWithTwoQueens() {
        Queen queen1 = new Queen(0, row1, column1);
        Queen queen2 = new Queen(1, row2, column2);
        constraintVerifier.verifyThat(NQueensConstraintProvider::descendingDiagonalConflict)
                .given(queen1, queen2)
                .penalizesBy(1);
    }

    @Test
    void descendingDiagonalConflictWithThreeQueens() {
        Queen queen1 = new Queen(0, row1, column1);
        Queen queen2 = new Queen(1, row2, column2);
        Queen queen3 = new Queen(2, row3, column3);
        constraintVerifier.verifyThat(NQueensConstraintProvider::descendingDiagonalConflict)
                .given(queen1, queen2, queen3)
                .penalizesBy(3);
    }

    private static NQueens readSolution(String resource) throws IOException {
        XStreamSolutionFileIO<NQueens> solutionFileIO = new XStreamSolutionFileIO<>(NQueens.class);
        try (InputStream inputStream = NQueensConstraintProviderTest.class.getResourceAsStream(resource)) {
            return solutionFileIO.read(inputStream);
        }
    }

    @Test
    void givenSolutionMultipleConstraints() throws IOException {
        constraintVerifier.verifyThat()
                .givenSolution(readSolution("256queensScore-30.xml"))
                .scores(SimpleScore.of(-30));
    }

    @Test
    void givenFactsMultipleConstraints() {
        Queen queen1 = new Queen(0, row1, column1);
        Queen queen2 = new Queen(1, row2, column2);
        Queen queen3 = new Queen(2, row3, column3);
        constraintVerifier.verifyThat()
                .given(queen1, queen2, queen3)
                .scores(SimpleScore.of(-3));
    }

}
