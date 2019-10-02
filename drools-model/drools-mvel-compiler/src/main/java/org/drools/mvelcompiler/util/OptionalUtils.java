package org.drools.mvelcompiler.util;

import java.util.Optional;
import java.util.function.BiFunction;

public class OptionalUtils {

    private OptionalUtils() {

    }

    public static <T, K, V> Optional<V> map2(Optional<T> opt1, Optional<K> opt2, BiFunction<T, K, V> f) {
        return opt1.flatMap(t1 -> opt2.map(t2 -> f.apply(t1, t2)));
    }
}
