/**
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

package org.drools.planner.core.localsearch.decider.acceptor;

import org.drools.planner.core.localsearch.LocalSearchSolverLifecycleListener;
import org.drools.planner.core.localsearch.decider.MoveScope;

/**
 * An Acceptor accepts or rejects a selected move for the Decider.
 * Notice that the Forager can still ignore the advice of the Acceptor.
 * @see AbstractAcceptor
 * @author Geoffrey De Smet
 */
public interface Acceptor extends LocalSearchSolverLifecycleListener {

    /**
     * @param moveScope not null
     * @return never negative; if rejected 0.0; if accepted higher than 0.0 (ussually 1.0)
     */
    double calculateAcceptChance(MoveScope moveScope);

}
