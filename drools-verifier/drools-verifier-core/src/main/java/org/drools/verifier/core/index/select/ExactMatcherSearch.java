package org.drools.verifier.core.index.select;

import java.util.List;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.matchers.ExactMatcher;
import org.drools.verifier.core.maps.MultiMap;

public class ExactMatcherSearch<T> {

    private ExactMatcher matcher;
    private MultiMap<Value, T, List<T>> map;

    public ExactMatcherSearch(final ExactMatcher matcher,
                              final MultiMap<Value, T, List<T>> map) {
        this.matcher = matcher;
        this.map = map;
    }

    public MultiMap<Value, T, List<T>> search() {

        if (matcher.isNegate()) {

            if (map.containsKey(matcher.getValue())) {

                return MultiMap.merge(map.subMap(map.firstKey(), true,
                                                 matcher.getValue(), false),
                                      map.subMap(matcher.getValue(), false,
                                                 map.lastKey(), true));
            } else {
                return map;
            }
        } else {
            return map.subMap(matcher.getValue(), true,
                              matcher.getValue(), true);
        }
    }
}
