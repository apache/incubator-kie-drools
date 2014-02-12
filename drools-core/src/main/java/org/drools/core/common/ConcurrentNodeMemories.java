/*
 * Copyright 2010 JBoss Inc
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

import org.drools.core.impl.InternalKnowledgeBase;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A concurrent implementation for the node memories interface
 */
public class ConcurrentNodeMemories implements NodeMemories {

    private AtomicReferenceArray<Memory> memories;

    private Lock                         lock;
    private InternalKnowledgeBase        kBase;

    public ConcurrentNodeMemories( InternalKnowledgeBase kBase ) {
        this.kBase = kBase;
        this.memories = new AtomicReferenceArray<Memory>( this.kBase.getNodeCount() );
        this.lock = new ReentrantLock();
    }

    public void clearNodeMemory( MemoryFactory node ) {
        this.memories.set( node.getId(),
                           null );
    }
    
    public void clear() {
        this.memories = new AtomicReferenceArray<Memory>( this.kBase.getNodeCount() );
    }

    /**
     * The implementation tries to delay locking as much as possible, by running
     * some potentialy unsafe opperations out of the critical session. In case it
     * fails the checks, it will move into the critical sessions and re-check everything
     * before effectively doing any change on data structures. 
     */
    public Memory getNodeMemory(MemoryFactory node, InternalWorkingMemory wm) {
        if( node.getId() >= this.memories.length() ) {
            resize( node );
        }
        Memory memory = this.memories.get( node.getId() );

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
            Memory memory = this.memories.get( node.getId() );
            if( memory == null ) {
                memory = node.createMemory( this.kBase.getConfiguration(), wm );

                if( !this.memories.compareAndSet( node.getId(), null, memory ) ) {
                    memory = this.memories.get( node.getId() );
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
            if( node.getId() >= this.memories.length() ) {
                // adding some buffer for new nodes, so that we reduce array copies
                int size = Math.max( this.kBase.getNodeCount(), node.getId() + 32 );
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

    public void setKnowledgeBaseReference( InternalKnowledgeBase kBase ) {
        this.kBase = kBase;
    }

    public Memory peekNodeMemory(int nodeId) {
        return this.memories.get( nodeId );
    }

    public int length() {
        return this.memories.length();
    }

}
