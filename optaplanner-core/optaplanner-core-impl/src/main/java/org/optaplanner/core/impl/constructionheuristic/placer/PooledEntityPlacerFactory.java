package org.optaplanner.core.impl.constructionheuristic.placer;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.config.constructionheuristic.placer.PooledEntityPlacerConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.CartesianProductMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.impl.AbstractFromConfigFactory;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelectorFactory;

public class PooledEntityPlacerFactory<Solution_>
        extends AbstractEntityPlacerFactory<Solution_, PooledEntityPlacerConfig> {

    public static <Solution_> PooledEntityPlacerConfig unfoldNew(HeuristicConfigPolicy<Solution_> configPolicy,
            MoveSelectorConfig templateMoveSelectorConfig) {
        PooledEntityPlacerConfig config = new PooledEntityPlacerConfig();
        List<MoveSelectorConfig> leafMoveSelectorConfigList = new ArrayList<>();
        MoveSelectorConfig moveSelectorConfig = (MoveSelectorConfig) templateMoveSelectorConfig.copyConfig();
        moveSelectorConfig.extractLeafMoveSelectorConfigsIntoList(leafMoveSelectorConfigList);
        config.setMoveSelectorConfig(moveSelectorConfig);

        EntitySelectorConfig entitySelectorConfig = null;
        for (MoveSelectorConfig leafMoveSelectorConfig : leafMoveSelectorConfigList) {
            if (!(leafMoveSelectorConfig instanceof ChangeMoveSelectorConfig)) {
                throw new IllegalStateException("The <constructionHeuristic> contains a moveSelector ("
                        + leafMoveSelectorConfig + ") that isn't a <changeMoveSelector>, a <unionMoveSelector>"
                        + " or a <cartesianProductMoveSelector>.\n"
                        + "Maybe you're using a moveSelector in <constructionHeuristic>"
                        + " that's only supported for <localSearch>.");
            }
            ChangeMoveSelectorConfig changeMoveSelectorConfig =
                    (ChangeMoveSelectorConfig) leafMoveSelectorConfig;
            if (changeMoveSelectorConfig.getEntitySelectorConfig() != null) {
                throw new IllegalStateException("The <constructionHeuristic> contains a changeMoveSelector ("
                        + changeMoveSelectorConfig + ") that contains an entitySelector ("
                        + changeMoveSelectorConfig.getEntitySelectorConfig()
                        + ") without explicitly configuring the <pooledEntityPlacer>.");
            }
            if (entitySelectorConfig == null) {
                EntityDescriptor<Solution_> entityDescriptor = new PooledEntityPlacerFactory<Solution_>(config)
                        .deduceEntityDescriptor(configPolicy.getSolutionDescriptor());
                entitySelectorConfig =
                        AbstractFromConfigFactory.getDefaultEntitySelectorConfigForEntity(configPolicy, entityDescriptor);
                changeMoveSelectorConfig.setEntitySelectorConfig(entitySelectorConfig);
            }
            changeMoveSelectorConfig.setEntitySelectorConfig(
                    EntitySelectorConfig.newMimicSelectorConfig(entitySelectorConfig.getId()));
        }
        return config;
    }

    public PooledEntityPlacerFactory(PooledEntityPlacerConfig placerConfig) {
        super(placerConfig);
    }

    @Override
    public PooledEntityPlacer<Solution_> buildEntityPlacer(HeuristicConfigPolicy<Solution_> configPolicy) {
        MoveSelectorConfig moveSelectorConfig_ =
                config.getMoveSelectorConfig() == null ? buildMoveSelectorConfig(configPolicy) : config.getMoveSelectorConfig();

        MoveSelector<Solution_> moveSelector = MoveSelectorFactory.<Solution_> create(moveSelectorConfig_)
                .buildMoveSelector(configPolicy, SelectionCacheType.JUST_IN_TIME, SelectionOrder.ORIGINAL);
        return new PooledEntityPlacer<>(moveSelector);
    }

    private MoveSelectorConfig buildMoveSelectorConfig(HeuristicConfigPolicy<Solution_> configPolicy) {
        EntityDescriptor<Solution_> entityDescriptor = deduceEntityDescriptor(configPolicy.getSolutionDescriptor());
        EntitySelectorConfig entitySelectorConfig =
                AbstractFromConfigFactory.getDefaultEntitySelectorConfigForEntity(configPolicy, entityDescriptor);

        List<GenuineVariableDescriptor<Solution_>> variableDescriptors = entityDescriptor.getGenuineVariableDescriptorList();
        List<MoveSelectorConfig> subMoveSelectorConfigList = new ArrayList<>(variableDescriptors.size());
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : variableDescriptors) {
            subMoveSelectorConfigList
                    .add(buildChangeMoveSelectorConfig(configPolicy, entitySelectorConfig.getId(), variableDescriptor));
        }
        // The first entitySelectorConfig must be the mimic recorder, not the mimic replayer
        ((ChangeMoveSelectorConfig) subMoveSelectorConfigList.get(0)).setEntitySelectorConfig(entitySelectorConfig);
        MoveSelectorConfig moveSelectorConfig_;
        if (subMoveSelectorConfigList.size() > 1) {
            // Default to cartesian product (not a union) of planning variables.
            moveSelectorConfig_ = new CartesianProductMoveSelectorConfig(subMoveSelectorConfigList);
        } else {
            moveSelectorConfig_ = subMoveSelectorConfigList.get(0);
        }
        return moveSelectorConfig_;
    }
}
