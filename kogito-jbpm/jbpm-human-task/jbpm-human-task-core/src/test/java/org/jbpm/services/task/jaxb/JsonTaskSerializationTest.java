package org.jbpm.services.task.jaxb;

import org.codehaus.jackson.map.ObjectMapper;
import org.jbpm.services.task.impl.model.ContentImpl;
import org.jbpm.services.task.impl.model.xml.JaxbContent;
import org.junit.Test;
import org.kie.api.task.model.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonTaskSerializationTest extends AbstractTaskSerializationTest {

    private static final Logger logger = LoggerFactory.getLogger(JsonTaskSerializationTest.class);
    
    public final static int JMS_SERIALIZATION_TYPE = 1;

    private ObjectMapper mapper = new ObjectMapper();

    public TestType getType() {
        return TestType.JSON;
    }
    
    public <T> T testRoundTrip(T object) throws Exception {
        String jsonStr =  mapper.writeValueAsString(object);
        logger.debug(jsonStr);
        return (T) mapper.readValue(jsonStr, object.getClass());
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
