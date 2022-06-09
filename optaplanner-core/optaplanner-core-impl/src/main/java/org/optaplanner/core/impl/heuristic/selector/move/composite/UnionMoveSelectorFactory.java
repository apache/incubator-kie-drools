package org.optaplanner.core.impl.heuristic.selector.move.composite;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class UnionMoveSelectorFactory<Solution_>
        extends AbstractCompositeMoveSelectorFactory<Solution_, UnionMoveSelectorConfig> {

    public UnionMoveSelectorFactory(UnionMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    public MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        List<MoveSelector<Solution_>> moveSelectorList = buildInnerMoveSelectors(config.getMoveSelectorList(),
                configPolicy, minimumCacheType, randomSelection);

        SelectionProbabilityWeightFactory<Solution_, MoveSelector<Solution_>> selectorProbabilityWeightFactory;
        if (config.getSelectorProbabilityWeightFactoryClass() != null) {
            if (!randomSelection) {
                throw new IllegalArgumentException("The moveSelectorConfig (" + config
                        + ") with selectorProbabilityWeightFactoryClass ("
                        + config.getSelectorProbabilityWeightFactoryClass()
                        + ") has non-random randomSelection (" + randomSelection + ").");
            }
            selectorProbabilityWeightFactory = ConfigUtils.newInstance(config,
                    "selectorProbabilityWeightFactoryClass", config.getSelectorProbabilityWeightFactoryClass());
        } else if (randomSelection) {
            Map<MoveSelector<Solution_>, Double> fixedProbabilityWeightMap =
                    new HashMap<>(config.getMoveSelectorList().size());
            for (int i = 0; i < config.getMoveSelectorList().size(); i++) {
                MoveSelectorConfig<?> innerMoveSelectorConfig = config.getMoveSelectorList().get(i);
                MoveSelector<Solution_> moveSelector = moveSelectorList.get(i);
                Double fixedProbabilityWeight = innerMoveSelectorConfig.getFixedProbabilityWeight();
                if (fixedProbabilityWeight != null) {
                    fixedProbabilityWeightMap.put(moveSelector, fixedProbabilityWeight);
                }
            }
            if (fixedProbabilityWeightMap.isEmpty()) { // Will end up using UniformRandomUnionMoveIterator.
                selectorProbabilityWeightFactory = null;
            } else { // Will end up using BiasedRandomUnionMoveIterator.
                selectorProbabilityWeightFactory = new FixedSelectorProbabilityWeightFactory<>(fixedProbabilityWeightMap);
            }
        } else {
            selectorProbabilityWeightFactory = null;
        }
        return new UnionMoveSelector<>(moveSelectorList, randomSelection, selectorProbabilityWeightFactory);
    }
}
