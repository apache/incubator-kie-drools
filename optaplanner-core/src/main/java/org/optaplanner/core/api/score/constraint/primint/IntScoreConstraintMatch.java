/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.core.api.score.constraint.primint;

import java.util.List;

import org.optaplanner.core.api.score.constraint.ScoreConstraintMatch;

public class IntScoreConstraintMatch extends ScoreConstraintMatch {

    protected final IntScoreConstraintMatchTotal scoreConstraintMatchTotal;

    protected final int weight;

    public IntScoreConstraintMatch(IntScoreConstraintMatchTotal scoreConstraintMatchTotal,
            List<Object> justificationList, int weight) {
        super(justificationList);
        this.scoreConstraintMatchTotal = scoreConstraintMatchTotal;
        this.weight = weight;
    }

    @Override
    public IntScoreConstraintMatchTotal getScoreConstraintMatchTotal() {
        return scoreConstraintMatchTotal;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public Number getWeightAsNumber() {
        return weight;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

}
