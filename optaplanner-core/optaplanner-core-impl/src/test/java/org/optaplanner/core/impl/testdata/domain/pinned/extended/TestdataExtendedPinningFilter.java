package org.optaplanner.core.impl.testdata.domain.pinned.extended;

import org.optaplanner.core.api.domain.entity.PinningFilter;

public class TestdataExtendedPinningFilter
        implements PinningFilter<TestdataExtendedPinnedSolution, TestdataExtendedPinnedEntity> {

    @Override
    public boolean accept(TestdataExtendedPinnedSolution solution, TestdataExtendedPinnedEntity entity) {
        return entity.isClosed();
    }

}
