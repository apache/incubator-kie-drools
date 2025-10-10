/*
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

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.ContextEntry;
import org.kie.api.runtime.rule.FactHandle;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * BiLinearContextEntry provides enhanced context management for BiLinearJoinNode
 * constraint evaluation. It maintains variable context from both input networks
 * and enables cross-network variable resolution during constraint evaluation.
 * 
 * This specialized context entry is essential for enabling complex join conditions
 * that reference variables from both input networks in a BiLinearJoinNode.
 */
public class BiLinearContextEntry implements ContextEntry {

    protected ContextEntry next;
    
    protected BaseTuple firstNetworkTuple;
    
    protected BaseTuple secondNetworkTuple;
    
    protected FactHandle rightHandle;
    
    protected BiLinearDeclarationContext declarationContext;
    
    protected transient ValueResolver valueResolver;
    
    protected BiLinearTuple combinedTuple;
    
    /**
     * Default constructor for serialization
     */
    public BiLinearContextEntry() {
    }

    public BiLinearContextEntry(BiLinearDeclarationContext declarationContext) {
        this.declarationContext = declarationContext;
    }
    
    @Override
    public ContextEntry getNext() {
        return next;
    }
    
    @Override
    public void setNext(ContextEntry entry) {
        this.next = entry;
    }

    @Override
    public void updateFromTuple(ValueResolver valueResolver, BaseTuple tuple) {
        this.valueResolver = valueResolver;
        
        if (tuple instanceof BiLinearTuple) {
            BiLinearTuple biLinearTuple = (BiLinearTuple) tuple;
            this.firstNetworkTuple = biLinearTuple.getFirstNetworkTuple();
            this.secondNetworkTuple = biLinearTuple.getSecondNetworkTuple();
            this.combinedTuple = biLinearTuple;
        } else {
            // Fallback for regular tuples - treat as first network
            this.firstNetworkTuple = tuple;
            this.secondNetworkTuple = null;
            this.combinedTuple = null;
        }
    }

    public void updateFromBiLinearTuples(ValueResolver valueResolver, 
                                       BaseTuple firstNetworkTuple, 
                                       BaseTuple secondNetworkTuple) {
        this.valueResolver = valueResolver;
        this.firstNetworkTuple = firstNetworkTuple;
        this.secondNetworkTuple = secondNetworkTuple;
        
        if (firstNetworkTuple instanceof TupleImpl && secondNetworkTuple instanceof TupleImpl) {
            this.combinedTuple = new BiLinearTuple(
                (TupleImpl) firstNetworkTuple,
                (TupleImpl) secondNetworkTuple,
                null, // No right fact handle for this context
                null  // No sink needed for context tuple
            );
        }
    }
    
    @Override
    public void updateFromFactHandle(ValueResolver valueResolver, FactHandle handle) {
        this.valueResolver = valueResolver;
        this.rightHandle = handle;
    }
    
    @Override
    public void resetTuple() {
        firstNetworkTuple = null;
        secondNetworkTuple = null;
        combinedTuple = null;
    }
    
    @Override
    public void resetFactHandle() {
        valueResolver = null;
        rightHandle = null;
    }

    public ValueResolver getValueResolver() {
        return valueResolver;
    }

    public BaseTuple getFirstNetworkTuple() {
        return firstNetworkTuple;
    }

    public FactHandle getRightHandle() {
        return rightHandle;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(next);
        out.writeObject(declarationContext);
    }
    
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        next = (ContextEntry) in.readObject();
        declarationContext = (BiLinearDeclarationContext) in.readObject();
    }
    
    @Override
    public String toString() {
        return "BiLinearContextEntry{" +
                "hasFirstNetwork=" + (firstNetworkTuple != null) +
                ", hasSecondNetwork=" + (secondNetworkTuple != null) +
                ", hasRightHandle=" + (rightHandle != null) +
                ", hasCombinedTuple=" + (combinedTuple != null) +
                '}';
    }
}