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

package org.optaplanner.core.impl.phase.event;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.event.SolverLifecycleListenerAdapter;

public abstract class PhaseLifecycleListenerAdapter extends SolverLifecycleListenerAdapter
        implements PhaseLifecycleListener {

    public void phaseStarted(AbstractPhaseScope phaseScope) {
        // Hook method
    }

    public void stepStarted(AbstractStepScope stepScope) {
        // Hook method
    }

    public void stepEnded(AbstractStepScope stepScope) {
        // Hook method
    }

    public void phaseEnded(AbstractPhaseScope phaseScope) {
        // Hook method
    }

}
