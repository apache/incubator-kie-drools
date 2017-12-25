/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import org.drools.core.phreak.SegmentUtilities;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.util.LinkedListNode;

/**
 * A super interface for node memories
 */
public interface Memory extends LinkedListNode<Memory> {
    
    short getNodeType();
    
    SegmentMemory getSegmentMemory();

    default SegmentMemory getOrCreateSegmentMemory( LeftTupleSource tupleSource, InternalWorkingMemory wm ) {
        SegmentMemory smem = getSegmentMemory();
        if (smem == null) {
            smem = SegmentUtilities.createSegmentMemory( tupleSource, this, wm );
        }
        return smem;
    }

    void setSegmentMemory(SegmentMemory segmentMemory);

    void reset();
}
