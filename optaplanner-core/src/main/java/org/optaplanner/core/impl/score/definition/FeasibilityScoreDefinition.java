/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.score.definition;

import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;

public interface FeasibilityScoreDefinition<S extends FeasibilityScore> extends ScoreDefinition<S> {

    /**
     * Returns the number of levels of {@link Score#toLevelNumbers()}.
     * that are used to determine {@link FeasibilityScore#isFeasible()}.
     * @return at least 0, at most {@link #getLevelsSize()}
     */
    int getFeasibleLevelsSize();

}
