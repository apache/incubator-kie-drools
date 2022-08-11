package org.optaplanner.core.impl.constructionheuristic.placer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.config.constructionheuristic.placer.QueuedEntityPlacerConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.CartesianProductMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelectorFactory;

public class QueuedEntityPlacerFactory<Solution_>
        extends AbstractEntityPlacerFactory<Solution_, QueuedEntityPlacerConfig> {

    public static <Solution_> QueuedEntityPlacerConfig unfoldNew(HeuristicConfigPolicy<Solution_> configPolicy,
            List<MoveSelectorConfig> templateMoveSelectorConfigList) {
        QueuedEntityPlacerConfig config = new QueuedEntityPlacerConfig();
        config.setEntitySelectorConfig(new QueuedEntityPlacerFactory<Solution_>(config)
                .buildEntitySelectorConfig(configPolicy));
        config.setMoveSelectorConfigList(new ArrayList<>(templateMoveSelectorConfigList.size()));
        List<MoveSelectorConfig> leafMoveSelectorConfigList = new ArrayList<>(templateMoveSelectorConfigList.size());
        for (MoveSelectorConfig templateMoveSelectorConfig : templateMoveSelectorConfigList) {
            MoveSelectorConfig moveSelectorConfig = (MoveSelectorConfig) templateMoveSelectorConfig.copyConfig();
            moveSelectorConfig.extractLeafMoveSelectorConfigsIntoList(leafMoveSelectorConfigList);
            config.getMoveSelectorConfigList().add(moveSelectorConfig);
        }
        for (MoveSelectorConfig leafMoveSelectorConfig : leafMoveSelectorConfigList) {
            if (!(leafMoveSelectorConfig instanceof ChangeMoveSelectorConfig)) {
                throw new IllegalStateException("The <constructionHeuristic> contains a moveSelector ("
                        + leafMoveSelectorConfig + ") that isn't a <changeMoveSelector>, a <unionMoveSelector>"
                        + " or a <cartesianProductMoveSelector>.\n"
                        + "Maybe you're using a moveSelector in <constructionHeuristic>"
                        + " that's only supported for <localSearch>.");
            }
            ChangeMoveSelectorConfig changeMoveSelectorConfig = (ChangeMoveSelectorConfig) leafMoveSelectorConfig;
            if (changeMoveSelectorConfig.getEntitySelectorConfig() != null) {
                throw new IllegalStateException("The <constructionHeuristic> contains a changeMoveSelector ("
                        + changeMoveSelectorConfig + ") that contains an entitySelector ("
                        + changeMoveSelectorConfig.getEntitySelectorConfig()
                        + ") without explicitly configuring the <queuedEntityPlacer>.");
            }
            changeMoveSelectorConfig.setEntitySelectorConfig(
                    EntitySelectorConfig.newMimicSelectorConfig(config.getEntitySelectorConfig().getId()));
        }
        return config;
    }

    public QueuedEntityPlacerFactory(QueuedEntityPlacerConfig placerConfig) {
        super(placerConfig);
    }

    @Override
    public QueuedEntityPlacer<Solution_> buildEntityPlacer(HeuristicConfigPolicy<Solution_> configPolicy) {
        EntitySelectorConfig entitySelectorConfig_ = buildEntitySelectorConfig(configPolicy);
        EntitySelector<Solution_> entitySelector = EntitySelectorFactory.<Solution_> create(entitySelectorConfig_)
                .buildEntitySelector(configPolicy, SelectionCacheType.PHASE, SelectionOrder.ORIGINAL);

        List<MoveSelectorConfig> moveSelectorConfigList_;
        if (ConfigUtils.isEmptyCollection(config.getMoveSelectorConfigList())) {
            EntityDescriptor<Solution_> entityDescriptor = entitySelector.getEntityDescriptor();
            List<GenuineVariableDescriptor<Solution_>> variableDescriptorList =
                    entityDescriptor.getGenuineVariableDescriptorList();
            List<MoveSelectorConfig> subMoveSelectorConfigList = new ArrayList<>(variableDescriptorList.size());
            for (GenuineVariableDescriptor<Solution_> variableDescriptor : variableDescriptorList) {
                subMoveSelectorConfigList
                        .add(buildChangeMoveSelectorConfig(configPolicy, entitySelectorConfig_.getId(), variableDescriptor));
            }
            MoveSelectorConfig subMoveSelectorConfig;
            if (subMoveSelectorConfigList.size() > 1) {
                // Default to cartesian product (not a queue) of planning variables.
                subMoveSelectorConfig = new CartesianProductMoveSelectorConfig(subMoveSelectorConfigList);
            } else {
                subMoveSelectorConfig = subMoveSelectorConfigList.get(0);
            }
            moveSelectorConfigList_ = Collections.singletonList(subMoveSelectorConfig);
        } else {
            moveSelectorConfigList_ = config.getMoveSelectorConfigList();
        }
        List<MoveSelector<Solution_>> moveSelectorList = new ArrayList<>(moveSelectorConfigList_.size());
        for (MoveSelectorConfig moveSelectorConfig : moveSelectorConfigList_) {
            MoveSelector<Solution_> moveSelector = MoveSelectorFactory.<Solution_> create(moveSelectorConfig)
                    .buildMoveSelector(configPolicy, SelectionCacheType.JUST_IN_TIME, SelectionOrder.ORIGINAL);
            moveSelectorList.add(moveSelector);
        }
        return new QueuedEntityPlacer<>(entitySelector, moveSelectorList);
    }

    public EntitySelectorConfig buildEntitySelectorConfig(HeuristicConfigPolicy<Solution_> configPolicy) {
        EntitySelectorConfig entitySelectorConfig_;
        if (config.getEntitySelectorConfig() == null) {
            EntityDescriptor<Solution_> entityDescriptor = getTheOnlyEntityDescriptor(configPolicy.getSolutionDescriptor());
            entitySelectorConfig_ = getDefaultEntitySelectorConfigForEntity(configPolicy, entityDescriptor);
        } else {
            entitySelectorConfig_ = config.getEntitySelectorConfig();
        }
        if (entitySelectorConfig_.getCacheType() != null
                && entitySelectorConfig_.getCacheType().compareTo(SelectionCacheType.PHASE) < 0) {
            throw new IllegalArgumentException("The queuedEntityPlacer (" + config
                    + ") cannot have an entitySelectorConfig (" + entitySelectorConfig_
                    + ") with a cacheType (" + entitySelectorConfig_.getCacheType()
                    + ") lower than " + SelectionCacheType.PHASE + ".");
        }
        return entitySelectorConfig_;
    }
}
