/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for {@link FinalistPodium}.
 * @see FinalistPodium
 */
public abstract class AbstractFinalistPodium extends LocalSearchPhaseLifecycleListenerAdapter implements FinalistPodium {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected boolean finalistIsAccepted;
    protected List<LocalSearchMoveScope> finalistList;

    @Override
    public void stepStarted(LocalSearchStepScope stepScope) {
        super.stepStarted(stepScope);
        finalistIsAccepted = false;
        finalistList = new ArrayList<>(1024);
    }

    @Override
    public List<LocalSearchMoveScope> getFinalistList() {
        return finalistList;
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        finalistIsAccepted = false;
        finalistList = null;
    }

}
