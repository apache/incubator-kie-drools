/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.beliefsystem.simple;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.spi.PropagationContext;

public class SimpleBeliefSet extends LinkedList<SimpleMode> implements BeliefSet<SimpleMode> {
    protected BeliefSystem beliefSystem;
    
    protected InternalFactHandle fh;
    
    protected WorkingMemoryAction wmAction;
    
    public SimpleBeliefSet(BeliefSystem beliefSystem, InternalFactHandle fh) {
        this.beliefSystem = beliefSystem;
        this.fh = fh;
    }
    
    public BeliefSystem getBeliefSystem() {
        return beliefSystem;
    }

    public InternalFactHandle getFactHandle() {
        return this.fh;
    }

    public void cancel(PropagationContext context) {        
        // get all but last, as that we'll do via the BeliefSystem, for cleanup
        for ( SimpleMode entry = getFirst(); entry != getLast();  ) {
            SimpleMode temp = entry.getNext(); // get next, as we are about to remove it
            final LogicalDependency<SimpleMode> node = entry.getObject();
            node.getJustifier().getLogicalDependencies().remove( node );
            remove( entry );
            entry = temp;
        }
        
        LinkedListEntry last = getFirst();
        final LogicalDependency<SimpleMode> node = (LogicalDependency) last.getObject();
        node.getJustifier().getLogicalDependencies().remove( node );
        beliefSystem.delete( node, this, context );
    }
    
    public void clear(PropagationContext context) { 
        // remove all, but don't allow the BeliefSystem to clean up, the FH is most likely going to be used else where
        for ( SimpleMode entry = getFirst(); entry != null;  ) {
            SimpleMode temp = entry.getNext(); // get next, as we are about to remove it
            final LogicalDependency<SimpleMode> node = entry.getObject();
            node.getJustifier().getLogicalDependencies().remove( node );
            remove( entry );
            entry = temp;
        }
    }    

    public WorkingMemoryAction getWorkingMemoryAction() {
        return wmAction;
    }

    public void setWorkingMemoryAction(WorkingMemoryAction wmAction) {
        this.wmAction = wmAction;
    }

    @Override
    public boolean isNegated() {
        return false;
    }

    @Override
    public boolean isDecided() {
        return true;
    }

    @Override
    public boolean isConflicting() {
        return false;
    }

    @Override
    public boolean isPositive() {
        return ! isEmpty();
    }


}
