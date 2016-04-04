package org.optaplanner.examples.nqueens.solver.tracking;

import java.util.List;

import static org.junit.Assert.*;

public abstract class NQueensAbstractTrackingTest {

    protected void assertTrackingList(List<NQueensStepTracking> expected, List<NQueensStepTracking> recorded) {
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getColumnIndex(), recorded.get(i).getColumnIndex());
            assertEquals(expected.get(i).getRowIndex(), recorded.get(i).getRowIndex());
        }
    }

}
