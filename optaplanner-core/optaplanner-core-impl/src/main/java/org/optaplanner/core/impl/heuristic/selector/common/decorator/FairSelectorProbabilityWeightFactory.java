package org.optaplanner.core.impl.heuristic.selector.common.decorator;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.selector.IterableSelector;

public class FairSelectorProbabilityWeightFactory<Solution_>
        implements SelectionProbabilityWeightFactory<Solution_, IterableSelector> {

    @Override
    public double createProbabilityWeight(ScoreDirector<Solution_> scoreDirector, IterableSelector selector) {
        return selector.getSize();
    }

}
