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
package org.drools.core.phreak;

import java.util.Collection;
import java.util.List;

import org.drools.base.common.NetworkNode;
import org.drools.core.common.ActivationsManager;
import org.drools.core.common.Memory;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.TupleImpl;

public interface RuleNetworkEvaluator {

    void evaluateNetwork(RuleExecutor executor,
                         PathMemory pmem);

    void evaluateNetwork(ActivationsManager activationsManager,
                         RuleExecutor executor,
                         PathMemory pmem);

    void evaluate(PathMemory pmem,
                  ActivationsManager activationsManager,
                  NetworkNode sink,
                  Memory tm,
                  TupleSets trgLeftTuples);
    
    void forceFlushPath(PathMemory outPmem); 

    void forceFlushLeftTuple(PathMemory pmem,
                             SegmentMemory sm,
                             TupleSets leftTupleSets);
    
    void forceFlushWhenSubnetwork(PathMemory pmem);
    
    boolean flushLeftTupleIfNecessary(SegmentMemory sm, boolean streamMode);
    
    boolean flushLeftTupleIfNecessary(SegmentMemory sm,
                                             TupleImpl leftTuple,
                                             boolean streamMode,
                                             short stagedType);
    
    List<PathMemory> findPathsToFlushFromSubnetwork(PathMemory pmem);
    

    void forceFlushPaths(Collection<PathMemory> pathsToFlush);

    void propagate(SegmentMemory smem, TupleSets actualResultLeftTuples);

}
