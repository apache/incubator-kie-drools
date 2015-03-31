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

package org.drools.core.rule;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.spi.PropagationContext;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

/**
 * A class to encapsulate behavior management for a given beta node
 */
public class BehaviorManager
    implements
    Externalizable {

    public static final Behavior[] NO_BEHAVIORS = new Behavior[0];

    private Behavior[]             behaviors;

    public BehaviorManager() {
        this( NO_BEHAVIORS );
    }

    /**
     * @param behaviors
     */
    public BehaviorManager(List<Behavior> behaviors) {
        super();
        this.behaviors = behaviors.toArray( new Behavior[behaviors.size()] );
    }

    /**
     * @param behaviors
     */
    public BehaviorManager(Behavior[] behaviors) {
        super();
        this.behaviors = behaviors;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        behaviors = (Behavior[]) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( behaviors );
    }

    /**
     * Creates the behaviors' context 
     * 
     * @return
     */
    public Object createBehaviorContext() {
        Object[] behaviorCtx = new Object[behaviors.length];
        for ( int i = 0; i < behaviors.length; i++ ) {
            behaviorCtx[i] = behaviors[i].createContext();
        }
        return behaviorCtx;
    }

    /**
     * Register a newly asserted right tuple into the behaviors' context
     */
    public boolean assertFact(final Object behaviorContext,
                              final InternalFactHandle factHandle,
                              final PropagationContext pctx,
                              final InternalWorkingMemory workingMemory) {
        boolean result = true;
        for ( int i = 0; i < behaviors.length; i++ ) {
            result = result && behaviors[i].assertFact( ((Object[]) behaviorContext)[i],
                                                        factHandle,
                                                        pctx,
                                                        workingMemory );
        }
        return result;
    }

    /**
     * Removes a newly asserted fact handle from the behaviors' context
     */
    public void retractFact(final Object behaviorContext,
                            final InternalFactHandle factHandle,
                            final PropagationContext pctx,
                            final InternalWorkingMemory workingMemory) {
        for ( int i = 0; i < behaviors.length; i++ ) {
            behaviors[i].retractFact( ((Object[]) behaviorContext)[i],
                                      factHandle,
                                      pctx,
                                      workingMemory );
        }
    }

    /**
     * @return the behaviors
     */
    public Behavior[] getBehaviors() {
        return behaviors;
    }

}
