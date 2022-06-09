package org.optaplanner.core.impl.testdata.domain.pinned;

import org.optaplanner.core.api.domain.entity.PinningFilter;

public class TestdataPinningFilter implements PinningFilter<TestdataPinnedSolution, TestdataPinnedEntity> {

    @Override
    public boolean accept(TestdataPinnedSolution solution, TestdataPinnedEntity entity) {
        return entity.isLocked();
    }

}
