package org.optaplanner.core.impl.constructionheuristic.decider.forager;

import org.optaplanner.core.config.constructionheuristic.decider.forager.ConstructionHeuristicForagerConfig;
import org.optaplanner.core.config.constructionheuristic.decider.forager.ConstructionHeuristicPickEarlyType;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;

public class ConstructionHeuristicForagerFactory<Solution_> {

    public static <Solution_> ConstructionHeuristicForagerFactory<Solution_> create(
            ConstructionHeuristicForagerConfig foragerConfig) {
        return new ConstructionHeuristicForagerFactory<>(foragerConfig);
    }

    private final ConstructionHeuristicForagerConfig foragerConfig;

    public ConstructionHeuristicForagerFactory(ConstructionHeuristicForagerConfig foragerConfig) {
        this.foragerConfig = foragerConfig;
    }

    public ConstructionHeuristicForager<Solution_> buildForager(HeuristicConfigPolicy<Solution_> configPolicy) {
        ConstructionHeuristicPickEarlyType pickEarlyType_;
        if (foragerConfig.getPickEarlyType() == null) {
            pickEarlyType_ = configPolicy.getScoreDirectorFactory().getInitializingScoreTrend().isOnlyDown()
                    ? ConstructionHeuristicPickEarlyType.FIRST_NON_DETERIORATING_SCORE
                    : ConstructionHeuristicPickEarlyType.NEVER;
        } else {
            pickEarlyType_ = foragerConfig.getPickEarlyType();
        }
        return new DefaultConstructionHeuristicForager<>(pickEarlyType_);
    }
}
