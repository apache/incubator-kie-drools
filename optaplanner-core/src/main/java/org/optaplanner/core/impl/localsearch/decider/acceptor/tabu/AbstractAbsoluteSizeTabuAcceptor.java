/*
 * Copyright 2010 JBoss Inc
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

import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;

public abstract class AbstractAbsoluteSizeTabuAcceptor extends AbstractTabuAcceptor {

    protected int tabuSize = -1;
    protected int fadingTabuSize = 0;

    public void setTabuSize(int tabuSize) {
        this.tabuSize = tabuSize;
    }

    public void setFadingTabuSize(int fadingTabuSize) {
        this.fadingTabuSize = fadingTabuSize;
    }
    
    protected void validate() {
        if (tabuSize < 0) {
            throw new IllegalArgumentException("The tabuSize (" + tabuSize
                    + ") cannot be negative.");
        }
        if (fadingTabuSize < 0) {
            throw new IllegalArgumentException("The fadingTabuSize (" + fadingTabuSize
                    + ") cannot be negative.");
        }
        if (tabuSize + fadingTabuSize < 1) {
            throw new IllegalArgumentException("The sum of tabuSize (" + tabuSize
                    + ") and fadingTabuSize (" + fadingTabuSize + ") should be at least 1.");
        }
    }

    @Override
    protected int calculateActualMaximumSize(LocalSearchSolverPhaseScope phaseScope) {
        return calculateFadingTabuSize(phaseScope) + calculateRegularTabuSize(phaseScope);
    }
    
    @Override
    protected int calculateFadingTabuSize(LocalSearchSolverPhaseScope phaseScope) {
        return fadingTabuSize;
    }

    @Override
    protected int calculateRegularTabuSize(LocalSearchSolverPhaseScope phaseScope) {
        return tabuSize;
    }

}
