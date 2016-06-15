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

package org.drools.core.common;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.SegmentMemory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * A concurrent implementation for the node memories interface
 */
public class ConcurrentNodeMemories implements NodeMemories {

    private AtomicReferenceArray<Memory> memories;

    private final Lock lock = new ReentrantLock();
    private final InternalKnowledgeBase kBase;
    private final String unitName;

    public ConcurrentNodeMemories( InternalKnowledgeBase kBase, String unitName ) {
        this.kBase = kBase;
        this.unitName = unitName;
        this.memories = new AtomicReferenceArray<Memory>( this.kBase.getMemoryCount(unitName) );
    }

    public void clearNodeMemory( MemoryFactory node ) {
        if ( peekNodeMemory(node.getMemoryId()) != null ) {
            this.memories.set(node.getMemoryId(), null);
        }
    }
    
    public void clear() {
        this.memories = new AtomicReferenceArray<Memory>( this.kBase.getMemoryCount(unitName) );
    }

    public void resetAllMemories(StatefulKnowledgeSession session) {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase)session.getKieBase();
        Set<SegmentMemory> smems = new HashSet<SegmentMemory>();

        for (int i = 0; i < memories.length(); i++) {
            Memory memory = memories.get(i);
            if (memory != null) {
                if (memory.getSegmentMemory() != null) {
                    smems.add(memory.getSegmentMemory());
                }
                memory.reset();
            }
        }

        for (SegmentMemory smem : smems) {
            smem.reset(kBase.getSegmentPrototype(smem));
            if ( smem.isSegmentLinked() ) {
                smem.notifyRuleLinkSegment((InternalWorkingMemory)session);
            }
        }
    }

    /**
     * The implementation tries to delay locking as much as possible, by running
     * some potentially unsafe operations out of the critical session. In case it
     * fails the checks, it will move into the critical sessions and re-check everything
     * before effectively doing any change on data structures. 
     */
    public Memory getNodeMemory(MemoryFactory node, InternalWorkingMemory wm) {
        if( node.getMemoryId() >= this.memories.length() ) {
            resize( node );
        }
        Memory memory = this.memories.get( node.getMemoryId() );

        if( memory == null ) {
            memory = createNodeMemory( node, wm );
        }

        return memory;
    }


    /**
     * Checks if a memory does not exists for the given node and
     * creates it.
     */
    private Memory createNodeMemory( MemoryFactory node,
                                     InternalWorkingMemory wm ) {
        try {
            this.lock.lock();
            // need to try again in a synchronized code block to make sure
            // it was not created yet
            Memory memory = this.memories.get( node.getMemoryId() );
            if( memory == null ) {
                memory = node.createMemory( this.kBase.getConfiguration(), wm );

                if( !this.memories.compareAndSet( node.getMemoryId(), null, memory ) ) {
                    memory = this.memories.get( node.getMemoryId() );
                }

            }
            return memory;
        } finally {
            this.lock.unlock();

        }
    }

    /**
     * @param node
     */
    private void resize( MemoryFactory node ) {
        try {
            this.lock.lock();
            if( node.getMemoryId() >= this.memories.length() ) {
                // adding some buffer for new nodes, so that we reduce array copies
                int size = Math.max( this.kBase.getMemoryCount(unitName), node.getMemoryId() + 32 );
                AtomicReferenceArray<Memory> newMem = new AtomicReferenceArray<Memory>( size );
                for ( int i = 0; i < this.memories.length(); i++ ) {
                    newMem.set( i,
                                this.memories.get( i ) );
                }
                this.memories = newMem;
            }
        } finally {
            this.lock.unlock();
        }
    }

    public Memory peekNodeMemory(int memoryId ) {
        if ( memoryId < this.memories.length() ) {
            return this.memories.get( memoryId );
        } else {
            return null;
        }
    }

    public int length() {
        return this.memories.length();
    }

}
