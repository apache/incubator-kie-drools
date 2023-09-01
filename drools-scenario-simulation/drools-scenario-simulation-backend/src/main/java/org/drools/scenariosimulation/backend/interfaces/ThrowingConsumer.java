package org.drools.scenariosimulation.backend.interfaces;

import java.util.Objects;

@FunctionalInterface
public interface ThrowingConsumer<T> {

    static <T> ThrowingConsumer<T> identity() {
        return document -> {
        };
    }

    void accept(T var1) throws Exception;

    default ThrowingConsumer<T> andThen(ThrowingConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (t) -> {
            this.accept(t);
            after.accept(t);
        };
    }
}
