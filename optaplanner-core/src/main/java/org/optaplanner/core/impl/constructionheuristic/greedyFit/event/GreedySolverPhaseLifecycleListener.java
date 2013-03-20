/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.core.impl.constructionheuristic.greedyFit.event;

import java.util.EventListener;

import org.optaplanner.core.impl.constructionheuristic.greedyFit.scope.GreedyFitSolverPhaseScope;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.scope.GreedyFitStepScope;

public interface GreedySolverPhaseLifecycleListener extends EventListener {

    void phaseStarted(GreedyFitSolverPhaseScope greedyFitSolverPhaseScope);

    void stepStarted(GreedyFitStepScope greedyFitStepScope);

    void stepEnded(GreedyFitStepScope greedyFitStepScope);

    void phaseEnded(GreedyFitSolverPhaseScope greedyFitSolverPhaseScope);

}
