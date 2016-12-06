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

package org.drools.core.reteoo;

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.spi.PropagationContext;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class EmptyLeftTupleSinkAdapter extends AbstractLeftTupleSinkAdapter {

    private static final EmptyLeftTupleSinkAdapter instance = new EmptyLeftTupleSinkAdapter();

    private static final LeftTupleSink[] sinks = new LeftTupleSink[]{};

    public static final EmptyLeftTupleSinkAdapter getInstance() {
        return instance;
    }

    public EmptyLeftTupleSinkAdapter() {
        super( RuleBasePartitionId.MAIN_PARTITION );
        // constructor needed for serialisation
    }

    public BaseNode getMatchingNode(BaseNode candidate) {
        return null;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public LeftTupleSink[] getSinks() {
        return sinks;
    }
    
    public LeftTupleSinkNode getFirstLeftTupleSink() {
        return null;
    }

    public LeftTupleSinkNode getLastLeftTupleSink() {
        return null;
    }

    public int size() {
        return 0;
    }
}
