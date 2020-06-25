/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.move;

import java.util.Collection;
import java.util.Collections;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;

/**
 * Makes no changes.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class NoChangeMove<Solution_> extends AbstractMove<Solution_> {

    @Override
    public boolean isMoveDoable(ScoreDirector<Solution_> scoreDirector) {
        return true;
    }

    @Override
    public NoChangeMove<Solution_> createUndoMove(ScoreDirector<Solution_> scoreDirector) {
        return new NoChangeMove<>();
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector) {
        // do nothing
    }

    @Override
    public NoChangeMove<Solution_> rebase(ScoreDirector<Solution_> destinationScoreDirector) {
        return new NoChangeMove<>();
    }

    // ************************************************************************
    // Introspection methods
    // ************************************************************************

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "No change";
    }

}
