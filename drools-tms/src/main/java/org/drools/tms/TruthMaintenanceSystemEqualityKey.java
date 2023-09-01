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
package org.drools.tms;

import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.tms.beliefsystem.BeliefSet;

/**
 * Upon instantiation the EqualityKey caches the first Object's hashCode
 * this can never change. The EqualityKey has an internal datastructure
 * which references all the handles which are equal. It also records
 * Whether the referenced facts are JUSTIFIED or STATED
 */
public class TruthMaintenanceSystemEqualityKey extends EqualityKey {

    private BeliefSet beliefSet;

    public TruthMaintenanceSystemEqualityKey() {
    }

    public TruthMaintenanceSystemEqualityKey(InternalFactHandle handle) {
        super(handle);
    }

    public TruthMaintenanceSystemEqualityKey(InternalFactHandle handle, int status) {
        super(handle, status);
    }

    @Override
    public InternalFactHandle getLogicalFactHandle() {
        if ( beliefSet == null ) {
            return null;
        }

        return getFirst();
    }

    @Override
    public void setLogicalFactHandle(InternalFactHandle logicalFactHandle) {
        if ( logicalFactHandle == null && beliefSet != null ) {
            // beliefSet needs to not be null, otherwise someone else has already set the LFH to null
            removeFirst();
        } else {
            addFirst((DefaultFactHandle) logicalFactHandle);
        }
    }

    public BeliefSet getBeliefSet() {
        return beliefSet;
    }

    public void setBeliefSet(BeliefSet beliefSet) {
        this.beliefSet = beliefSet;
    }
}
