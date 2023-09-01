package org.drools.reliability.infinispan.proto;

import java.io.IOException;

import org.drools.reliability.core.ReliabilityRuntimeException;
import org.drools.reliability.core.StorageManagerFactory;
import org.drools.reliability.infinispan.InfinispanStorageManager;
import org.infinispan.protostream.ProtobufUtil;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.types.protobuf.AnySchema;

public class ProtoStreamUtils {

    private ProtoStreamUtils() {
        // util class
    }

    public static AnySchema.Any toAnySchema(Object object) {
        SerializationContext serializationContext = ((InfinispanStorageManager) StorageManagerFactory.get().getStorageManager()).getSerializationContext();
        byte[] objectBytes;
        try {
            objectBytes = ProtobufUtil.toByteArray(serializationContext, object);
        } catch (IOException e) {
            throw new ReliabilityRuntimeException(e);
        }
        return new AnySchema.Any(object.getClass().getCanonicalName(), objectBytes);
    }

    public static Object fromAnySchema(AnySchema.Any protoObject) {
        final Object object;
        SerializationContext serializationContext = ((InfinispanStorageManager) StorageManagerFactory.get().getStorageManager()).getSerializationContext();
        try {
            Class<?> type = Class.forName(protoObject.getTypeUrl());
            object = ProtobufUtil.fromByteArray(serializationContext, protoObject.getValue(), type);
        } catch (IOException | ClassNotFoundException e) {
            throw new ReliabilityRuntimeException(e);
        }
        return object;
    }
}
