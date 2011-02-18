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

package org.drools.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

public class LIANodePropagation
    implements
    Externalizable {
    private LeftInputAdapterNode node;
    private InternalFactHandle   handle;
    private PropagationContext   context;
    private boolean leftTupleMemoryEnabled;
    
    public LIANodePropagation() {
        // constructor needed for serialisation
    }

    public LIANodePropagation(final LeftInputAdapterNode node,
                              final InternalFactHandle handle,
                              final PropagationContext context) {
        super();
        this.node = node;
        this.handle = handle;
        this.context = context;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        node = (LeftInputAdapterNode) in.readObject();
        handle = (InternalFactHandle) in.readObject();
        context = (PropagationContext) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( node );
        out.writeObject( handle );
        out.writeObject( context );
    }

    public void doPropagation(InternalWorkingMemory workingMemory) {
        node.getSinkPropagator().createAndPropagateAssertLeftTuple( handle,
                                                                    context,
                                                                    workingMemory,
                                                                    false  );
    }

}
