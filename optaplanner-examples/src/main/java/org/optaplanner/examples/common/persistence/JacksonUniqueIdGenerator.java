package org.optaplanner.examples.common.persistence;

import org.optaplanner.examples.common.domain.AbstractPersistableJackson;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;

/**
 * Exists so that we can serialize/deserialize recursive data models (such as TSP chaining) using object references,
 * while at the same time being able to serialize/deserialize map keys using those same references.
 * <p>
 * Similar in principle to {@link UUIDGenerator}, but without the overly long and undescriptive UUIDs.
 * Works only for children of {@link AbstractPersistableJackson}.
 * No two such classes must have the same {@link Class#getSimpleName()}.
 */
public final class JacksonUniqueIdGenerator extends ObjectIdGenerator<String> {

    private final Class<?> scope;

    public JacksonUniqueIdGenerator() {
        this.scope = Object.class;
    }

    @Override
    public Class<?> getScope() {
        return scope;
    }

    @Override
    public boolean canUseFor(ObjectIdGenerator<?> gen) {
        return (gen.getClass() == getClass());
    }

    @Override
    public ObjectIdGenerator<String> forScope(Class<?> scope) {
        return this;
    }

    @Override
    public ObjectIdGenerator<String> newForSerialization(Object context) {
        return this;
    }

    @Override
    public IdKey key(Object key) {
        if (key == null) {
            return null;
        }
        return new IdKey(getClass(), null, key);
    }

    @Override
    public String generateId(Object forPojo) {
        return forPojo.getClass().getSimpleName() + "#" + ((AbstractPersistableJackson) forPojo).getId();
    }
}
