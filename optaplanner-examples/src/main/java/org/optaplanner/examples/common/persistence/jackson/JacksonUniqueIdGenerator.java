package org.optaplanner.examples.common.persistence.jackson;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.AbstractJsonSolutionFileIO;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;

/**
 * Exists so that complex data models (such as TSP chaining) can be serialized/deserialized.
 * These complexities include:
 *
 * <ul>
 * <li>Serializing maps where keys are themselves serialized objects that need to be referenced later.</li>
 * <li>Serializing polymorphic types.</li>
 * <li>Serializing self-referential and/or recursive types.</li>
 * </ul>
 *
 * Jackson can easily handle any of these problems individually,
 * but struggles when they are all combined.
 * <p>
 * This class and other classes in this package aim to solve those issues
 * by introducing a new ID field on all serialized objects,
 * typically called "@id".
 * This field is used exclusively for referencing objects in the serialized JSON,
 * it never enters the Java data model.
 * Therefore it is not related to {@link AbstractPersistable#getId()},
 * which is the actual object ID used in the Java examples.
 * See Vehicle Routing example to learn how to use this pattern.
 * <p>
 * For use cases without these advanced needs,
 * the less complex way of using {@link JsonIdentityInfo} with {@link PropertyGenerator} is preferred.
 * See Cloud Balancing example to learn how to use this pattern.
 * <p>
 * The implementation is similar in principle to {@link UUIDGenerator}, but without the long and undescriptive UUIDs.
 * Works only for children of {@link AbstractPersistable}.
 * No two such classes must have the same {@link Class#getSimpleName()}.
 *
 * @see KeySerializer
 * @see AbstractKeyDeserializer
 * @see AbstractJsonSolutionFileIO
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
        return forPojo.getClass().getSimpleName() + "#" + ((AbstractPersistable) forPojo).getId();
    }
}
