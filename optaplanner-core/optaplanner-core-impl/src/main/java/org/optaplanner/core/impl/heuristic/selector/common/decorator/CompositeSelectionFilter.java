/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.common.decorator;

import java.util.Arrays;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;

/**
 * Combines several {@link SelectionFilter}s into one.
 * Does a logical AND over the accept status of its filters.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <T> the selection type
 */
final class CompositeSelectionFilter<Solution_, T> implements SelectionFilter<Solution_, T> {

    static final SelectionFilter NOOP = (scoreDirector, selection) -> true;

    final SelectionFilter<Solution_, T>[] selectionFilterArray;

    CompositeSelectionFilter(SelectionFilter<Solution_, T>[] selectionFilterArray) {
        this.selectionFilterArray = selectionFilterArray;
    }

    @Override
    public boolean accept(ScoreDirector<Solution_> scoreDirector, T selection) {
        for (SelectionFilter<Solution_, T> selectionFilter : selectionFilterArray) {
            if (!selectionFilter.accept(scoreDirector, selection)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        CompositeSelectionFilter<?, ?> that = (CompositeSelectionFilter<?, ?>) other;
        return Arrays.equals(selectionFilterArray, that.selectionFilterArray);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(selectionFilterArray);
    }

}
