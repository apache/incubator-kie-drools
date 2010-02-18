package org.drools.common;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.util.ObjectHashMap;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;
import org.drools.FactHandle;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;

public class PropagationContextImpl
    implements
    PropagationContext {

    private static final long  serialVersionUID = 8400185220119865618L;

    private int                type;

    private Rule               rule;

    private LeftTuple          leftTuple;

    private InternalFactHandle factHandle;

    private long               propagationNumber;

    public int                 activeActivations;

    public int                 dormantActivations;

    public ObjectHashMap       retracted;

    private EntryPoint         entryPoint;

    public PropagationContextImpl() {

    }

    public PropagationContextImpl(final long number,
                                  final int type,
                                  final Rule rule,
                                  final LeftTuple leftTuple,
                                  final InternalFactHandle factHandle) {
        this.type = type;
        this.rule = rule;
        this.leftTuple = leftTuple;
        this.factHandle = factHandle;
        this.propagationNumber = number;
        this.activeActivations = 0;
        this.dormantActivations = 0;
        this.entryPoint = EntryPoint.DEFAULT;
    }

    public PropagationContextImpl(final long number,
                                  final int type,
                                  final Rule rule,
                                  final LeftTuple leftTuple,
                                  final InternalFactHandle factHandle,
                                  final int activeActivations,
                                  final int dormantActivations,
                                  final EntryPoint entryPoint) {
        this.type = type;
        this.rule = rule;
        this.leftTuple = leftTuple;
        this.factHandle = factHandle;
        this.propagationNumber = number;
        this.activeActivations = activeActivations;
        this.dormantActivations = dormantActivations;
        this.entryPoint = entryPoint;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.type = in.readInt();
        this.activeActivations = in.readInt();
        this.dormantActivations = in.readInt();
        this.propagationNumber = in.readLong();
        this.rule = (Rule) in.readObject();
        this.leftTuple = (LeftTuple) in.readObject();
        this.retracted = (ObjectHashMap) in.readObject();
        this.entryPoint = (EntryPoint) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt( this.type );
        out.writeInt( this.activeActivations );
        out.writeInt( this.dormantActivations );
        out.writeLong( this.propagationNumber );
        out.writeObject( this.rule );
        out.writeObject( this.leftTuple );
        out.writeObject( this.retracted );
        out.writeObject( this.entryPoint );
    }

    public long getPropagationNumber() {
        return this.propagationNumber;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.drools.reteoo.PropagationContext#getRuleOrigin()
     */
    public Rule getRuleOrigin() {
        return this.rule;
    }
    
    public org.drools.definition.rule.Rule getRule() {
        return this.rule;
    }

    public LeftTuple getLeftTupleOrigin() {
        return this.leftTuple;
    }

    public InternalFactHandle getFactHandleOrigin() {
        return this.factHandle;
    }
    
    public FactHandle getFactHandle() {
        return this.factHandle;
    }    

    /*
     * (non-Javadoc)
     *
     * @see org.drools.reteoo.PropagationContext#getType()
     */
    public int getType() {
        return this.type;
    }

    public int getActiveActivations() {
        return this.activeActivations;
    }

    public int getDormantActivations() {
        return this.dormantActivations;
    }

    public void addRetractedTuple(final Rule rule,
                                  final Activation activation) {
        if ( this.retracted == null ) {
            this.retracted = new ObjectHashMap();
        }

        LeftTuple tuple = (LeftTuple) activation.getTuple();

        ObjectHashMap tuples = (ObjectHashMap) this.retracted.get( rule );
        if ( tuples == null ) {
            tuples = new ObjectHashMap();
            this.retracted.put( rule,
                                tuples );
        }
        tuples.put( tuple,
                    activation );
    }

    public Activation removeRetractedTuple(final Rule rule,
                                           final LeftTuple tuple) {
        if ( this.retracted == null ) {
            return null;
        }

        final ObjectHashMap tuples = (ObjectHashMap) this.retracted.get( rule );
        if ( tuples != null ) {
            return (Activation) tuples.remove( tuple );
        } else {
            return null;
        }
    }

    public void clearRetractedTuples() {
        this.retracted = null;
    }

    public void releaseResources() {
        this.leftTuple = null;
        this.retracted = null;
        this.rule = null;
    }

    /**
     * @return the entryPoint
     */
    public EntryPoint getEntryPoint() {
        return entryPoint;
    }

    /**
     * @param entryPoint the entryPoint to set
     */
    public void setEntryPoint(EntryPoint entryPoint) {
        this.entryPoint = entryPoint;
    }

    public void setFactHandle(InternalFactHandle factHandle) {
        this.factHandle = factHandle;
    }
}
