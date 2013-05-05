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

public abstract class AbstractRelativeSizeTabuAcceptor extends AbstractTabuAcceptor {

    protected double tabuRatio = -1;
    protected double fadingTabuRatio = 0;

    public void setTabuSizeToEntityCountRatio(double ratio) {
        this.tabuRatio = ratio;
    }

    public void setFadingTabuSizeToEntityCountRatio(double ratio) {
        this.fadingTabuRatio = ratio;
    }
    
    protected void validate() {
        if (tabuRatio < 0 || tabuRatio > 1) {
            throw new IllegalArgumentException("The tabuRatio (" + tabuRatio
                    + ") must fall into <0, 1> range.");
        }
        if (fadingTabuRatio < 0 || fadingTabuRatio > 1) {
            throw new IllegalArgumentException("The fadingTabuRatio (" + fadingTabuRatio
                    + ") must fall into <0, 1> range.");
        }
        if (tabuRatio + fadingTabuRatio == 0) {
            throw new IllegalArgumentException("The sum of tabuSize and fadingTabuSize should be at least 1.");
        }
    }

    @Override
    protected int calculateActualMaximumSize(LocalSearchSolverPhaseScope scope) {
        return calculateFadingTabuSize(scope) + calculateRegularTabuSize(scope);
    }
    
    @Override
    protected int calculateFadingTabuSize(LocalSearchSolverPhaseScope scope) {
        return (int)Math.round(scope.getWorkingPlanningEntityList().size() * fadingTabuRatio);
    }

    @Override
    protected int calculateRegularTabuSize(LocalSearchSolverPhaseScope scope) {
        return (int)Math.round(scope.getWorkingPlanningEntityList().size() * tabuRatio);
    }

}
