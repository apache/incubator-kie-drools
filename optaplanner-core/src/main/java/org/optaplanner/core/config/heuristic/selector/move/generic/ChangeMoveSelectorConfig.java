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

package org.optaplanner.core.config.heuristic.selector.move.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.CartesianProductMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;

@XStreamAlias("changeMoveSelector")
public class ChangeMoveSelectorConfig extends MoveSelectorConfig {

    @XStreamAlias("entitySelector")
    private EntitySelectorConfig entitySelectorConfig = null;
    @XStreamAlias("valueSelector")
    private ValueSelectorConfig valueSelectorConfig = null;


    public EntitySelectorConfig getEntitySelectorConfig() {
        return entitySelectorConfig;
    }

    public void setEntitySelectorConfig(EntitySelectorConfig entitySelectorConfig) {
        this.entitySelectorConfig = entitySelectorConfig;
    }

    public ValueSelectorConfig getValueSelectorConfig() {
        return valueSelectorConfig;
    }

    public void setValueSelectorConfig(ValueSelectorConfig valueSelectorConfig) {
        this.valueSelectorConfig = valueSelectorConfig;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        if (entitySelectorConfig == null) {
            throw new IllegalStateException("The entitySelectorConfig (" + entitySelectorConfig
                    + ") should haven been initialized during unfolding.");
        }
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(configPolicy,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        if (valueSelectorConfig == null) {
            throw new IllegalStateException("The valueSelectorConfig (" + valueSelectorConfig
                    + ") should haven been initialized during unfolding.");
        }
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(configPolicy,
                entitySelector.getEntityDescriptor(),
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        return new ChangeMoveSelector(entitySelector, valueSelector, randomSelection);
    }

    @Override
    protected MoveSelectorConfig buildUnfoldedMoveSelectorConfig(HeuristicConfigPolicy configPolicy) {
        Collection<EntityDescriptor> entityDescriptors;
        SolutionDescriptor solutionDescriptor = configPolicy.getSolutionDescriptor();
        if (entitySelectorConfig != null && (entitySelectorConfig.getEntityClass() != null || entitySelectorConfig.getMimicSelectorRef() != null)) {
            if (valueSelectorConfig != null && valueSelectorConfig.getVariableName() != null) {
                return null;
            }
            EntityDescriptor entityDescriptor;
            if (entitySelectorConfig.getEntityClass() != null) {
                entityDescriptor = solutionDescriptor.getEntityDescriptorStrict(entitySelectorConfig.getEntityClass());
                if (entityDescriptor == null) {
                    throw new IllegalArgumentException("The selectorConfig (" + entitySelectorConfig
                            + ") has an entityClass (" + entitySelectorConfig.getEntityClass() + ") that is not a known planning entity.\n"
                            + "Check your solver configuration. If that class (" + entitySelectorConfig.getEntityClass().getSimpleName()
                            + ") is not in the entityClassSet (" + solutionDescriptor.getEntityClassSet()
                            + "), check your Solution implementation's annotated methods too.");
                }
            } else {
                entityDescriptor = configPolicy.getEntityMimicRecorder(entitySelectorConfig.getMimicSelectorRef()).getEntityDescriptor();
            }
            entityDescriptors = Collections.singletonList(entityDescriptor);
        } else {
            entityDescriptors = solutionDescriptor.getEntityDescriptors();
        }

        List<GenuineVariableDescriptor> variableDescriptorList = new ArrayList<GenuineVariableDescriptor>();
        for (EntityDescriptor entityDescriptor : entityDescriptors) {
            if (valueSelectorConfig != null && valueSelectorConfig.getVariableName() != null) {
                GenuineVariableDescriptor variableDescriptor = entityDescriptor.getGenuineVariableDescriptor(valueSelectorConfig.getVariableName());
                if (variableDescriptor == null) {
                    throw new IllegalArgumentException("The selectorConfig (" + valueSelectorConfig
                            + ") has a variableName (" + valueSelectorConfig.getVariableName()
                            + ") which is not a valid planning variable on entityClass ("
                            + entityDescriptor.getEntityClass() + ").\n"
                            + entityDescriptor.buildInvalidVariableNameExceptionMessage(valueSelectorConfig.getVariableName()));
                }
                variableDescriptorList.add(variableDescriptor);
            } else {
                variableDescriptorList.addAll(entityDescriptor.getGenuineVariableDescriptors());
            }
        }

        List<MoveSelectorConfig> moveSelectorConfigList = new ArrayList<MoveSelectorConfig>(variableDescriptorList.size());
        for (GenuineVariableDescriptor variableDescriptor : variableDescriptorList) {
            ChangeMoveSelectorConfig changeMoveSelectorConfig = new ChangeMoveSelectorConfig();
            changeMoveSelectorConfig.inherit(this);
            EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
            if (this.entitySelectorConfig != null) {
                entitySelectorConfig.inherit(this.entitySelectorConfig);
            }
            if (entitySelectorConfig.getMimicSelectorRef() == null) {
                entitySelectorConfig.setEntityClass(variableDescriptor.getEntityDescriptor().getEntityClass());
            }
            changeMoveSelectorConfig.setEntitySelectorConfig(entitySelectorConfig);
            ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
            if (this.valueSelectorConfig != null) {
                valueSelectorConfig.inherit(this.valueSelectorConfig);
            }
            valueSelectorConfig.setVariableName(variableDescriptor.getVariableName());
            changeMoveSelectorConfig.setValueSelectorConfig(valueSelectorConfig);
            moveSelectorConfigList.add(changeMoveSelectorConfig);
        }
        return moveSelectorConfigList.size() == 1 ? moveSelectorConfigList.get(0)
                : new CartesianProductMoveSelectorConfig(moveSelectorConfigList);
    }

    public void inherit(ChangeMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (entitySelectorConfig == null) {
            entitySelectorConfig = inheritedConfig.getEntitySelectorConfig();
        } else if (inheritedConfig.getEntitySelectorConfig() != null) {
            entitySelectorConfig.inherit(inheritedConfig.getEntitySelectorConfig());
        }
        if (valueSelectorConfig == null) {
            valueSelectorConfig = inheritedConfig.getValueSelectorConfig();
        } else if (inheritedConfig.getValueSelectorConfig() != null) {
            valueSelectorConfig.inherit(inheritedConfig.getValueSelectorConfig());
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelectorConfig + ", " + valueSelectorConfig + ")";
    }

}
