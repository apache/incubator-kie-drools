package org.drools.reliability.test.proto;

import org.drools.reliability.infinispan.proto.ProtoStreamGlobal;
import org.drools.reliability.infinispan.proto.ProtoStreamStoredEvent;
import org.drools.reliability.infinispan.proto.ProtoStreamStoredObject;
import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import org.infinispan.protostream.types.java.collections.ArrayListAdapter;
import org.infinispan.protostream.types.protobuf.AnySchema;

@AutoProtoSchemaBuilder(includeClasses = {ProtoStreamStoredObject.class, ProtoStreamStoredEvent.class, ProtoStreamGlobal.class, // infrastructure classes
                                          StringAdaptor.class,  IntegerAdaptor.class, BooleanAdaptor.class, ArrayListAdapter.class, // basic types
                                          PersonAdaptor.class, StockTickAdaptor.class, // domain classes
                                          HashMapEventImplAdaptor.class, HashMapAdaptor.class, EntryImpl.class}, // CEP event classes
        dependsOn = AnySchema.class,
        schemaPackageName = "org.drools.reliability.test",
        schemaFileName = "test-store-object.proto", schemaFilePath = "proto")
public interface TestProtoStreamSchema extends GeneratedSchema {
}
