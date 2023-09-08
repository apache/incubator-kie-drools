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
package org.drools.core.common;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.rule.accessor.FactHandleFactory;

import static java.util.stream.Collectors.toCollection;

public abstract class AbstractFactHandleFactory implements FactHandleFactory  {

    /** The fact id. */
    private IdsGenerator idGen;

    /** The number of facts created - used for recency. */
    private AtomicLong counter;
    
    public AbstractFactHandleFactory() {
        // starts at 0. So first assigned is 1.
        // 0 is hard coded to Initialfact
        this.idGen = new IdsGenerator(0);
        this.counter = new AtomicLong(0);
    }
    
    public AbstractFactHandleFactory(long id, long counter) {
        this.idGen = new IdsGenerator( id );
        this.counter = new AtomicLong( counter );
    }

    public final InternalFactHandle newFactHandle(Object object,
                                                  ObjectTypeConf conf,
                                                  ReteEvaluator reteEvaluator,
                                                  WorkingMemoryEntryPoint wmEntryPoint) {
        return newFactHandle( getNextId(),
                              object,
                              conf,
                              reteEvaluator,
                              wmEntryPoint );
    }

    public final InternalFactHandle newFactHandle(long id,
                                                  Object object,
                                                  ObjectTypeConf conf,
                                                  ReteEvaluator reteEvaluator,
                                                  WorkingMemoryEntryPoint wmEntryPoint) {
        return newFactHandle( id,
                              object,
                              getNextRecency(),
                              conf,
                              reteEvaluator,
                              wmEntryPoint );
    }

    public final InternalFactHandle newFactHandle( final long id,
                                                   final Object object,
                                                   final long recency,
                                                   final ObjectTypeConf conf,
                                                   final ReteEvaluator reteEvaluator,
                                                   final WorkingMemoryEntryPoint wmEntryPoint ) {
        WorkingMemoryEntryPoint entryPoint = getWmEntryPoint(reteEvaluator, wmEntryPoint);
        return conf == null ?
            createDefaultFactHandle(id, object, recency, entryPoint) :
            conf.createFactHandle(this, id, object, recency, reteEvaluator, entryPoint);
    }

    @Override
    public DefaultFactHandle createDefaultFactHandle(long id, Object object, long recency, WorkingMemoryEntryPoint entryPoint) {
        return new DefaultFactHandle(id, object, recency, entryPoint);
    }

    @Override
    public DefaultEventHandle createEventFactHandle(long id, Object object, long recency, WorkingMemoryEntryPoint entryPoint, long timestamp, long duration) {
        return new DefaultEventHandle(id, object, recency, timestamp, duration, entryPoint);
    }

    protected WorkingMemoryEntryPoint getWmEntryPoint(ReteEvaluator reteEvaluator, WorkingMemoryEntryPoint wmEntryPoint) {
        if (wmEntryPoint != null) {
            return wmEntryPoint;
        }
        return reteEvaluator != null ? reteEvaluator.getDefaultEntryPoint() : null;
    }

    public final void increaseFactHandleRecency(final InternalFactHandle factHandle) {
        factHandle.setRecency( getNextRecency() );
    }

    public void destroyFactHandle(final InternalFactHandle factHandle) {
        factHandle.invalidate();
    }

    public abstract FactHandleFactory newInstance();

    public long getNextId() {
        return idGen.getNextId();
    }

    public long getNextRecency() {
        return this.counter.incrementAndGet();
    }

    public long getId() {
        return idGen.getId();
    }

    public long getRecency() {
        return this.counter.get();
    }

    public void clear(long id, long counter) {
        this.idGen = new IdsGenerator( id );
        this.counter = new AtomicLong( counter );
    }

    public void doRecycleIds(Collection<Long> usedIds) {
        idGen.doRecycle( usedIds );
    }

    public void stopRecycleIds() {
        idGen.stopRecycle();
    }

    private static class IdsGenerator {

        /** The fact id. */
        private AtomicLong id;

        private Queue<Long> usedIds;
        private long recycledId;

        private IdsGenerator( long startId ) {
            this.id = new AtomicLong( startId );
        }

        public long getNextId() {
            return hasRecycledId() ? recycledId++ : this.id.incrementAndGet();
        }

        private boolean hasRecycledId() {
            if (usedIds != null) {
                while ( !usedIds.isEmpty() ) {
                    long firstUsedId = usedIds.peek();
                    if ( recycledId < firstUsedId ) {
                        return true;
                    } else if ( recycledId == firstUsedId ) {
                        recycledId++;
                    }
                    usedIds.poll();
                }
                usedIds = null;
            }
            return false;
        }

        public long getId() {
            return this.id.get();
        }

        public void doRecycle(Collection<Long> usedIds) {
            this.usedIds = usedIds.stream().sorted().collect( toCollection( ArrayDeque::new ) );
            this.usedIds.add( id.get()+1 );
            this.recycledId = 1;
        }

        public void stopRecycle() {
            this.usedIds = null;
        }
    }
}
