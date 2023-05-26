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

package org.drools.reliability.infinispan.proto;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.drools.core.common.DefaultEventHandle;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.rule.accessor.FactHandleFactory;
import org.drools.reliability.core.ReliabilityRuntimeException;
import org.drools.reliability.core.StorageManagerFactory;
import org.drools.reliability.core.StoredObject;
import org.drools.reliability.infinispan.InfinispanStorageManager;
import org.infinispan.protostream.ProtobufUtil;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.types.protobuf.AnySchema;

public class ProtoStreamStoredObject implements StoredObject {

    private final Object object;
    private final boolean propagated;
    private final long timestamp;
    private final long duration;

    private final String typeUrl;
    private final AnySchema.Any protoObject;

    public ProtoStreamStoredObject(Object object, boolean propagated) {
        this(object, propagated, -1, -1);
    }

    public ProtoStreamStoredObject(Object object, boolean propagated, long timestamp, long duration) {
        this.object = object;
        this.propagated = propagated;
        this.timestamp = timestamp;
        this.duration = duration;

        this.typeUrl = object.getClass().getCanonicalName();
        SerializationContext serializationContext = ((InfinispanStorageManager) StorageManagerFactory.get().getStorageManager()).getSerializationContext();
        byte[] objectBytes;
        try {
            objectBytes = ProtobufUtil.toByteArray(serializationContext, object);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        this.protoObject = new AnySchema.Any(typeUrl, objectBytes);
    }

    @ProtoFactory
    public ProtoStreamStoredObject(AnySchema.Any protoObject, boolean propagated, long timestamp, long duration) {
        this.propagated = propagated;
        this.timestamp = timestamp;
        this.duration = duration;

        this.protoObject = protoObject;
        this.typeUrl = protoObject.getTypeUrl();

        SerializationContext serializationContext = ((InfinispanStorageManager) StorageManagerFactory.get().getStorageManager()).getSerializationContext();
        try {
            Class<?> type = Class.forName(this.typeUrl);
            this.object = ProtobufUtil.fromByteArray(serializationContext, protoObject.getValue(), type);
        } catch (IOException | ClassNotFoundException e) {
            throw new ReliabilityRuntimeException(e);
        }
    }

    @ProtoField(value = 1, required = true)
    public AnySchema.Any getProtoObject() {
        return protoObject;
    }

    @ProtoField(value = 2, required = true)
    public boolean isPropagated() {
        return propagated;
    }

    @ProtoField(value = 3, required = true)
    public long getTimestamp() {
        return timestamp;
    }

    @ProtoField(value = 4, required = true)
    public long getDuration() {
        return duration;
    }

    public boolean isEvent() {
        return timestamp >= 0;
    }

    public Object getObject() {
        return object;
    }

    public void repropagate(InternalWorkingMemoryEntryPoint ep) {
        if (isEvent()) {
            FactHandleFactory fhFactory = ep.getHandleFactory();
            DefaultEventHandle eFh = fhFactory.createEventFactHandle(fhFactory.getNextId(), object, fhFactory.getNextRecency(), ep, timestamp, duration);
            ep.insert(eFh);
        } else {
            ep.insert(object);
        }
    }

    @Override
    public String toString() {
        return "ProtoStreamStoredObject{" +
                "object=" + object +
                ", propagated=" + propagated +
                ", timestamp=" + timestamp +
                ", duration=" + duration +
                ", typeUrl='" + typeUrl + '\'' +
                '}';
    }
}