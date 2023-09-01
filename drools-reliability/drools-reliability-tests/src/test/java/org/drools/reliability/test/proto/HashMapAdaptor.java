package org.drools.reliability.test.proto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

/**
 * This adaptor is specialized for HashMap<String, Object>. If you have a different type of map, you need to enhance this adaptor.
 */
@ProtoAdapter(HashMap.class)
public class HashMapAdaptor {

    @ProtoFactory
    HashMap<String, Object> create(List<EntryImpl> entryList) {
        HashMap<String, Object> map = new HashMap<>();
        for (EntryImpl entry : entryList) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    @ProtoField(number = 1, collectionImplementation = ArrayList.class)
    List<EntryImpl> getEntryList(HashMap<String, Object> map) {
        return map.entrySet()
                .stream()
                .map(e -> new EntryImpl(e.getKey(), e.getValue()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}

