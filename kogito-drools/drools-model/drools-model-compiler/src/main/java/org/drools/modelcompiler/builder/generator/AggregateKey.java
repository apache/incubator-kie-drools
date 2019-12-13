package org.drools.modelcompiler.builder.generator;

import java.util.Objects;

public final class AggregateKey {
    String key;
    Class<?> clazz;

    public AggregateKey(String key, Class<?> clazz) {
        this.key = key;
        this.clazz = clazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AggregateKey that = (AggregateKey) o;
        return key.equals(that.key) &&
                clazz.equals(that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, clazz);
    }
}

