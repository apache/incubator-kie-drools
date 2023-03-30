/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.reliability;

import java.io.Serializable;

import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.rule.accessor.FactHandleFactory;

public class StoredObject implements Serializable {
    private final Object object;
    private final boolean propagated;
    private final long timestamp;
    private final long duration;

    public StoredObject(Object object, boolean propagated) {
        this(object, propagated, -1, -1);
    }

    public StoredObject(Object object, boolean propagated, long timestamp, long duration) {
        this.object = object;
        this.propagated = propagated;
        this.timestamp = timestamp;
        this.duration = duration;
    }

    boolean isEvent() {
        return timestamp >= 0;
    }

    public boolean isPropagated() {
        return propagated;
    }

    void repropagate(InternalWorkingMemoryEntryPoint ep) {
        if (isEvent()) {
            FactHandleFactory fhFactory = ep.getHandleFactory();
            EventFactHandle eFh = fhFactory.createEventFactHandle(fhFactory.getNextId(), object, fhFactory.getNextRecency(), ep, timestamp, duration);
            ep.insert(eFh);
        } else {
            ep.insert(object);
        }
    }
}