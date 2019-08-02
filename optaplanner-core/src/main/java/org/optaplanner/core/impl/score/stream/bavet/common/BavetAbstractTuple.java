/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.bavet.common;

public abstract class BavetAbstractTuple implements BavetTuple {

    protected BavetTupleState state = BavetTupleState.NEW;

    public abstract void refresh();

    public boolean isDirty() {
        return state.isDirty();
    }

    public boolean isActive() {
        return state.isActive();
    }

    public void refreshed() {
        switch (state) {
            case CREATING:
                state = BavetTupleState.OK;
                break;
            case UPDATING:
                state = BavetTupleState.OK;
                break;
            case DYING:
            case ABORTING:
                state = BavetTupleState.DEAD;
                break;
            case DEAD:
                throw new IllegalStateException("The tuple (" + this
                        + ") is already in the dead state (" + state + ").");
        }
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public int getNodeOrder() {
        return getNode().getNodeOrder();
    }

    public BavetTupleState getState() {
        return state;
    }

    public void setState(BavetTupleState state) {
        this.state = state;
    }

}
