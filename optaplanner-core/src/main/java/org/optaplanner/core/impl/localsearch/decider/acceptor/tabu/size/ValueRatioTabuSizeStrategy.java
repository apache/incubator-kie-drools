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

package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size;

import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

public class ValueRatioTabuSizeStrategy extends AbstractTabuSizeStrategy {

    protected final double tabuRatio;

    public ValueRatioTabuSizeStrategy(double tabuRatio) {
        this.tabuRatio = tabuRatio;
        if (tabuRatio <= 0.0 || tabuRatio >= 1.0) {
            throw new IllegalArgumentException("The tabuRatio (" + tabuRatio
                    + ") must be between 0.0 and 1.0.");
        }
    }

    @Override
    public int determineTabuSize(LocalSearchStepScope stepScope) {
        // TODO we might want to cache the valueCount if and only if moves don't add/remove entities
        int valueCount = stepScope.getPhaseScope().getWorkingValueCount();
        int tabuSize = (int) Math.round(valueCount * tabuRatio);
        return protectTabuSizeCornerCases(valueCount, tabuSize);
    }

}
