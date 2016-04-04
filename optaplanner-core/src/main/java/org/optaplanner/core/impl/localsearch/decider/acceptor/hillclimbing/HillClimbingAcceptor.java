/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.localsearch.decider.acceptor.hillclimbing;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;

public class HillClimbingAcceptor extends AbstractAcceptor {

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isAccepted(LocalSearchMoveScope moveScope) {
        Score moveScore = moveScope.getScore();
        Score lastStepScore = moveScope.getStepScope().getPhaseScope().getLastCompletedStepScope().getScore();
        return moveScore.compareTo(lastStepScore) >= 0;
    }

}
