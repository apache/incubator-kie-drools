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

import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.DataStream;
import org.kie.kogito.rules.SingletonStore;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@SpringBootConfiguration
public class KogitoObjectMapper {

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
            DataStream stream = DataSource.createStream();
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