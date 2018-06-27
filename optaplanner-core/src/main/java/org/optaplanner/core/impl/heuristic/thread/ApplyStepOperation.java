/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.thread;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.heuristic.move.Move;

public class ApplyStepOperation<Solution_> extends MoveThreadOperation<Solution_> {

    private final int stepIndex;
    private final Move<Solution_> step;
    private final Score score;

    public ApplyStepOperation(int stepIndex, Move<Solution_> step, Score score) {
        this.stepIndex = stepIndex;
        this.step = step;
        this.score = score;
    }

    public int getStepIndex() {
        return stepIndex;
    }

    public Move<Solution_> getStep() {
        return step;
    }

    public Score getScore() {
        return score;
    }

}
