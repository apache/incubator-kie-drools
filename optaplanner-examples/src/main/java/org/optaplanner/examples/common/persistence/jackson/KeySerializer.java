package org.optaplanner.examples.common.persistence.jackson;

import java.io.IOException;

import org.optaplanner.examples.common.domain.AbstractPersistableJackson;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializes a child of {@link AbstractPersistableJackson} to a JSON map key using {@link JacksonUniqueIdGenerator}.
 *
 * @param <E> The type must have a {@link com.fasterxml.jackson.annotation.JsonIdentityInfo} annotation with
 *        {@link JacksonUniqueIdGenerator} as its generator.
 */
public final class KeySerializer<E extends AbstractPersistableJackson> extends JsonSerializer<E> {

    private final ObjectIdGenerator<String> idGenerator = new JacksonUniqueIdGenerator();

    @Override
    public void serialize(E persistable, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        Object jsonId = serializerProvider.findObjectId(persistable, idGenerator)
                .generateId(persistable);
        jsonGenerator.writeFieldName(jsonId.toString());
    }
}
