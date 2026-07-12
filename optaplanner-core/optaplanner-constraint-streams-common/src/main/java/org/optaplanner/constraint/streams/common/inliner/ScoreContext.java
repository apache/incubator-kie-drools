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

package org.optaplanner.constraint.streams.common.inliner;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;

public abstract class ScoreContext<Score_ extends Score<Score_>> {

    private final AbstractScoreInliner<Score_> parent;
    protected final Constraint constraint;
    protected final Score_ constraintWeight;
    protected final boolean constraintMatchEnabled;

    protected ScoreContext(AbstractScoreInliner<Score_> parent, Constraint constraint, Score_ constraintWeight) {
        this.parent = parent;
        this.constraint = constraint;
        this.constraintWeight = constraintWeight;
        this.constraintMatchEnabled = parent.constraintMatchEnabled;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public Score_ getConstraintWeight() {
        return constraintWeight;
    }

    public boolean isConstraintMatchEnabled() {
        return constraintMatchEnabled;
    }

    protected UndoScoreImpacter impactWithConstraintMatch(UndoScoreImpacter undoScoreImpact, Score_ score,
            JustificationsSupplier justificationsSupplier) {
        Runnable undoConstraintMatch = parent.addConstraintMatch(constraint, constraintWeight, score, justificationsSupplier);
        return () -> {
            undoScoreImpact.run();
            undoConstraintMatch.run();
        };
    }

}
