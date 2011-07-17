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

package org.drools.planner.config.constructionheuristic.greedyFit;

import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.core.domain.meta.PlanningEntityDescriptor;
import org.drools.planner.core.domain.meta.SolutionDescriptor;
import org.drools.planner.core.heuristic.selector.entity.PlanningEntitySelectionOrder;
import org.drools.planner.core.heuristic.selector.entity.PlanningEntitySelector;

@XStreamAlias("greedyFitPlanningEntity")
public class GreedyFitPlanningEntityConfig {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    private Class<?> planningEntityClass = null;

    private PlanningEntitySelectionOrder selectionOrder = null;
    private Boolean resetInitializedPlanningEntities = null;

    public PlanningEntitySelectionOrder getSelectionOrder() {
        return selectionOrder;
    }

    public void setSelectionOrder(PlanningEntitySelectionOrder selectionOrder) {
        this.selectionOrder = selectionOrder;
    }

    public Boolean getResetInitializedPlanningEntities() {
        return resetInitializedPlanningEntities;
    }

    public void setResetInitializedPlanningEntities(Boolean resetInitializedPlanningEntities) {
        this.resetInitializedPlanningEntities = resetInitializedPlanningEntities;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public PlanningEntitySelector buildPlanningEntitySelector(SolutionDescriptor solutionDescriptor) {
        PlanningEntityDescriptor planningEntityDescriptor;
        if (planningEntityClass != null) {
            planningEntityDescriptor = solutionDescriptor.getPlanningEntityDescriptor(planningEntityClass);
            if (planningEntityDescriptor == null) {
                throw new IllegalArgumentException("The greedyFitPlanningEntity has a planningEntityClass ("
                        + planningEntityClass + ") that has not been configured as a planningEntity.");
            }
        } else {
            Set<Class<?>> planningEntityImplementationClassSet
                    = solutionDescriptor.getPlanningEntityImplementationClassSet();
            if (planningEntityImplementationClassSet.size() != 1) {
                throw new IllegalArgumentException(
                        "The greedyFitPlanningEntity has no planningEntityClass but there are multiple ("
                                + planningEntityImplementationClassSet.size() + ") planningEntityClasses.");
            }
            planningEntityDescriptor = solutionDescriptor.getPlanningEntityDescriptor(
                    planningEntityImplementationClassSet.iterator().next());
        }
        PlanningEntitySelector planningEntitySelector = new PlanningEntitySelector(planningEntityDescriptor);
        planningEntitySelector.setSelectionOrder(selectionOrder != null ? selectionOrder
                : PlanningEntitySelectionOrder.ORIGINAL);
        planningEntitySelector.setResetInitializedPlanningEntities(resetInitializedPlanningEntities != null ?
                resetInitializedPlanningEntities.booleanValue() : false);
        return planningEntitySelector;
    }

    public void inherit(GreedyFitPlanningEntityConfig inheritedConfig) {
        if (selectionOrder == null) {
            selectionOrder = inheritedConfig.getSelectionOrder();
        }
        if (resetInitializedPlanningEntities == null) {
            resetInitializedPlanningEntities = inheritedConfig.getResetInitializedPlanningEntities();
        }
    }

}
