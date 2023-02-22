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

import org.drools.core.common.InternalFactHandle;

public class ObjectStoreEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    enum EventType {
        INSERT,
        UPDATE,
        DELETE
    }

    private EventType eventType;

    private InternalFactHandle factHandle;

    public ObjectStoreEvent(EventType eventType, InternalFactHandle factHandle) {
        this.eventType = eventType;
        this.factHandle = factHandle;
    }

    public EventType getEventType() {
        return eventType;
    }

    public InternalFactHandle getFactHandle() {
        return factHandle;
    }

    @Override
    public String toString() {
        return "ObjectStoreEvent [eventType=" + eventType + ", factHandle=" + factHandle + "]";
    }
}
