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

package org.optaplanner.core.impl.heuristic.selector.common.decorator;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;

/**
 * Combines several {@link SelectionFilter}s into one.
 * Does a logical AND over the accept status of its filters.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <T> the selection type
 */
public class CompositeSelectionFilter<Solution_, T> implements SelectionFilter<Solution_, T> {

    protected final List<SelectionFilter<Solution_, T>> selectionFilterList;

    public CompositeSelectionFilter(List<SelectionFilter<Solution_, T>> selectionFilterList) {
        this.selectionFilterList = selectionFilterList;
    }

    @Override
    public boolean accept(ScoreDirector<Solution_> scoreDirector, T selection) {
        for (SelectionFilter<Solution_, T> selectionFilter : selectionFilterList) {
            if (!selectionFilter.accept(scoreDirector, selection)) {
                return false;
            }
        }
        return true;
    }

}
