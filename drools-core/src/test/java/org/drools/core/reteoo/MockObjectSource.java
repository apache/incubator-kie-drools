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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.base.base.ObjectType;
import org.drools.base.common.RuleBasePartitionId;
import org.drools.base.rule.Pattern;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContext;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.util.bitmask.BitMask;

public class MockObjectSource extends ObjectSource {
    private static final long serialVersionUID = 510l;

    private int               attached;

    private int               updated;

    private List              facts;

    public MockObjectSource(int i, BuildContext context) {
    }

    public MockObjectSource(final int id) {
        super( id, RuleBasePartitionId.MAIN_PARTITION );
        this.facts = new ArrayList();
    }

    public void attach() {
        this.attached++;

    }

    public int getAttached() {
        return this.attached;
    }

    public int getUdated() {
        return this.updated;
    }

    public void addFact(final InternalFactHandle handle) {
        this.facts.add( handle );
    }
    
    public void removeFact(final InternalFactHandle handle) {
        this.facts.remove( handle );
    }

    public void updateSink(final ObjectSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        this.updated++;
        for ( final Iterator it = this.facts.iterator(); it.hasNext(); ) {
            final InternalFactHandle handle = (InternalFactHandle) it.next();
            sink.assertObject( handle,
                               context,
                               workingMemory );
        }
    }

    public void doAttach(BuildContext context) {
    }

   
    public int getType() {
        return 0;
    }
    
    @Override
    public BitMask calculateDeclaredMask(Pattern pattern, ObjectType modifiedType, List<String> settableProperties) {
        throw new UnsupportedOperationException();
    }
}
