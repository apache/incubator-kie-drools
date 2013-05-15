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

    protected double tabuRatio = Double.NaN;
    protected double fadingTabuRatio = 0.0;

    public void setTabuSizeToEntityCountRatio(double ratio) {
        this.tabuRatio = ratio;
    }

    public void setFadingTabuSizeToEntityCountRatio(double ratio) {
        this.fadingTabuRatio = ratio;
    }
    
    protected void validate() {
        if (tabuRatio < 0.0 || tabuRatio > 1.0) {
            throw new IllegalArgumentException("The tabuRatio (" + tabuRatio
                    + ") must be between 0.0 and 1.0.");
        }
        if (fadingTabuRatio < 0 || fadingTabuRatio > 1) {
            throw new IllegalArgumentException("The fadingTabuRatio (" + fadingTabuRatio
                    + ") must be between 0.0 and 1.0.");
        }
        if (tabuRatio + fadingTabuRatio <= 0.0) {
            throw new IllegalArgumentException("The sum of tabuRatio (" + tabuRatio
                    + ") and fadingTabuRatio (" + fadingTabuRatio + ") should be higher than 0.0.");
        }
    }
    
    @Override
    protected int calculateFadingTabuSize(LocalSearchSolverPhaseScope phaseScope) {
        return (int) Math.round(phaseScope.getWorkingPlanningEntityList().size() * fadingTabuRatio);
    }

    @Override
    protected int calculateRegularTabuSize(LocalSearchSolverPhaseScope phaseScope) {
        return (int) Math.round(phaseScope.getWorkingPlanningEntityList().size() * tabuRatio);
    }

}
