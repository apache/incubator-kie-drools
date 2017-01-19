/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu;

import java.util.Collection;
import java.util.Collections;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

public class MoveTabuAcceptor extends AbstractTabuAcceptor {

    protected boolean useUndoMoveAsTabuMove = true;

    public MoveTabuAcceptor(String logIndentation) {
        super(logIndentation);
    }

    public void setUseUndoMoveAsTabuMove(boolean useUndoMoveAsTabuMove) {
        this.useUndoMoveAsTabuMove = useUndoMoveAsTabuMove;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    protected Collection<? extends Object> findTabu(LocalSearchMoveScope moveScope) {
        return Collections.singletonList(moveScope.getMove());
    }

    @Override
    protected Collection<? extends Object> findNewTabu(LocalSearchStepScope stepScope) {
        Move<?> tabuMove;
        if (useUndoMoveAsTabuMove) {
            tabuMove = stepScope.getUndoStep();
        } else {
            tabuMove = stepScope.getStep();
        }
        return Collections.singletonList(tabuMove);
    }

}
