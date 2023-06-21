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

package org.drools.reliability.core;

import java.io.Serializable;

import org.drools.core.common.DefaultEventHandle;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.rule.accessor.FactHandleFactory;

public abstract class BaseStoredObject implements StoredObject,
                                                  Serializable {

    protected final boolean propagated;
    protected final long timestamp;
    protected final long duration;
    protected final long handleId;

    protected BaseStoredObject(boolean propagated, long timestamp, long duration, long handleId) {
        this.propagated = propagated;
        this.timestamp = timestamp;
        this.duration = duration;
        this.handleId = handleId;
    }

    @Override
    public boolean isEvent() {
        return timestamp >= 0;
    }

    @Override
    public boolean isPropagated() {
        return propagated;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public void repropagate(InternalWorkingMemoryEntryPoint ep) {
        if (isEvent()) {
            FactHandleFactory fhFactory = ep.getHandleFactory();
            DefaultEventHandle eFh = fhFactory.createEventFactHandle(fhFactory.getNextId(), getObject(), fhFactory.getNextRecency(), ep, timestamp, duration);
            ep.insert(eFh);
        } else {
            ep.insert(getObject());
        }
    }
}