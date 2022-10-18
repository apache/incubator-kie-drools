package org.kie.efesto.common.api.utils;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

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

}
