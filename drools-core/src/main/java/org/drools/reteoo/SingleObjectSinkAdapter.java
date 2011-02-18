/**
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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.RuleBasePartitionId;
import org.drools.spi.PropagationContext;

public class SingleObjectSinkAdapter extends AbstractObjectSinkAdapter {

    private static final long serialVersionUID = 510l;

    protected ObjectSink      sink;

    public SingleObjectSinkAdapter() {
        super( null );
    }

    public SingleObjectSinkAdapter(final RuleBasePartitionId partitionId,
                                   final ObjectSink sink) {
        super( partitionId );
        this.sink = sink;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        sink = (ObjectSink) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( sink );
    }

    public void propagateAssertObject(final InternalFactHandle factHandle,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
        this.sink.assertObject( factHandle,
                                context,
                                workingMemory );
    }

    public void propagateModifyObject(InternalFactHandle factHandle,
                                            ModifyPreviousTuples modifyPreviousTuples,
                                            PropagationContext context,
                                            InternalWorkingMemory workingMemory) {
        this.sink.modifyObject( factHandle,
                                       modifyPreviousTuples,
                                       context,
                                       workingMemory );
    }

    public BaseNode getMatchingNode(BaseNode candidate) {
        if ( candidate.equals( sink ) ) {
            return (BaseNode) sink;
        }
        return null;
    }

    public ObjectSink[] getSinks() {
        return new ObjectSink[]{this.sink};
    }

    public int size() {
        return 1;
    }

}
