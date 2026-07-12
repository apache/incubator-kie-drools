/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.examples.common.persistence.jackson;

import java.util.Objects;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.AbstractJsonSolutionFileIO;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

/**
 * Deserializes map key defined by {@link JacksonUniqueIdGenerator} to a child of {@link AbstractPersistable}.
 * <p>
 * Deserialization will create new instances of the map key type.
 * Duplicate instances will be created if any other part of the JSON is also referencing the same type.
 * In that case, a custom implementation of {@link AbstractJsonSolutionFileIO} must be used later
 * to resolve the duplicates by comparing IDs of such objects and making sure only one instance exists with each ID.
 * <p>
 * Example: let us consider a "Location" object that has a field of type Map&lt;Location, Distance&gt;.
 * When the outer Location object gets deserialized from List&lt;Location&gt;,
 * the nested map needs to be deserialized as well.
 * However, at this point, the Location objects used as keys in the map do not yet exist.
 * (The rest of the list has not been read yet.)
 * Therefore the deserializer needs to create a temporary dummy Location object to use as the map key.
 * This object later needs to be replaced by the actual Location object by the {@link AbstractJsonSolutionFileIO}.
 *
 * @param <E> The type must have a {@link com.fasterxml.jackson.annotation.JsonIdentityInfo} annotation with
 *        {@link JacksonUniqueIdGenerator} as its generator.
 */
public abstract class AbstractKeyDeserializer<E extends AbstractPersistable> extends KeyDeserializer {

    private final Class<E> persistableClass;

    protected AbstractKeyDeserializer(Class<E> persistableClass) {
        this.persistableClass = Objects.requireNonNull(persistableClass);
    }

    @Override
    public final E deserializeKey(String value, DeserializationContext deserializationContext) {
        String[] parts = value.split("#");
        String className = parts[0];
        if (!Objects.equals(className, persistableClass.getSimpleName())) {
            throw new IllegalStateException("Impossible state: not the correct type (" + value + ").");
        }
        String idString = parts[1];
        try {
            long id = Long.parseLong(idString);
            return createInstance(id); // Need to be de-duplicated in solution IO.
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Impossible state: id is not a number (" + idString + ")");
        }
    }

    protected abstract E createInstance(long id);

}
