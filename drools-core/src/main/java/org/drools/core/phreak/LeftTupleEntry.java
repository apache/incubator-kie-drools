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

package org.drools.core.phreak;

import org.drools.core.common.Memory;
import org.drools.core.common.PhreakPropagationContext;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.spi.PropagationContext;

public class LeftTupleEntry implements TupleEntry {
    private final LeftTuple          lt;
    private final PropagationContext pctx;
    private final Memory             nodeMemory;
    private final int                propagationType;

    private TupleEntry next;

    public LeftTupleEntry(LeftTuple lt, PropagationContext pctx, Memory nodeMemory, int propagationType) {
        this.lt = lt;
        this.pctx = pctx;
        this.nodeMemory = nodeMemory;
        this.propagationType = propagationType;
    }

    public LeftTuple getLeftTuple() {
        return lt;
    }

    public RightTuple getRightTuple() {
        return null;
    }

    public PropagationContext getPropagationContext() {
        return pctx;
    }

    public Memory getNodeMemory() {
        return nodeMemory;
    }

    public int getPropagationType() {
        return propagationType;
    }


    @Override
    public String toString() {
        return "LeftTupleEntry{" +
               "lt=" + lt +
               ", pctx=" + PhreakPropagationContext.intEnumToString(pctx) +
               ", nodeMemory=" + nodeMemory +
               '}';
    }

    public TupleEntry getNext() {
        return next;
    }

    public void setNext(TupleEntry next) {
        this.next = next;
    }
}
