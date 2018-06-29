/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.constructionheuristic.placer;

import java.util.Iterator;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.heuristic.move.Move;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class Placement<Solution_> implements Iterable<Move<Solution_>> {

    private final Iterator<Move<Solution_>> moveIterator;

    public Placement(Iterator<Move<Solution_>> moveIterator) {
        this.moveIterator = moveIterator;
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        return moveIterator;
    }

    @Override
    public String toString() {
        return "Placement (" + moveIterator + ")";
    }

}
