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