/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.core.impl.localsearch.decider.acceptor.common;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.localsearch.decider.acceptor.Acceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;

/**
 * Aspiration can override the rejection of a selected move by an {@link Acceptor},
 * if the select move improves the {@link Score}.
 */
public enum AspirationType {
    /**
     * There is no aspiration.
     */
    NONE,
    /**
     * The selected move's {@link Score} is better than the last step's {@link Score}
     * (which implies its also better than the best {@link Score}).
     */
    BETTER_THAN_LAST_STEP_SCORE,
    /**
     * The selected move's {@link Score} is better than the best {@link Score}.
     */
    BETTER_THAN_BEST_SCORE;

    public boolean isAspired(LocalSearchMoveScope moveScope) {
        Score comparisonScore;
        switch (this) {
            case NONE:
                return false;
            case BETTER_THAN_LAST_STEP_SCORE:
                comparisonScore = moveScope.getStepScope().getPhaseScope().getLastCompletedStepScope().getScore();
                break;
            case BETTER_THAN_BEST_SCORE:
                comparisonScore = moveScope.getStepScope().getPhaseScope().getBestScore();
                break;
            default:
                throw new IllegalStateException("The aspirationType (" + this + ") is not implemented.");
        }
        // Doesn't use the deciderScoreComparator because shifting penalties don't apply
        return moveScope.getScore().compareTo(comparisonScore) > 0;
    }

}
