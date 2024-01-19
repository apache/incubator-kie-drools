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
package org.drools.core.reteoo;

import org.drools.base.common.NetworkNode;
import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.SuperCacheFixer;

/**
 * A parent class for all specific LeftTuple specializations
 *
 */
public class LeftTuple
        extends TupleImpl {

    public LeftTuple() {
        // constructor needed for serialisation
        super();
    }

    public LeftTuple(InternalFactHandle factHandle,
                     Sink sink,
                     boolean leftTupleMemoryEnabled) {
        super(factHandle, sink, leftTupleMemoryEnabled);
    }

    public LeftTuple(InternalFactHandle factHandle,
                     TupleImpl leftTuple,
                     Sink sink) {
        super(factHandle, leftTuple, sink);
    }

    public LeftTuple(TupleImpl leftTuple,
                     Sink sink,
                     PropagationContext pctx,
                     boolean leftTupleMemoryEnabled) {
        super(leftTuple, sink, pctx, leftTupleMemoryEnabled);
    }

    public LeftTuple(TupleImpl leftTuple,
                     TupleImpl rightTuple,
                     Sink sink) {
        super(leftTuple, rightTuple, sink);
    }

    public LeftTuple(TupleImpl leftTuple,
                     TupleImpl rightTuple,
                     Sink sink,
                     boolean leftTupleMemoryEnabled) {
        super( leftTuple,
              rightTuple,
              null,
              null,
              sink,
              leftTupleMemoryEnabled );
    }

    public LeftTuple(TupleImpl leftTuple,
                     TupleImpl rightTuple,
                     TupleImpl currentLeftChild,
                     TupleImpl currentRightChild,
                     Sink sink,
                     boolean leftTupleMemoryEnabled) {
        super(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled);
    }

    @Override
    public ObjectTypeNodeId getInputOtnId() {
        return SuperCacheFixer.getLeftInputOtnId(this);
    }

    @Override
    public void reAdd() {
        getFactHandle().addLastLeftTuple( this );
    }

    public boolean isLeftTuple() {
        return true;
    }
}
