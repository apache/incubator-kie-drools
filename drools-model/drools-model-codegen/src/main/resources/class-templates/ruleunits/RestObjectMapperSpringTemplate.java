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
package $Package$;

import java.util.List;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.DataStream;
import org.drools.ruleunits.api.SingletonStore;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@SpringBootConfiguration
public class RestObjectMapper {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizeObjectMapper() {
        return new Jackson2ObjectMapperBuilderCustomizer() {
            @Override
            public void customize(Jackson2ObjectMapperBuilder builder) {
                //addDefaultDeserializers
                builder.deserializerByType(DataStream.class, new DataStreamDeserializer());
                builder.deserializerByType(DataStore.class, new DataStoreDeserializer());
                builder.deserializerByType(SingletonStore.class, new SingletonStoreDeserializer());
            }
        };
    }

    public static class DataStreamDeserializer extends JsonDeserializer<DataStream<?>> implements ContextualDeserializer {

        private CollectionType collectionType;

        @Override
        public DataStream deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            DataStream stream = DataSource.createBufferedStream(16);
            List list = ctxt.readValue(jp, collectionType);
            list.forEach(stream::append);
            return stream;
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
            CollectionType collectionType = ctxt.getTypeFactory().constructCollectionType(List.class, property.getType().containedType(0));
            DataStreamDeserializer deserializer = new DataStreamDeserializer();
            deserializer.collectionType = collectionType;
            return deserializer;
        }
    }

    public static class DataStoreDeserializer extends JsonDeserializer<DataStore<?>> implements ContextualDeserializer {

        private CollectionType collectionType;

        @Override
        public DataStore deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            DataStore store = DataSource.createStore();
            List list = ctxt.readValue(jp, collectionType);
            list.forEach(store::add);
            return store;
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
            CollectionType collectionType = ctxt.getTypeFactory().constructCollectionType(List.class, property.getType().containedType(0));
            DataStoreDeserializer deserializer = new DataStoreDeserializer();
            deserializer.collectionType = collectionType;
            return deserializer;
        }
    }

    public static class SingletonStoreDeserializer extends JsonDeserializer<SingletonStore<?>> implements ContextualDeserializer {

        private JavaType javaType;

        @Override
        public SingletonStore deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            SingletonStore store = DataSource.createSingleton();
            store.set(ctxt.readValue(jp, javaType));
            return store;
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
            JavaType javaType = property.getType().containedType(0);
            SingletonStoreDeserializer deserializer = new SingletonStoreDeserializer();
            deserializer.javaType = javaType;
            return deserializer;
        }
    }
}