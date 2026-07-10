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

package org.optaplanner.constraint.streams.bavet.common;

public abstract class AbstractTuple implements Tuple {

    /*
     * We create a lot of tuples, many of them having store size of 1.
     * If an array of size 1 was created for each such tuple, memory would be wasted and indirection created.
     * This trade-off of increased memory efficiency for marginally slower access time is proven beneficial.
     */
    private final boolean storeIsArray;

    private Object store;
    public BavetTupleState state = BavetTupleState.CREATING;

    protected AbstractTuple(int storeSize) {
        this.store = (storeSize < 2) ? null : new Object[storeSize];
        this.storeIsArray = store != null;
    }

    @Override
    public final BavetTupleState getState() {
        return state;
    }

    @Override
    public final void setState(BavetTupleState state) {
        this.state = state;
    }

    @Override
    public final <Value_> Value_ getStore(int index) {
        if (storeIsArray) {
            return (Value_) ((Object[]) store)[index];
        }
        return (Value_) store;
    }

    @Override
    public final void setStore(int index, Object value) {
        if (storeIsArray) {
            ((Object[]) store)[index] = value;
            return;
        }
        store = value;
    }

    @Override
    public <Value_> Value_ removeStore(int index) {
        Value_ value;
        if (storeIsArray) {
            Object[] array = (Object[]) store;
            value = (Value_) array[index];
            array[index] = null;
        } else {
            value = (Value_) store;
            store = null;
        }
        return value;
    }

}
