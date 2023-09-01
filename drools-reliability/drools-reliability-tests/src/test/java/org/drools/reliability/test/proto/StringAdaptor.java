package org.drools.reliability.test.proto;

import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@ProtoAdapter(String.class)
public class StringAdaptor {

    @ProtoFactory
    String create(String value) {
        return new String(value);
    }

    @ProtoField(1)
    String getValue(String value) {
        return value;
    }
}
