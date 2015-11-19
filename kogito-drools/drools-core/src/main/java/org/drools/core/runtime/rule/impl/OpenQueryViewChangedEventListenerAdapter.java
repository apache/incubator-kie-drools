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

package org.drools.core.runtime.rule.impl;

import java.util.List;

import org.drools.core.base.InternalViewChangedEventListener;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.spi.PropagationContext;
import org.kie.api.runtime.rule.ViewChangedEventListener;

public class OpenQueryViewChangedEventListenerAdapter
    implements
    InternalViewChangedEventListener {

    private ViewChangedEventListener viewEventListener;
    
    public OpenQueryViewChangedEventListenerAdapter(ViewChangedEventListener viewEventListener) {
        this.viewEventListener = viewEventListener;
    }

    public void rowAdded(final RuleImpl rule,
                         final LeftTuple leftTuple,
                         final PropagationContext context,
                         final InternalWorkingMemory workingMemory) {
        RowAdapter rowAdapter = new RowAdapter(rule,
                                               leftTuple);
        leftTuple.setContextObject( rowAdapter );
        this.viewEventListener.rowInserted( rowAdapter );
    }

    public void rowRemoved(final RuleImpl rule,
                           final LeftTuple leftTuple,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        RowAdapter rowAdapter = (RowAdapter) leftTuple.getContextObject();
        this.viewEventListener.rowDeleted( rowAdapter );
    }

    public void rowUpdated(final RuleImpl rule,
                           final LeftTuple leftTuple,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        RowAdapter rowAdapter = (RowAdapter) leftTuple.getContextObject();
        this.viewEventListener.rowUpdated( rowAdapter );
    }

    public List< ? extends Object> getResults() {
        throw new UnsupportedOperationException(getClass().getCanonicalName()+" does not support the getResults() method.");
    }

}
