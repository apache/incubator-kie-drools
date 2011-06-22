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

package org.drools.planner.config.constructionheuristic.greedy;

import java.util.Comparator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.config.AbstractSolverConfig;
import org.drools.planner.core.constructionheuristic.greedy.DefaultGreedySolver;
import org.drools.planner.core.constructionheuristic.greedy.GreedySolver;
import org.drools.planner.core.constructionheuristic.greedy.decider.DefaultGreedyDecider;
import org.drools.planner.core.constructionheuristic.greedy.decider.GreedyDecider;
import org.drools.planner.core.constructionheuristic.greedy.decider.PickEarlyFitType;
import org.drools.planner.core.constructionheuristic.greedy.selector.GreedyPlanningEntitySelector;

@XStreamAlias("greedySolver")
public class GreedySolverConfig extends AbstractSolverConfig {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    // private Boolean resetInitializedPlanningEntities;

    private Class<? extends Comparator<Object>> fitOrderPlanningEntityComparatorClass = null;
    private PickEarlyFitType pickEarlyFitType = null;

    public Class<? extends Comparator<Object>> getFitOrderPlanningEntityComparatorClass() {
        return fitOrderPlanningEntityComparatorClass;
    }

    public void setFitOrderPlanningEntityComparatorClass(Class<? extends Comparator<Object>> fitOrderPlanningEntityComparatorClass) {
        this.fitOrderPlanningEntityComparatorClass = fitOrderPlanningEntityComparatorClass;
    }

    public PickEarlyFitType getPickEarlyFitType() {
        return pickEarlyFitType;
    }

    public void setPickEarlyFitType(PickEarlyFitType pickEarlyFitType) {
        this.pickEarlyFitType = pickEarlyFitType;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public GreedySolver buildSolver() {
        DefaultGreedySolver greedySolver = new DefaultGreedySolver();
        configureAbstractSolver(greedySolver);
        greedySolver.setGreedyPlanningEntitySelector(buildGreedyPlanningEntitySelector());
        greedySolver.setGreedyDecider(buildGreedyDecider());
        return greedySolver;
    }

    private GreedyPlanningEntitySelector buildGreedyPlanningEntitySelector() {
        GreedyPlanningEntitySelector greedyPlanningEntitySelector = new GreedyPlanningEntitySelector();
        if (fitOrderPlanningEntityComparatorClass != null) {
            Comparator<Object> fitOrderPlanningEntityComparator;
            try {
                fitOrderPlanningEntityComparator = fitOrderPlanningEntityComparatorClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("The fitOrderPlanningEntityComparatorClass ("
                        + fitOrderPlanningEntityComparatorClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("The fitOrderPlanningEntityComparatorClass ("
                        + fitOrderPlanningEntityComparatorClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
            greedyPlanningEntitySelector.setFitOrderPlanningEntityComparator(fitOrderPlanningEntityComparator);
        }
        return greedyPlanningEntitySelector;
    }

    private GreedyDecider buildGreedyDecider() {
        DefaultGreedyDecider greedyDecider = new DefaultGreedyDecider();
        PickEarlyFitType pickEarlyFitType = (this.pickEarlyFitType == null)
                ? PickEarlyFitType.NEVER : this.pickEarlyFitType;
        greedyDecider.setPickEarlyFitType(pickEarlyFitType);
        return greedyDecider;
    }

    public void inherit(GreedySolverConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (fitOrderPlanningEntityComparatorClass == null) {
            fitOrderPlanningEntityComparatorClass = inheritedConfig.getFitOrderPlanningEntityComparatorClass();
        }
        if (pickEarlyFitType == null) {
            pickEarlyFitType = inheritedConfig.getPickEarlyFitType();
        }
    }

}
