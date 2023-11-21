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
package org.drools.core.phreak;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.drools.base.phreak.ReactiveObject;
import org.drools.base.reteoo.BaseTuple;

public class AbstractReactiveObject implements ReactiveObject {

    private Collection<BaseTuple> tuples;

    public void addTuple(BaseTuple tuple) {
        if (tuples == null) {
            tuples = new HashSet<>();
        }
        tuples.add(tuple);
    }

    public Collection<BaseTuple> getTuples() {
        return tuples != null ? tuples : Collections.emptyList();
    }

    protected void notifyModification() {
        ReactiveObjectUtil.notifyModification(this);
    }

    @Override
    public void removeTuple(BaseTuple tuple) {
        tuples.remove(tuple);
    }
}
