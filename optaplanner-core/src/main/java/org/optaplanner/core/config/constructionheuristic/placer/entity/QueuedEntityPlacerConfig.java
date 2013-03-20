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

package org.optaplanner.core.config.constructionheuristic.placer.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.constructionheuristic.placer.value.ValuePlacerConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.constructionheuristic.placer.entity.QueuedEntityPlacer;
import org.optaplanner.core.impl.constructionheuristic.placer.value.ValuePlacer;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.termination.Termination;

@XStreamAlias("queuedEntityPlacer")
public class QueuedEntityPlacerConfig extends EntityPlacerConfig {

    @XStreamAlias("entitySelector")
    protected EntitySelectorConfig entitySelectorConfig = null;
    @XStreamImplicit(itemFieldName = "valuePlacer")
    protected List<ValuePlacerConfig> valuePlacerConfigList = null;

    public EntitySelectorConfig getEntitySelectorConfig() {
        return entitySelectorConfig;
    }

    public void setEntitySelectorConfig(EntitySelectorConfig entitySelectorConfig) {
        this.entitySelectorConfig = entitySelectorConfig;
    }

    public List<ValuePlacerConfig> getValuePlacerConfigList() {
        return valuePlacerConfigList;
    }

    public void setValuePlacerConfigList(List<ValuePlacerConfig> valuePlacerConfigList) {
        this.valuePlacerConfigList = valuePlacerConfigList;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public QueuedEntityPlacer buildEntityPlacer(EnvironmentMode environmentMode,
            SolutionDescriptor solutionDescriptor, Termination phaseTermination) {
        // TODO filter out initialized entities
        EntitySelectorConfig entitySelectorConfig_ = entitySelectorConfig == null ? new EntitySelectorConfig()
                : entitySelectorConfig;
        EntitySelector entitySelector = entitySelectorConfig_.buildEntitySelector(environmentMode,
                solutionDescriptor, SelectionCacheType.JUST_IN_TIME, SelectionOrder.ORIGINAL); // TODO fix selection order
        List<ValuePlacerConfig> valuePlacerConfigList_
                = valuePlacerConfigList == null ? Arrays.asList(new ValuePlacerConfig())
                : valuePlacerConfigList;
        List<ValuePlacer> valuePlacerList = new ArrayList<ValuePlacer>(valuePlacerConfigList_.size());
        for (ValuePlacerConfig valuePlacerConfig : valuePlacerConfigList_) {
            valuePlacerList.add(valuePlacerConfig.buildValuePlacer(environmentMode,
                    solutionDescriptor, phaseTermination, entitySelector.getEntityDescriptor()));
        }
        return new QueuedEntityPlacer(entitySelector, valuePlacerList);
    }

    public void inherit(QueuedEntityPlacerConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (entitySelectorConfig == null) {
            entitySelectorConfig = inheritedConfig.getEntitySelectorConfig();
        } else if (inheritedConfig.getEntitySelectorConfig() != null) {
            entitySelectorConfig.inherit(inheritedConfig.getEntitySelectorConfig());
        }
        valuePlacerConfigList = ConfigUtils.inheritMergeableListProperty(
                valuePlacerConfigList, inheritedConfig.getValuePlacerConfigList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelectorConfig + ", " + valuePlacerConfigList + ")";
    }

}
