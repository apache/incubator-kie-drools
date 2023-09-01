package org.drools.reliability.test.proto;

import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@ProtoAdapter(Boolean.class)
public class BooleanAdaptor {

    @ProtoFactory
    Boolean create(Boolean value) {
        return Boolean.valueOf(value);
    }

    @ProtoField(1)
    Boolean getValue(Boolean value) {
        return value;
    }
}
