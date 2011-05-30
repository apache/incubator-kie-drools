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

import org.drools.planner.core.constructionheuristic.greedy.GreedySolverScope;
import org.drools.planner.core.constructionheuristic.greedy.event.GreedySolverLifecycleListener;
import org.drools.planner.core.localsearch.LocalSearchSolverScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.localsearch.event.LocalSearchSolverLifecycleListener;

public abstract class GreedySolverLifecycleListenerAdapter implements GreedySolverLifecycleListener {

    public void solvingStarted(GreedySolverScope greedySolverScope) {
        // Hook method
    }

    public void beforeDeciding(GreedySolverScope greedySolverScope) {
        // Hook method
    }

    public void stepDecided(GreedySolverScope greedySolverScope) {
        // Hook method
    }

    public void stepTaken(GreedySolverScope greedySolverScope) {
        // Hook method
    }

    public void solvingEnded(GreedySolverScope greedySolverScope) {
        // Hook method
    }

}
