/*
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

package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class MrResource extends AbstractPersistable {

    private int index;
    private boolean transientlyConsumed;
    private int loadCostWeight;

    @SuppressWarnings("unused")
    MrResource() {
    }

    public MrResource(long id) {
        super(id);
    }

    public MrResource(int index, boolean transientlyConsumed, int loadCostWeight) {
        this.index = index;
        this.transientlyConsumed = transientlyConsumed;
        this.loadCostWeight = loadCostWeight;
    }

    public MrResource(long id, int index, boolean transientlyConsumed, int loadCostWeight) {
        super(id);
        this.index = index;
        this.transientlyConsumed = transientlyConsumed;
        this.loadCostWeight = loadCostWeight;
    }

    public int getIndex() {
        return index;
    }

    public boolean isTransientlyConsumed() {
        return transientlyConsumed;
    }

    public int getLoadCostWeight() {
        return loadCostWeight;
    }

}
