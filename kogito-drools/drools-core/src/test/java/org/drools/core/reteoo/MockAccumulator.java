/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.Tuple;

/**
 * A Mock accumulate object.
 */
public class MockAccumulator
    implements
    Accumulator, Serializable {

    private static final long serialVersionUID = 510l;

    private Tuple             leftTuple        = null;
    private List              matchingObjects  = Collections.EMPTY_LIST;
    private WorkingMemory     workingMemory    = null;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        leftTuple   = (Tuple)in.readObject();
        matchingObjects = (List)in.readObject();
        workingMemory = (WorkingMemory)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(leftTuple);
        out.writeObject(matchingObjects);
        out.writeObject(workingMemory);
    }
    public Tuple getLeftTuple() {
        return this.leftTuple;
    }

    public List getMatchingObjects() {
        return this.matchingObjects;
    }

    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }

    public Serializable createContext() {
        return this;
    }

    public void init(Object workingMemoryContext,
                     Object context,
                     Tuple leftTuple,
                     Declaration[] declarations,
                     WorkingMemory workingMemory) throws Exception {
        this.leftTuple = leftTuple;
        this.matchingObjects = new ArrayList();
        this.workingMemory = workingMemory;
    }

    public void accumulate(Object workingMemoryContext,
                           Object context,
                           Tuple leftTuple,
                           InternalFactHandle handle,
                           Declaration[] declarations,
                           Declaration[] innerDeclarations,
                           WorkingMemory workingMemory) throws Exception {
        this.matchingObjects.add( handle.getObject() );
    }

    public Object getResult(Object workingMemoryContext,
                            Object context,
                            Tuple leftTuple,
                            Declaration[] declarations,
                            WorkingMemory workingMemory) throws Exception {
        return this.matchingObjects;
    }

    public void reverse(Object workingMemoryContext,
                        Object context,
                        Tuple leftTuple,
                        InternalFactHandle handle,
                        Declaration[] declarations,
                        Declaration[] innerDeclarations,
                        WorkingMemory workingMemory) throws Exception {
        // nothing to do yet
    }

    public boolean supportsReverse() {
        return false;
    }

    public Object createWorkingMemoryContext( ) {
        return null;
    }

}
