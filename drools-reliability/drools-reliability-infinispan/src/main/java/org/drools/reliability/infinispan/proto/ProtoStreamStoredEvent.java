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
package org.drools.reliability.infinispan.proto;

import org.drools.reliability.core.BaseStoredEvent;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.types.protobuf.AnySchema;

/**
 * This class is used to store objects in Infinispan using ProtoStream.
 * This class inherits Serializable from BaseStoredEvent, but it uses ProtoStream instead of Java serialization.
 */
public class ProtoStreamStoredEvent extends BaseStoredEvent {

    private final transient Object object;

    public ProtoStreamStoredEvent(Object object, boolean propagated, long timestamp, long duration) {
        super(propagated, timestamp, duration);
        this.object = object;
    }

    @ProtoFactory
    public ProtoStreamStoredEvent(AnySchema.Any protoObject, boolean propagated, long timestamp, long duration) {
        super(propagated, timestamp, duration);
        this.object = ProtoStreamUtils.fromAnySchema(protoObject);
    }

    @ProtoField(number = 1, required = true)
    public AnySchema.Any getProtoObject() {
        return ProtoStreamUtils.toAnySchema(object);
    }

    @Override
    @ProtoField(number = 2, required = true)
    public boolean isPropagated() {
        return propagated;
    }

    @Override
    @ProtoField(number = 3, required = true)
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    @ProtoField(number = 4, required = true)
    public long getDuration() {
        return duration;
    }

    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "ProtoStreamStoredEvent{" +
                "object=" + object +
                ", propagated=" + propagated +
                ", timestamp=" + timestamp +
                ", duration=" + duration +
                '}';
    }
}