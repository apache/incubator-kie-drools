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

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.impl.localsearch.event.LocalSearchPhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

/**
 * Abstract superclass for {@link FinalistPodium}.
 *
 * @see FinalistPodium
 */
public abstract class AbstractFinalistPodium<Solution_> extends LocalSearchPhaseLifecycleListenerAdapter<Solution_>
        implements FinalistPodium<Solution_> {

    protected static final int FINALIST_LIST_MAX_SIZE = 1_024_000;

    protected boolean finalistIsAccepted;
    protected List<LocalSearchMoveScope<Solution_>> finalistList = new ArrayList<>(1024);

    @Override
    public void stepStarted(LocalSearchStepScope<Solution_> stepScope) {
        super.stepStarted(stepScope);
        finalistIsAccepted = false;
        finalistList.clear();
    }

    protected void clearAndAddFinalist(LocalSearchMoveScope<Solution_> moveScope) {
        finalistList.clear();
        finalistList.add(moveScope);
    }

    protected void addFinalist(LocalSearchMoveScope<Solution_> moveScope) {
        if (finalistList.size() >= FINALIST_LIST_MAX_SIZE) {
            // Avoid unbounded growth and OutOfMemoryException
            return;
        }
        finalistList.add(moveScope);
    }

    @Override
    public List<LocalSearchMoveScope<Solution_>> getFinalistList() {
        return finalistList;
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        finalistIsAccepted = false;
        finalistList.clear();
    }

}
