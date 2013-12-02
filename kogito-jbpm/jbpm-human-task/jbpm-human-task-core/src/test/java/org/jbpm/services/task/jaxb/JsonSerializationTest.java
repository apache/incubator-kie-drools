package org.jbpm.services.task.jaxb;

import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.jbpm.services.task.jaxb.AbstractSerializationTest.TestType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonSerializationTest extends AbstractSerializationTest {

    private static final Logger logger = LoggerFactory.getLogger(JsonSerializationTest.class);
    
    public final static int JMS_SERIALIZATION_TYPE = 1;

    private ObjectMapper mapper = new JaxbJacksonObjectMapper();

    public JsonSerializationTest() {
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    }

    public TestType getType() {
        return TestType.JSON;
    }
    
    public Object testRoundTrip(Object object) throws Exception {
        String jsonStr =  mapper.writeValueAsString(object);
        logger.debug(jsonStr);
        return mapper.readValue(jsonStr, object.getClass());
    }

    private static class JaxbJacksonObjectMapper extends ObjectMapper {

        public JaxbJacksonObjectMapper() {
            super();

            final AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();

            this.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
            this.configure(org.codehaus.jackson.map.SerializationConfig.Feature.WRAP_ROOT_VALUE, true);

            this.setDeserializationConfig(this.getDeserializationConfig().withAnnotationIntrospector(introspector));
            this.setSerializationConfig(this.getSerializationConfig().withAnnotationIntrospector(introspector));

            this.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        }
    }

    @Override
    public void addClassesToSerializationContext(Class<?>... extraClass) {
        // no-op
    }

    // Specific JSON tests --------------------------------------------------------------------------------------------------------
    
    /**
     * None at the moment
     */
}
