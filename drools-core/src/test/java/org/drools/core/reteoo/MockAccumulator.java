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
package org.drools.core.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.Accumulator;
import org.kie.api.runtime.rule.FactHandle;

/**
 * A Mock accumulate object.
 */
public class MockAccumulator
    implements
    Accumulator, Serializable {

    private static final long serialVersionUID = 510l;

    private BaseTuple leftTuple        = null;
    private List              matchingObjects  = Collections.EMPTY_LIST;
    private ValueResolver     valueResolver    = null;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        leftTuple   = (Tuple)in.readObject();
        matchingObjects = (List)in.readObject();
        valueResolver = (ValueResolver)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(leftTuple);
        out.writeObject(matchingObjects);
        out.writeObject(valueResolver);
    }
    public BaseTuple getLeftTuple() {
        return this.leftTuple;
    }

    public List getMatchingObjects() {
        return this.matchingObjects;
    }

    public ValueResolver getReteEvaluator() {
        return this.valueResolver;
    }

    public Object createContext() {
        return this;
    }

    public Object init(Object workingMemoryContext,
                       Object context,
                       BaseTuple leftTuple,
                       Declaration[] declarations,
                       ValueResolver valueResolver) {
        this.leftTuple = leftTuple;
        this.matchingObjects = new ArrayList();
        this.valueResolver = valueResolver;
        return context;
    }

    public Object accumulate(Object workingMemoryContext,
                           Object context,
                           BaseTuple leftTuple,
                           FactHandle handle,
                           Declaration[] declarations,
                           Declaration[] innerDeclarations,
                           ValueResolver valueResolver) {
        this.matchingObjects.add( handle.getObject() );
        return handle.getObject();
    }

    public Object getResult(Object workingMemoryContext,
                            Object context,
                            BaseTuple leftTuple,
                            Declaration[] declarations,
                            ValueResolver valueResolver) {
        return this.matchingObjects;
    }

    public boolean tryReverse(Object workingMemoryContext,
                              Object context,
                              BaseTuple leftTuple,
                              FactHandle handle,
                              Object value,
                              Declaration[] declarations,
                              Declaration[] innerDeclarations,
                              ValueResolver valueResolver) {
        return false;
    }

    public boolean supportsReverse() {
        return false;
    }

    public Object createWorkingMemoryContext( ) {
        return null;
    }

}
