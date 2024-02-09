/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.kie.api.runtime.rule.FactHandle;

/**
 * A class to encapsulate behavior management for a given beta node
 */
public class BehaviorManager
    implements
    Externalizable {

    public static final BehaviorRuntime[] NO_BEHAVIORS = new BehaviorRuntime[0];

    private BehaviorRuntime[]             behaviors;

    public BehaviorManager() {
        this( NO_BEHAVIORS );
    }

    public BehaviorManager(List<BehaviorRuntime> behaviors) {
        super();
        this.behaviors = behaviors.toArray( new BehaviorRuntime[behaviors.size()]);
    }

    public BehaviorManager(BehaviorRuntime[] behaviors) {
        super();
        this.behaviors = behaviors;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        behaviors = (BehaviorRuntime[]) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( behaviors );
    }

    /**
     * Creates the behaviors' context 
     */
    public BehaviorContext[] createBehaviorContext() {
        BehaviorContext[] behaviorCtx = new BehaviorContext[behaviors.length];
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
                              final ReteEvaluator reteEvaluator) {
        boolean result = true;
        for ( int i = 0; i < behaviors.length; i++ ) {
            result = result && behaviors[i].assertFact( ((Object[]) behaviorContext)[i],
                                                        factHandle,
                                                        pctx,
                                                        reteEvaluator );
        }
        return result;
    }

    /**
     * Removes a newly asserted fact handle from the behaviors' context
     */
    public void retractFact(final Object behaviorContext,
                            final FactHandle factHandle,
                            final PropagationContext pctx,
                            final ReteEvaluator reteEvaluator) {
        for ( int i = 0; i < behaviors.length; i++ ) {
            behaviors[i].retractFact( ((Object[]) behaviorContext)[i],
                                      factHandle,
                                      pctx,
                                      reteEvaluator );
        }
    }

    /**
     * @return the behaviors
     */
    public BehaviorRuntime[] getBehaviors() {
        return behaviors;
    }

}
