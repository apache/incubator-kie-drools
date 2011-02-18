/**
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

package org.drools.common;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.FactHandle;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;
import org.drools.spi.PropagationContext;

public class PropagationContextImpl
    implements
    PropagationContext {

    private static final long  serialVersionUID = 510l;

    private int                type;

    private Rule               rule;

    private LeftTuple          leftTuple;

    private InternalFactHandle factHandle;

    private long               propagationNumber;

    public int                 activeActivations;

    public int                 dormantActivations;

    private EntryPoint         entryPoint;
    
    private int                originOffset;

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
        this.originOffset = -1;
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
        this.originOffset = -1;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.type = in.readInt();
        this.activeActivations = in.readInt();
        this.dormantActivations = in.readInt();
        this.propagationNumber = in.readLong();
        this.rule = (Rule) in.readObject();
        this.leftTuple = (LeftTuple) in.readObject();
        this.entryPoint = (EntryPoint) in.readObject();
        this.originOffset = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt( this.type );
        out.writeInt( this.activeActivations );
        out.writeInt( this.dormantActivations );
        out.writeLong( this.propagationNumber );
        out.writeObject( this.rule );
        out.writeObject( this.leftTuple );
        out.writeObject( this.entryPoint );
        out.writeInt( this.originOffset );
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

    public void releaseResources() {
        this.leftTuple = null;
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
    
    public int getOriginOffset() {
        return originOffset;
    }

    public void setOriginOffset(int originOffset) {
        this.originOffset = originOffset;
    }

    @Override
    public String toString() {
        return "PropagationContextImpl [activeActivations=" + activeActivations + ", dormantActivations=" + dormantActivations + ", entryPoint=" + entryPoint + ", factHandle=" + factHandle + ", leftTuple=" + leftTuple + ", originOffset="
               + originOffset + ", propagationNumber=" + propagationNumber + ", rule=" + rule + ", type=" + type + "]";
    }

}
