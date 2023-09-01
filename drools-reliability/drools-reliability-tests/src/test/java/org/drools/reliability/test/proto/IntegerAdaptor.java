package org.drools.reliability.test.proto;

import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@ProtoAdapter(Integer.class)
public class IntegerAdaptor {

    @ProtoFactory
    Integer create(Integer value) {
        return Integer.valueOf(value);
    }

    @ProtoField(1)
    Integer getValue(Integer value) {
        return value;
    }
}
