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

package org.drools.common;

import org.drools.FactHandle;
import org.drools.base.ClassObjectType;
import org.drools.core.util.BitMaskUtil;
import org.drools.core.util.ObjectHashSet;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.WindowTupleList;
import org.drools.rule.*;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
    
    private ObjectHashSet      propagationAttempts;

    private ObjectTypeNode     currentPropagatingOTN;
    
    private boolean            shouldPropagateAll;
    
    private final LinkedList<WorkingMemoryAction> queue1 = new LinkedList<WorkingMemoryAction>(); // for inserts
    
    private LinkedList<WorkingMemoryAction> queue2; // for evaluations and fixers

    private long               modificationMask = Long.MAX_VALUE;
    private long               originalMask = Long.MAX_VALUE;

    private Class<?>           modifiedClass;

    private WindowTupleList    windowTupleList;

    private ObjectType         objectType;
    
    // this field is only set for propagations happening during 
    // the deserialization of a session
    private transient MarshallerReaderContext readerContext;

    public PropagationContextImpl() {

    }

    public PropagationContextImpl(final long number,
                                  final int type,
                                  final Rule rule,
                                  final LeftTuple leftTuple,
                                  final InternalFactHandle factHandle) {
        this(number, 
             type, 
             rule, 
             leftTuple, 
             factHandle, 
             0, 
             0, 
             EntryPoint.DEFAULT,
             Long.MAX_VALUE,
             Object.class,
             null );
        this.originOffset = -1;
        this.shouldPropagateAll = true;
    }

    public PropagationContextImpl(final long number,
                                  final int type,
                                  final Rule rule,
                                  final LeftTuple leftTuple,
                                  final InternalFactHandle factHandle,
                                  final int activeActivations,
                                  final int dormantActivations,
                                  final EntryPoint entryPoint) {
        this(number, 
             type, 
             rule, 
             leftTuple, 
             factHandle, 
             activeActivations, 
             dormantActivations, 
             entryPoint,
             Long.MAX_VALUE,
             Object.class,
             null );
    }

    public PropagationContextImpl(final long number,
                                  final int type,
                                  final Rule rule,
                                  final LeftTuple leftTuple,
                                  final InternalFactHandle factHandle,
                                  final int activeActivations,
                                  final int dormantActivations,
                                  final EntryPoint entryPoint,
                                  final long modificationMask) {
        this(number, 
             type, 
             rule, 
             leftTuple, 
             factHandle, 
             activeActivations, 
             dormantActivations, 
             entryPoint,
             modificationMask,
             Object.class,
             null );
    }

    public PropagationContextImpl(final long number,
                                  final int type,
                                  final Rule rule,
                                  final LeftTuple leftTuple,
                                  final InternalFactHandle factHandle,
                                  final int activeActivations,
                                  final int dormantActivations,
                                  final EntryPoint entryPoint,
                                  final MarshallerReaderContext readerContext) {
        this(number, 
             type, 
             rule, 
             leftTuple, 
             factHandle, 
             activeActivations, 
             dormantActivations, 
             entryPoint,
             Long.MAX_VALUE,
             Object.class,
             readerContext );
    }

    public PropagationContextImpl(final long number,
                                  final int type,
                                  final Rule rule,
                                  final LeftTuple leftTuple,
                                  final InternalFactHandle factHandle,
                                  final int activeActivations,
                                  final int dormantActivations,
                                  final EntryPoint entryPoint,
                                  final long modificationMask,
                                  final Class<?> modifiedClass,
                                  final MarshallerReaderContext readerContext ) {
        this.type = type;
        this.rule = rule;
        this.leftTuple = leftTuple;
        this.factHandle = factHandle;
        this.propagationNumber = number;
        this.activeActivations = activeActivations;
        this.dormantActivations = dormantActivations;
        this.entryPoint = entryPoint;
        this.originOffset = -1;
        this.modificationMask = modificationMask;
        this.originalMask = modificationMask;
        this.modifiedClass = modifiedClass;
        this.readerContext = readerContext;
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
        this.propagationAttempts = (ObjectHashSet) in.readObject();
        this.currentPropagatingOTN = (ObjectTypeNode) in.readObject();
        this.shouldPropagateAll = in.readBoolean();        
        this.modificationMask = in.readLong();
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
        out.writeObject( this.propagationAttempts );
        out.writeObject( this.currentPropagatingOTN );
        out.writeObject( this.shouldPropagateAll );
        out.writeLong( this.modificationMask );
    }

    public long getPropagationNumber() {
        return this.propagationNumber;
    }

    public void cleanReaderContext() {
        readerContext = null;
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
    
    public ObjectHashSet getPropagationAttemptsMemory() {
        
        if (this.propagationAttempts == null) {
            this.propagationAttempts = new ObjectHashSet();
        }
        
        return this.propagationAttempts;
    }
    
    public boolean isPropagating(ObjectTypeNode otn) {
        return this.currentPropagatingOTN != null
               && this.currentPropagatingOTN.equals( otn );
    }
    
    public void setCurrentPropagatingOTN(ObjectTypeNode otn) {
        this.currentPropagatingOTN = otn;
    }

    public void setShouldPropagateAll(Object node) {
        this.shouldPropagateAll = getPropagationAttemptsMemory().contains( node );
    }

    public boolean shouldPropagateAll() {
        return this.shouldPropagateAll;
    }

    public void addInsertAction(WorkingMemoryAction action) {
        synchronized (queue1) {
            queue1.addFirst(action);
        }
    }

    public void removeInsertAction(WorkingMemoryAction action) {
        synchronized (queue1) {
            queue1.remove(action);
        }
    }

    public LinkedList<WorkingMemoryAction> getQueue2() {
        if ( this.queue2 == null ) {
            this.queue2 = new LinkedList<WorkingMemoryAction>();
        }
        return this.queue2; 
    }   
    
    public void evaluateActionQueue(InternalWorkingMemory workingMemory) {
        boolean repeat = true;
        while(repeat) {
            synchronized (queue1) {
                WorkingMemoryAction action;
                while ( (action = (!queue1.isEmpty()) ? queue1.removeFirst() : null ) != null ) {
                    action.execute( workingMemory );
                }
            }
            
            repeat = false;
            if ( this.queue2 != null ) {
                WorkingMemoryAction action;

                while ( (action = (!queue2.isEmpty()) ? queue2.removeFirst() : null) != null ) {
                    action.execute( workingMemory );
                    if ( !this.queue1.isEmpty() ) {
                        // Queue1 always takes priority and it's contents should be evaluated first
                        repeat = true;
                        break;
                    }
                }
            }     
                                  
        }
    }

    public long getModificationMask() {
        return modificationMask;
    }

    public PropagationContext adaptModificationMaskForObjectType(ObjectType type, InternalWorkingMemory workingMemory) {
        modificationMask = originalMask;
        if (modificationMask == Long.MAX_VALUE || !(type instanceof ClassObjectType)) {
            return this;
        }
        ClassObjectType classObjectType = (ClassObjectType)type;
        Class<?> classType = classObjectType.getClassType();
        if (classType == modifiedClass || !(classType.isInterface() || modifiedClass.isInterface())) {
            return this;
        }

        Long cachedMask = classObjectType.getTransformedMask(modifiedClass, originalMask);
        if (cachedMask != null) {
            modificationMask = cachedMask;
            return this;
        }

        modificationMask = 0L;
        List<String> typeClassProps = getSettableProperties( workingMemory, classType );
        List<String> modifiedClassProps = getSettableProperties( workingMemory, modifiedClass );

        for (int i = 0; i < modifiedClassProps.size(); i++) {
            if (BitMaskUtil.isPositionSet(originalMask, i)) {
                int posInType = typeClassProps.indexOf(modifiedClassProps.get(i));
                if (posInType >= 0) {
                    modificationMask = BitMaskUtil.set(modificationMask, posInType);
                }
            }
        }
        classObjectType.storeTransformedMask(modifiedClass, originalMask, modificationMask);

        return this;
    }

    private List<String> getSettableProperties(InternalWorkingMemory workingMemory, Class<?> classType) {
        String pkgName = classType.getPackage().getName();
        return "java.lang".equals(pkgName) ?
                Collections.EMPTY_LIST :
                workingMemory.getRuleBase().getPackage(pkgName).getTypeDeclaration(classType).getSettableProperties();
    }

    public WindowTupleList getActiveWindowTupleList() {
        return windowTupleList;
    }

    public void setActiveWindowTupleList( WindowTupleList list ) {
        this.windowTupleList = list;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }
    
    public MarshallerReaderContext getReaderContext() {
        return this.readerContext;
    }

    @Override
    public String toString() {
        return "PropagationContextImpl [activeActivations=" + activeActivations + ", dormantActivations=" + dormantActivations + ", entryPoint=" + entryPoint + ", factHandle=" + factHandle + ", leftTuple=" + leftTuple + ", originOffset="
               + originOffset + ", propagationNumber=" + propagationNumber + ", rule=" + rule + ", type=" + type + "]";
    }
}
