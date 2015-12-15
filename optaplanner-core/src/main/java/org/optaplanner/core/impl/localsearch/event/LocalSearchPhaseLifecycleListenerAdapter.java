/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.localsearch.event;

import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.event.SolverLifecycleListenerAdapter;

public abstract class LocalSearchPhaseLifecycleListenerAdapter extends SolverLifecycleListenerAdapter
        implements LocalSearchPhaseLifecycleListener {

    public void phaseStarted(LocalSearchPhaseScope phaseScope) {
        // Hook method
    }

    public void stepStarted(LocalSearchStepScope stepScope) {
        // Hook method
    }

    public void stepEnded(LocalSearchStepScope stepScope) {
        // Hook method
    }

    public void phaseEnded(LocalSearchPhaseScope phaseScope) {
        // Hook method
    }

}
