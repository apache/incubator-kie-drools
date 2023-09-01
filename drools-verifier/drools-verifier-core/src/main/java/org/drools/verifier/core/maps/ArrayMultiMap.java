package org.drools.verifier.core.maps;

import java.util.ArrayList;
import java.util.SortedMap;

//import org.jboss.errai.common.client.api.annotations.MapsTo;
//import org.jboss.errai.common.client.api.annotations.Portable;

//@Portable
public class ArrayMultiMap<Key extends Comparable, Value>
        extends RawMultiMap<Key, Value, ArrayList<Value>>
        implements MultiMap<Key, Value, ArrayList<Value>> {

    public ArrayMultiMap() {
        super(new ArrayListSubMapProvider<>());
    }

    protected ArrayMultiMap(
//            @MapsTo("map")
            final SortedMap<Key, ArrayList<Value>> map) {
        super(map,
              new ArrayListSubMapProvider<>());
    }

    @Override
    public void addChangeListener(final MultiMapChangeHandler<Key, Value> multiMapChangeHandler) {
        throw new UnsupportedOperationException("This map " + this.getClass().getName() + " can not have change handlers.");
    }

    @Override
    public ArrayMultiMap<Key, Value> subMap(final Key fromKey,
                                            final boolean fromInclusive,
                                            final Key toKey,
                                            final boolean toInclusive) {
        return new ArrayMultiMap<>(map.subMap(fromKey,
                                              fromInclusive,
                                              toKey,
                                              toInclusive));
    }
}
