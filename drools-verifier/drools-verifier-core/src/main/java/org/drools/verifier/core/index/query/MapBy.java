package org.drools.verifier.core.index.query;

import java.util.HashMap;
import java.util.List;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.maps.MultiMap;

public class MapBy<KeyType, ValueType> {

    private MultiMap<Value, ValueType, List<ValueType>> multiMap;

    private HashMap<KeyType, Value> valueMap = new HashMap<>();

    public MapBy(final MultiMap<Value, ValueType, List<ValueType>> multiMap) {
        this.multiMap = multiMap;
        for (final Value value : multiMap.keySet()) {
            valueMap.put((KeyType) value.getComparable(),
                         value);
        }
    }

    public boolean containsKey(final KeyType key) {
        return valueMap.containsKey(key);
    }

    public List<ValueType> get(final KeyType key) {
        return multiMap.get(valueMap.get(key));
    }
}
