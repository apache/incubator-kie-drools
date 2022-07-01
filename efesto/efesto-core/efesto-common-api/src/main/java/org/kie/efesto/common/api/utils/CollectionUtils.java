package org.kie.efesto.common.api.utils;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CollectionUtils {

    private CollectionUtils() { }

    public static <T, X extends RuntimeException> Optional<T> findAtMostOne(Iterable<T> collection, Predicate<T> filter, BiFunction<T, T, X> multipleValuesExceptionSupplier) {
        T result = null;
        for (T t : collection) {
            if (filter.test(t)) {
                if (result == null) {
                    result = t;
                } else {
                    throw multipleValuesExceptionSupplier.apply(result, t);
                }
            }
        }
        return Optional.ofNullable(result);
    }

    public static <T, X extends RuntimeException> T findExactlyOne(Iterable<T> collection, Predicate<T> filter, BiFunction<T, T, X> multipleValuesExceptionSupplier, Supplier<X> missingValueExceptionSupplier) {
        return findAtMostOne(collection, filter, multipleValuesExceptionSupplier).orElseThrow(missingValueExceptionSupplier);
    }
}
