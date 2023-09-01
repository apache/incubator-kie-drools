package org.drools.ancompiler;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapUtils {

    private MapUtils() {

    }

    public static <K, V1, V2> Map<K, V2> mapValues(Map<K, V1> map, Function<V1, V2> mapper) {
        return map
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, v -> mapper.apply(v.getValue())));
    }
}
