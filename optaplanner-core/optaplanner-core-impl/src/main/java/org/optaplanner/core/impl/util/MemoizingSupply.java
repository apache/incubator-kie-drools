package org.optaplanner.core.impl.util;

import java.util.Objects;
import java.util.function.Supplier;

import org.optaplanner.core.impl.domain.variable.supply.Supply;

/**
 * Supply whose value is pre-computed and cached the first time {@link #read()} is called.
 *
 * @param <T>
 */
public final class MemoizingSupply<T> implements Supply {

    private final Supplier<T> valueSupplier;
    private boolean cached = false;
    private T memoizedValue;

    public MemoizingSupply(Supplier<T> supplier) {
        this.valueSupplier = Objects.requireNonNull(supplier);
    }

    public T read() {
        if (!cached) {
            cached = true; // Don't re-compute the supply even if the value was null.
            memoizedValue = valueSupplier.get();
        }
        return memoizedValue;
    }

}
