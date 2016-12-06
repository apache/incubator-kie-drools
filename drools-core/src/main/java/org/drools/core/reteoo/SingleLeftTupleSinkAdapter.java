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
import org.drools.core.common.RuleBasePartitionId;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class SingleLeftTupleSinkAdapter extends AbstractLeftTupleSinkAdapter {
    protected LeftTupleSink sink;
    
    private LeftTupleSink[] sinkArray;

    public SingleLeftTupleSinkAdapter() {
        this( RuleBasePartitionId.MAIN_PARTITION,
              null );
    }

    public SingleLeftTupleSinkAdapter(final RuleBasePartitionId partitionId,
                                      final LeftTupleSink sink) {
        super( partitionId );
        this.sink = sink;
        this.sinkArray = new LeftTupleSink[]{this.sink};
    }
    
    public BaseNode getMatchingNode(BaseNode candidate) {
        if ( sink.thisNodeEquals( candidate ) ) {
            return (BaseNode) sink;
        }
        return null;
    }

    public LeftTupleSink[] getSinks() {
        return sinkArray;
    }
    
    public LeftTupleSinkNode getFirstLeftTupleSink() {
        return ( LeftTupleSinkNode ) sink;
    }

    public LeftTupleSinkNode getLastLeftTupleSink() {
        return ( LeftTupleSinkNode ) sink;
    }

    public int size() {
        return (this.sink != null) ? 1 : 0;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        this.sink = (LeftTupleSink) in.readObject();
        this.sinkArray = new LeftTupleSink[]{this.sink};
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( this.sink );
    }
}
