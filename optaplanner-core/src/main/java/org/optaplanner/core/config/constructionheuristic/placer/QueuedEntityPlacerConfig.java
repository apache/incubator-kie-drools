/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.config.constructionheuristic.placer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.collections.CollectionUtils;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.CartesianProductMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.constructionheuristic.placer.QueuedEntityPlacer;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.termination.Termination;

@XStreamAlias("queuedEntityPlacer")
public class QueuedEntityPlacerConfig extends EntityPlacerConfig {

    @XStreamAlias("entitySelector")
    protected EntitySelectorConfig entitySelectorConfig = null;
    @XStreamImplicit()
    private List<MoveSelectorConfig> moveSelectorConfigList = null;

    public EntitySelectorConfig getEntitySelectorConfig() {
        return entitySelectorConfig;
    }

    public void setEntitySelectorConfig(EntitySelectorConfig entitySelectorConfig) {
        this.entitySelectorConfig = entitySelectorConfig;
    }

    public List<MoveSelectorConfig> getMoveSelectorConfigList() {
        return moveSelectorConfigList;
    }

    public void setMoveSelectorConfigList(List<MoveSelectorConfig> moveSelectorConfigList) {
        this.moveSelectorConfigList = moveSelectorConfigList;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public QueuedEntityPlacer buildEntityPlacer(HeuristicConfigPolicy configPolicy, Termination phaseTermination) {
        SelectionOrder defaultSelectionOrder = SelectionOrder.ORIGINAL;
        EntitySelectorConfig entitySelectorConfig_;
        String entitySelectorId = "undefined";
        if (entitySelectorConfig == null) {
            entitySelectorConfig_ = new EntitySelectorConfig();
            PlanningEntityDescriptor entityDescriptor = deduceEntityDescriptor(configPolicy.getSolutionDescriptor());
            Class<?> entityClass = entityDescriptor.getPlanningEntityClass();
            entitySelectorId = entityClass.getName();
            entitySelectorConfig_.setId(entitySelectorId);
            entitySelectorConfig_.setEntityClass(entityClass);
            if (configPolicy.isSortEntitiesByDecreasingDifficultyEnabled()) {
                entitySelectorConfig_.setCacheType(SelectionCacheType.PHASE);
                entitySelectorConfig_.setSelectionOrder(SelectionOrder.SORTED);
                entitySelectorConfig_.setSorterManner(EntitySelectorConfig.EntitySorterManner.DECREASING_DIFFICULTY);
            }
        } else {
            entitySelectorConfig_ = entitySelectorConfig;
        }
        if (entitySelectorConfig_.getCacheType() != null
                && entitySelectorConfig_.getCacheType().compareTo(SelectionCacheType.PHASE) < 0) {
            throw new IllegalArgumentException("The queuedEntityPlacer (" + this
                    + ") cannot have an entitySelectorConfig ("  + entitySelectorConfig
                    + ") with a cacheType (" + entitySelectorConfig_.getCacheType()
                    + ") lower than " + SelectionCacheType.PHASE + ".");
        }
        EntitySelector entitySelector = entitySelectorConfig_.buildEntitySelector(configPolicy,
                SelectionCacheType.PHASE, defaultSelectionOrder);

        List<MoveSelectorConfig> moveSelectorConfigList_;
        if (CollectionUtils.isEmpty(moveSelectorConfigList)) {
            PlanningEntityDescriptor entityDescriptor = entitySelector.getEntityDescriptor();
            Collection<PlanningVariableDescriptor> variableDescriptors = entityDescriptor.getVariableDescriptors();
            List<MoveSelectorConfig> subMoveSelectorConfigList = new ArrayList<MoveSelectorConfig>(
                    variableDescriptors.size());
            for (PlanningVariableDescriptor variableDescriptor : variableDescriptors) {
                ChangeMoveSelectorConfig changeMoveSelectorConfig = new ChangeMoveSelectorConfig();
                EntitySelectorConfig changeEntitySelectorConfig = new EntitySelectorConfig();
                changeEntitySelectorConfig.setMimicSelectorRef(entitySelectorId);
                changeMoveSelectorConfig.setEntitySelectorConfig(changeEntitySelectorConfig);
                ValueSelectorConfig changeValueSelectorConfig = new ValueSelectorConfig();
                changeValueSelectorConfig.setVariableName(variableDescriptor.getVariableName());
                if (configPolicy.isSortValuesByIncreasingStrengthEnabled()) {
                    if (variableDescriptor.getValueRangeDescriptor().isEntityIndependent()) {
                        changeValueSelectorConfig.setCacheType(SelectionCacheType.PHASE);
                    } else {
                        changeValueSelectorConfig.setCacheType(SelectionCacheType.STEP);
                    }
                    changeValueSelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
                    changeValueSelectorConfig.setSorterManner(ValueSelectorConfig.ValueSorterManner.INCREASING_STRENGTH);
                }
                changeMoveSelectorConfig.setValueSelectorConfig(changeValueSelectorConfig);
                subMoveSelectorConfigList.add(changeMoveSelectorConfig);
            }
            if (true) { // TODO
                moveSelectorConfigList_ = Collections.<MoveSelectorConfig>singletonList(
                        new CartesianProductMoveSelectorConfig(subMoveSelectorConfigList));
            } else {
                moveSelectorConfigList_ = subMoveSelectorConfigList;
            }
        } else {
            moveSelectorConfigList_ = moveSelectorConfigList;
        }
        List<MoveSelector> moveSelectorList = new ArrayList<MoveSelector>(moveSelectorConfigList_.size());
        for (MoveSelectorConfig moveSelectorConfig : moveSelectorConfigList_) {
            moveSelectorList.add(moveSelectorConfig.buildMoveSelector(
                    configPolicy, SelectionCacheType.JUST_IN_TIME, defaultSelectionOrder));
        }
        return new QueuedEntityPlacer(entitySelector, moveSelectorList);
    }

    public void inherit(QueuedEntityPlacerConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (entitySelectorConfig == null) {
            entitySelectorConfig = inheritedConfig.getEntitySelectorConfig();
        } else if (inheritedConfig.getEntitySelectorConfig() != null) {
            entitySelectorConfig.inherit(inheritedConfig.getEntitySelectorConfig());
        }
        moveSelectorConfigList = ConfigUtils.inheritMergeableListProperty(
                moveSelectorConfigList, inheritedConfig.getMoveSelectorConfigList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelectorConfig + ", " + moveSelectorConfigList + ")";
    }

}
