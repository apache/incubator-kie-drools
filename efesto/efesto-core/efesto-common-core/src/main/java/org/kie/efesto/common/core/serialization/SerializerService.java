package org.kie.efesto.common.core.serialization;

import com.fasterxml.jackson.databind.JsonSerializer;

/**
 * This is used to dynamically discover all the implementation-specific serializers that are present in the classloader.
 * For each custom-made deserializer, an instance of this interface has to be provided.
 * <code>JSONUtils</code> will dynamically discover them to configure the ObjectMapper
 *
 * @param <T>
 */
public interface SerializerService<T> {

    Class<T> type();

    JsonSerializer<? extends T> ser();
}
