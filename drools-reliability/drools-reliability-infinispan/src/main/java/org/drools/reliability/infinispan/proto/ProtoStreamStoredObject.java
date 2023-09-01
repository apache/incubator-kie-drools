package org.drools.reliability.infinispan.proto;

import org.drools.reliability.core.BaseStoredObject;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.types.protobuf.AnySchema;

/**
 * This class is used to store objects in Infinispan using ProtoStream.
 * This class inherits Serializable from BaseStoredObject, but it uses ProtoStream instead of Java serialization.
 */
public class ProtoStreamStoredObject extends BaseStoredObject {

    private final transient Object object;

    public ProtoStreamStoredObject(Object object, boolean propagated) {
        super(propagated);
        this.object = object;
    }

    @ProtoFactory
    public ProtoStreamStoredObject(AnySchema.Any protoObject, boolean propagated) {
        super(propagated);
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
    public Object getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "ProtoStreamStoredObject{" +
                "object=" + object +
                ", propagated=" + propagated +
                '}';
    }
}