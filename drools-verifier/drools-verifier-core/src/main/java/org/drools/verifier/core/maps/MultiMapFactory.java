package org.drools.verifier.core.maps;

import java.util.ArrayList;
import java.util.List;

public class MultiMapFactory {

    public static <Key extends Comparable, Value, ListType extends List<Value>> MultiMap<Key, Value, ListType> make(final boolean updatable,
                                                                                                                    final NewSubMapProvider<Value, ListType> newSubMapProvider) {
        if (updatable) {
            return new ChangeHandledMultiMap<>(new RawMultiMap<>(newSubMapProvider));
        } else {
            return new RawMultiMap<>(newSubMapProvider);
        }
    }

    public static <Key extends Comparable, Value> MultiMap<Key, Value, List<Value>> make(final boolean updatable) {
        return make(updatable,
                    new NewSubMapProvider<Value, List<Value>>() {
                        @Override
                        public List<Value> getNewSubMap() {
                            return new ArrayList<>();
                        }
                    });
    }

    public static MultiMap make() {
        return make(false);
    }
}
