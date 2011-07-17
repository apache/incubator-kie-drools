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

package org.drools.base;

import java.util.ArrayList;
import java.util.List;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.QueryTerminalNode;
import org.drools.rule.Rule;
import org.drools.spi.PropagationContext;

public class NonCloningQueryViewListener 
    implements
    InternalViewChangedEventListener {

    private List<Object> results;

    public NonCloningQueryViewListener() {
        this.results = new ArrayList<Object>( 250 );
    }

    public List<? extends Object> getResults() {
        return this.results;
    }

    public void rowAdded(final Rule rule,
                         final LeftTuple tuple,
                         final PropagationContext context,
                         final InternalWorkingMemory workingMemory) {
        InternalFactHandle[] handles = new InternalFactHandle[tuple.getIndex() + 1];
        LeftTuple entry = tuple;

        // Add all the FactHandles except the root DroolQuery object
        while ( entry.getIndex() > 0 ) {
            InternalFactHandle handle = entry.getLastHandle();
            handles[entry.getIndex()] = handle;
            entry = entry.getParent();
        }

        InternalFactHandle handle = entry.getLastHandle();
        handles[entry.getIndex()] = handle;
        QueryTerminalNode node = ( QueryTerminalNode ) tuple.getLeftTupleSink();     
        this.results.add( new QueryRowWithSubruleIndex(handles, node.getSubruleIndex()) );
    }
    
    public void rowRemoved(final Rule rule,
                           final LeftTuple tuple,
            final PropagationContext context,
            final InternalWorkingMemory workingMemory) {
    }
    
    public void rowUpdated(final Rule rule,
                           final LeftTuple tuple,
            final PropagationContext context,
            final InternalWorkingMemory workingMemory) {
    }

}
