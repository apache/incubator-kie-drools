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
 * Created on Jan 15, 2008
 */

package org.drools.common;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * A concurrent implementation for the node memories interface
 *
 * @author etirelli
 */
public class ConcurrentNodeMemories
    implements
    NodeMemories {

    private static final long serialVersionUID = -2032997426288974117L;

    private AtomicReferenceArray<Object> memories;
    private Lock                         lock;
    private InternalRuleBase   rulebase;

    public ConcurrentNodeMemories() {

    }

    public ConcurrentNodeMemories(InternalRuleBase rulebase) {
        this.rulebase = rulebase;
        this.memories = new AtomicReferenceArray<Object>( this.rulebase.getNodeCount() );
        this.lock = new ReentrantLock();
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        lock        = (Lock)in.readObject();
        rulebase    = (InternalRuleBase)in.readObject();
        memories    = (AtomicReferenceArray<Object>)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(lock);
        out.writeObject(rulebase);
        out.writeObject(memories);
    }
    /**
     * @inheritDoc
     *
     * @see org.drools.common.NodeMemories#clearNodeMemory(org.drools.common.NodeMemory)
     */
    public void clearNodeMemory(NodeMemory node) {
        this.memories.set( node.getId(), null );
    }

    /**
     * @inheritDoc
     *
     * @see org.drools.common.NodeMemories#getNodeMemory(org.drools.common.NodeMemory)
     */
    public Object getNodeMemory(NodeMemory node) {
        if ( node.getId() >= this.memories.length() ) {
            resize( node );
        }
        Object memory = this.memories.get( node.getId() );

        if ( memory == null ) {
            memory = node.createMemory( this.rulebase.getConfiguration() );

            if( !this.memories.compareAndSet( node.getId(), null, memory ) ) {
                memory = this.memories.get( node.getId() );
            }
        }

        return memory;
    }

    /**
     * @param node
     */
    private void resize(NodeMemory node) {
        this.lock.lock();
        try {
            if( node.getId() >= this.memories.length() ) {
                int size = Math.max( this.rulebase.getNodeCount(),
                                     node.getId() + 1 );
                AtomicReferenceArray<Object> newMem = new AtomicReferenceArray<Object>( size );
                for( int i = 0; i < this.memories.length(); i++ ) {
                    newMem.set( i, this.memories.get( i ) );
                }
                this.memories = newMem;
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void setRuleBaseReference(InternalRuleBase ruleBase) {
        this.rulebase = ruleBase;
    }

}
