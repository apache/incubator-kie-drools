package org.optaplanner.core.impl.testdata.domain.pinned.chained;

import org.optaplanner.core.api.domain.entity.PinningFilter;

public class TestdataChainedEntityPinningFilter
        implements PinningFilter<TestdataPinnedChainedSolution, TestdataPinnedChainedEntity> {

    @Override
    public boolean accept(TestdataPinnedChainedSolution scoreDirector, TestdataPinnedChainedEntity entity) {
        return entity.isPinned();
    }

}
