/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.beliefsystem;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.util.FastIterator;
import org.drools.core.util.LinkedListNode;
import org.drools.core.spi.PropagationContext;
import org.kie.internal.runtime.beliefs.Mode;

public interface BeliefSet<M extends ModedAssertion<M>> {
    public BeliefSystem getBeliefSystem();
    
    public InternalFactHandle getFactHandle();
    
    public M getFirst();

    public FastIterator iterator();

    
    public void add(M node);
    public void remove(M node);
    
    public boolean isEmpty();
    //public boolean isPropagated();
    public int size();

    /**
     *  This will remove all entries and do clean up, like retract FHs.
     * @param propagationContext
     */
    public void cancel(final PropagationContext propagationContext);

    /**
     * This will remove all entries, but not do cleanup, the FH is most likely needed else where
     * @param propagationContext
     */
    public void clear(PropagationContext propagationContext);
    
    public void setWorkingMemoryAction(WorkingMemoryAction wmAction);

    boolean isNegated();

    boolean isDecided();

    boolean isConflicting();

    boolean isPositive();

}
