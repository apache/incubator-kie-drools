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

package org.drools.planner.core.heuristic.selector.entity.decorator;

import java.util.Collections;

import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.phase.step.AbstractStepScope;

public class ShufflingEntitySelector extends CachingEntitySelector {

    public ShufflingEntitySelector(EntitySelector childEntitySelector, SelectionCacheType cacheType) {
        super(childEntitySelector, cacheType);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void stepStarted(AbstractStepScope stepScope) {
        super.stepStarted(stepScope);
        // Shuffle every step, even if the cacheType is PHASE
        Collections.shuffle(cachedEntityList, stepScope.getWorkingRandom());
    }

    @Override
    public String toString() {
        return "Shuffling(" + childEntitySelector + ")";
    }

}
