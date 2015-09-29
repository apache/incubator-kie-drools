/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.core.impl.domain.solution;

import java.io.Serializable;
import java.util.Collection;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.Score;

/**
 * Currently only used by OptaPlanner Workbench.
 * TODO Should we promote using this class in the examples and docs too?
 * We can never enforce it, as the user might want to use a different superclass.
 * @param <S> the {@link Score} type used by this use case
 */
public abstract class AbstractSolution<S extends Score> implements Solution<S>, Serializable {

    protected S score;

    @Override
    public S getScore() {
        return score;
    }

    @Override
    public void setScore(S score) {
        this.score = score;
    }

    @Override
    public Collection<?> getProblemFacts() {
        throw new UnsupportedOperationException("TODO PLANNER-461"); // TODO https://issues.jboss.org/browse/PLANNER-461
    }

}
