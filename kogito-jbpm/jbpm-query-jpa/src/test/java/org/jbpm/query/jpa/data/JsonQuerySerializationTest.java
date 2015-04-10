package org.jbpm.query.jpa.data;

import org.codehaus.jackson.map.ObjectMapper;

public class JsonQuerySerializationTest extends AbstractQuerySerializationTest {

    private ObjectMapper mapper = new ObjectMapper();
   
    @Override
    public <T> T testRoundTrip(T object) throws Exception {
        String jsonStr =  mapper.writeValueAsString(object);
        logger.debug(jsonStr);
        return (T) mapper.readValue(jsonStr, object.getClass());
    }

    @Override
    void addSerializableClass( Class objClass ) {
       // no-op
    }
 
}
