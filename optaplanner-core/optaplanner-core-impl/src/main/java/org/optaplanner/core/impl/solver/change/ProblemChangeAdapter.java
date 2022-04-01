/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.solver.change;

import org.optaplanner.core.api.solver.ProblemFactChange;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.impl.solver.scope.SolverScope;

/**
 * Provides a layer of abstraction over {@link org.optaplanner.core.api.solver.change.ProblemChange} and the
 * deprecated {@link org.optaplanner.core.api.solver.ProblemFactChange} to preserve backward compatibility.
 */
public interface ProblemChangeAdapter<Solution_> {

    void doProblemChange(SolverScope<Solution_> solverScope);

    static <Solution_> ProblemChangeAdapter<Solution_> create(ProblemFactChange<Solution_> problemFactChange) {
        return (solverScope) -> problemFactChange.doChange(solverScope.getScoreDirector());
    }

    static <Solution_> ProblemChangeAdapter<Solution_> create(ProblemChange<Solution_> problemChange) {
        return (solverScope) -> {
            problemChange.doChange(solverScope.getWorkingSolution(), solverScope.getProblemChangeDirector());
            solverScope.getScoreDirector().triggerVariableListeners();
        };
    }
}
