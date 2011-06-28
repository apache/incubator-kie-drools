/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.core.constructionheuristic.greedy.event;

import org.drools.planner.core.constructionheuristic.greedy.GreedySolverPhaseScope;
import org.drools.planner.core.constructionheuristic.greedy.GreedyStepScope;

public abstract class GreedySolverPhaseLifecycleListenerAdapter implements GreedySolverPhaseLifecycleListener {

    public void phaseStarted(GreedySolverPhaseScope greedySolverPhaseScope) {
        // Hook method
    }

    public void beforeDeciding(GreedyStepScope greedyStepScope) {
        // Hook method
    }

    public void stepDecided(GreedyStepScope greedyStepScope) {
        // Hook method
    }

    public void stepTaken(GreedyStepScope greedyStepScope) {
        // Hook method
    }

    public void phaseEnded(GreedySolverPhaseScope greedySolverPhaseScope) {
        // Hook method
    }

}
