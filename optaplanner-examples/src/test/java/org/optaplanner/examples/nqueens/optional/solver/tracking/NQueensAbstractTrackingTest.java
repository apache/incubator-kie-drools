package org.optaplanner.examples.nqueens.optional.solver.tracking;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

public abstract class NQueensAbstractTrackingTest {

    protected void assertTrackingList(List<NQueensStepTracking> expected, List<NQueensStepTracking> recorded) {
        for (int i = 0; i < expected.size(); i++) {
            assertThat(recorded.get(i).getColumnIndex()).isEqualTo(expected.get(i).getColumnIndex());
            assertThat(recorded.get(i).getRowIndex()).isEqualTo(expected.get(i).getRowIndex());
        }
    }

}
