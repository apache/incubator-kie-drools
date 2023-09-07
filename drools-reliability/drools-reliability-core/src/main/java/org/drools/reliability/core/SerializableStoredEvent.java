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
package org.drools.reliability.core;

import java.io.Serializable;

public class SerializableStoredEvent extends BaseStoredEvent {

    protected final Serializable object;

    public SerializableStoredEvent(Object object, boolean propagated, long timestamp, long duration) {
        super(propagated, timestamp, duration);
        if (!(object instanceof Serializable)) {
            throw new IllegalArgumentException("Object must be serializable : " + object.getClass().getCanonicalName());
        }
        this.object = (Serializable) object;
    }

    @Override
    public Serializable getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "SerializableStoredEvent{" +
                "object=" + object +
                ", propagated=" + propagated +
                ", timestamp=" + timestamp +
                ", duration=" + duration +
                '}';
    }
}