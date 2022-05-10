/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.holder;

import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.Score;

/**
 * This is the base interface for all score holder implementations.
 *
 * @deprecated Score DRL is deprecated and will be removed in a future major version of OptaPlanner.
 *             See <a href="https://www.optaplanner.org/learn/drl-to-constraint-streams-migration.html">DRL to Constraint
 *             Streams migration recipe</a>.
 * @param <Score_> the {@link Score} type
 */
@Deprecated(forRemoval = true)
public interface ScoreHolder<Score_ extends Score<Score_>> {

    /**
     * Penalize a match by the {@link ConstraintWeight} negated.
     *
     * @param kcontext never null, the magic variable in DRL
     */
    void penalize(RuleContext kcontext);

    /**
     * Reward a match by the {@link ConstraintWeight}.
     *
     * @param kcontext never null, the magic variable in DRL
     */
    void reward(RuleContext kcontext);

}
