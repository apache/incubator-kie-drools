/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.bavet.common;

public enum BavetTupleState {
    CREATING(true, true),
    UPDATING(true, true),
    /**
     * Freshly refreshed tuple.
     */
    OK(false, true),
    /**
     * Tuple which was {@link #UPDATING} and then invalidated by subsequent tuple.
     */
    DYING(true, false),
    DEAD(false, false),
    /**
     * Tuple which was {@link #CREATING} and then invalidated by subsequent tuple.
     */
    ABORTING(true, false);

    private final boolean dirty;
    private final boolean active;

    BavetTupleState(boolean dirty, boolean active) {
        this.dirty = dirty;
        this.active = active;
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean isActive() {
        return active;
    }

}
