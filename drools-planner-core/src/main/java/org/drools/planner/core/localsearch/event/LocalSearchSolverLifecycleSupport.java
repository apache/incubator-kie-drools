/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.core.localsearch.event;

import java.util.Iterator;

import org.drools.event.AbstractEventSupport;
import org.drools.planner.core.localsearch.LocalSearchSolverScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;

/**
 * Internal API.
 */
public class LocalSearchSolverLifecycleSupport extends AbstractEventSupport<LocalSearchSolverLifecycleListener> {

    public void fireSolvingStarted(LocalSearchSolverScope localSearchSolverScope) {
        final Iterator<LocalSearchSolverLifecycleListener> iter = getEventListenersIterator();
        while (iter.hasNext()) {
            iter.next().solvingStarted(localSearchSolverScope);
        }
    }

    public void fireBeforeDeciding(LocalSearchStepScope localSearchStepScope) {
        final Iterator<LocalSearchSolverLifecycleListener> iter = getEventListenersIterator();
        while (iter.hasNext()) {
            iter.next().beforeDeciding(localSearchStepScope);
        }
    }

    public void fireStepDecided(LocalSearchStepScope localSearchStepScope) {
        final Iterator<LocalSearchSolverLifecycleListener> iter = getEventListenersIterator();
        while (iter.hasNext()) {
            iter.next().stepDecided(localSearchStepScope);
        }
    }

    public void fireStepTaken(LocalSearchStepScope localSearchStepScope) {
        final Iterator<LocalSearchSolverLifecycleListener> iter = getEventListenersIterator();
        while (iter.hasNext()) {
            iter.next().stepTaken(localSearchStepScope);
        }
    }

    public void fireSolvingEnded(LocalSearchSolverScope localSearchSolverScope) {
        final Iterator<LocalSearchSolverLifecycleListener> iter = getEventListenersIterator();
        while (iter.hasNext()) {
            iter.next().solvingEnded(localSearchSolverScope);
        }
    }

}
