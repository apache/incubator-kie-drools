/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.config.constructionheuristic.greedyFit;

import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.selector.entity.PlanningEntitySelectionOrder;
import org.optaplanner.core.impl.heuristic.selector.entity.PlanningEntitySelector;

@XStreamAlias("greedyFitPlanningEntity")
@Deprecated // Use ConstructionHeuristicSolverPhaseConfig
public class GreedyFitPlanningEntityConfig {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    protected Class<?> planningEntityClass = null;

    protected PlanningEntitySelectionOrder selectionOrder = null;

    public PlanningEntitySelectionOrder getSelectionOrder() {
        return selectionOrder;
    }

    public void setSelectionOrder(PlanningEntitySelectionOrder selectionOrder) {
        this.selectionOrder = selectionOrder;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public PlanningEntitySelector buildPlanningEntitySelector(SolutionDescriptor solutionDescriptor) {
        PlanningEntityDescriptor planningEntityDescriptor;
        Class<?> resolvedEntityClass;
        if (planningEntityClass != null) {
            resolvedEntityClass = planningEntityClass;
        } else {
            Set<Class<?>> planningEntityClassSet
                    = solutionDescriptor.getPlanningEntityClassSet();
            if (planningEntityClassSet.size() != 1) {
                throw new IllegalArgumentException(
                        "The greedyFitPlanningEntity has no planningEntityClass but there are multiple ("
                                + planningEntityClassSet.size() + ") planningEntityClasses.");
            }
            resolvedEntityClass = planningEntityClassSet.iterator().next();
        }
        if (!solutionDescriptor.hasPlanningEntityDescriptor(planningEntityClass)) {
            throw new IllegalArgumentException("The greedyFitPlanningEntity has a planningEntityClass ("
                    + planningEntityClass + ") that has not been configured as a planningEntity.");
        }
        planningEntityDescriptor = solutionDescriptor.getPlanningEntityDescriptor(planningEntityClass);
        PlanningEntitySelector planningEntitySelector = new PlanningEntitySelector(planningEntityDescriptor);
        planningEntitySelector.setSelectionOrder(selectionOrder != null ? selectionOrder
                : PlanningEntitySelectionOrder.ORIGINAL);
        return planningEntitySelector;
    }

    public void inherit(GreedyFitPlanningEntityConfig inheritedConfig) {
        if (selectionOrder == null) {
            selectionOrder = inheritedConfig.getSelectionOrder();
        }
    }

}
