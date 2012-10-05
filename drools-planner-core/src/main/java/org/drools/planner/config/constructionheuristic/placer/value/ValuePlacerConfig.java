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

package org.drools.planner.config.constructionheuristic.placer.value;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.constructionheuristic.placer.PlacerConfig;
import org.drools.planner.config.heuristic.selector.common.SelectionOrder;
import org.drools.planner.config.heuristic.selector.value.ValueSelectorConfig;
import org.drools.planner.config.util.ConfigUtils;
import org.drools.planner.core.constructionheuristic.placer.value.ValuePlacer;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.value.ValueSelector;
import org.drools.planner.core.termination.Termination;

@XStreamAlias("valuePlacer")
public class ValuePlacerConfig extends PlacerConfig {

    @XStreamAlias("valueSelector")
    protected ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();

    protected Integer selectedCountLimit = null;

    public ValueSelectorConfig getValueSelectorConfig() {
        return valueSelectorConfig;
    }

    public void setValueSelectorConfig(ValueSelectorConfig valueSelectorConfig) {
        this.valueSelectorConfig = valueSelectorConfig;
    }

    public Integer getSelectedCountLimit() {
        return selectedCountLimit;
    }

    public void setSelectedCountLimit(Integer selectedCountLimit) {
        this.selectedCountLimit = selectedCountLimit;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public ValuePlacer buildValuePlacer(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            Termination phaseTermination, PlanningEntityDescriptor entityDescriptor) {
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(environmentMode,
                solutionDescriptor, entityDescriptor,
                selectedCountLimit == null ? SelectionCacheType.STEP : SelectionCacheType.JUST_IN_TIME,
                selectedCountLimit == null ? SelectionOrder.ORIGINAL : SelectionOrder.RANDOM);
        ValuePlacer valuePlacer = new ValuePlacer(phaseTermination, valueSelector,
                selectedCountLimit == null ? Integer.MAX_VALUE : selectedCountLimit);
        if (environmentMode == EnvironmentMode.TRACE) {
            valuePlacer.setAssertMoveScoreIsUncorrupted(true);
        }
        if (environmentMode == EnvironmentMode.DEBUG || environmentMode == EnvironmentMode.TRACE) {
            valuePlacer.setAssertUndoMoveIsUncorrupted(true);
        }
        return valuePlacer;
    }

    public void inherit(ValuePlacerConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (valueSelectorConfig == null) {
            valueSelectorConfig = inheritedConfig.getValueSelectorConfig();
        } else if (inheritedConfig.getValueSelectorConfig() != null) {
            valueSelectorConfig.inherit(inheritedConfig.getValueSelectorConfig());
        }
        selectedCountLimit = ConfigUtils.inheritOverwritableProperty(selectedCountLimit, inheritedConfig.getSelectedCountLimit());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + valueSelectorConfig + ")";
    }

}
