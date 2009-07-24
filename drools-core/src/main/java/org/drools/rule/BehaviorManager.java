/*
 * Copyright 2008 JBoss Inc
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
 *
 * Created on May 12, 2008
 */

package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.RightTuple;

/**
 * A class to encapsulate behavior management for a given beta node
 * 
 * @author etirelli
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
     *  
     * @param context
     * @param tuple
     * @return
     */
    public boolean assertRightTuple(final Object behaviorContext,
                                    final RightTuple rightTuple,
                                    final InternalWorkingMemory workingMemory) {
        boolean result = true;
        for ( int i = 0; i < behaviors.length; i++ ) {
            result = result && behaviors[i].assertRightTuple( ((Object[]) behaviorContext)[i],
                                                              rightTuple,
                                                              workingMemory );
        }
        return result;
    }

    /**
     * Removes a newly asserted right tuple from the behaviors' context
     * @param behaviorContext
     * @param rightTuple
     * @param workingMemory
     */
    public void retractRightTuple(final Object behaviorContext,
                                  final RightTuple rightTuple,
                                  final InternalWorkingMemory workingMemory) {
        for ( int i = 0; i < behaviors.length; i++ ) {
            behaviors[i].retractRightTuple( ((Object[]) behaviorContext)[i],
                                            rightTuple,
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
