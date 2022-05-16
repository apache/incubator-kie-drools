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

package org.optaplanner.core.impl.localsearch.decider.forager.finalist;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

/**
 * Default implementation of {@link FinalistPodium}.
 *
 * @see FinalistPodium
 */
public final class HighestScoreFinalistPodium<Solution_> extends AbstractFinalistPodium<Solution_> {

    protected Score finalistScore;

    @Override
    public void stepStarted(LocalSearchStepScope<Solution_> stepScope) {
        super.stepStarted(stepScope);
        finalistScore = null;
    }

    @Override
    public void addMove(LocalSearchMoveScope<Solution_> moveScope) {
        boolean accepted = moveScope.getAccepted();
        if (finalistIsAccepted && !accepted) {
            return;
        }
        if (accepted && !finalistIsAccepted) {
            finalistIsAccepted = true;
            finalistScore = null;
        }
        Score moveScore = moveScope.getScore();
        int scoreComparison = doComparison(moveScore);
        if (scoreComparison > 0) {
            finalistScore = moveScore;
            clearAndAddFinalist(moveScope);
        } else if (scoreComparison == 0) {
            addFinalist(moveScope);
        }
    }

    private int doComparison(Score moveScore) {
        if (finalistScore == null) {
            return 1;
        }
        return moveScore.compareTo(finalistScore);
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        finalistScore = null;
    }

}
