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

import org.drools.core.common.UpdateContext;
import org.drools.core.reteoo.builder.BuildContext;

public class MockTupleSource extends LeftTupleSource {

    private static final long serialVersionUID = 510l;

    private int               attached;

    private int               updated;

    public MockTupleSource(final int id, BuildContext context) {
        super( id, context );
    }

    public void attach() {
        this.attached++;
    }

    public int getAttached() {
        return this.sink.getSinks().length;
    }

    public int getUdated() {
        return this.updated;
    }

    protected boolean doRemove(final RuleRemovalContext context,
                               final ReteooBuilder builder) {
        return true;
    }

    public void doAttach( BuildContext context ) {
    }

    @Override
    public void networkUpdated(UpdateContext updateContext) {
    }

    public int getType() {
        return 0;
    }

    public ObjectTypeNode getObjectTypeNode() {
        return null;
    }

    @Override
    public boolean isLeftTupleMemoryEnabled() {
        return true;
    }
}
