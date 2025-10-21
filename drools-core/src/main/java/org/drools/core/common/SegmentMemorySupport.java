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
package org.drools.core.common;

import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.LeftTupleSinkPropagator;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.PathEndNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.SegmentMemory;

public interface SegmentMemorySupport {
    
    public SegmentMemory getOrCreateSegmentMemory(LeftTupleNode node);
    
    public SegmentMemory getOrCreateSegmentMemory(LeftTupleNode node, Memory memory);
    
    public SegmentMemory createSegmentMemoryLazily(LeftTupleSource segmentRoot);
    
    public SegmentMemory getQuerySegmentMemory(QueryElementNode queryNode);
    
    public void createChildSegments(LeftTupleSinkPropagator sinkProp, SegmentMemory smem);
      
    public SegmentMemory createChildSegment(LeftTupleNode node);
   
    public SegmentMemory createChildSegmentLazily(LeftTupleNode node);
    
    public PathMemory initializePathMemory(PathEndNode pathEndNode);
    
    public void checkEagerSegmentCreation(LeftTupleSource lt, int nodeTypesInSegment);
}
