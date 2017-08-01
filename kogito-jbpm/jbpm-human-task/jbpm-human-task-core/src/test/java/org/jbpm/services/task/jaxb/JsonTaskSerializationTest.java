/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.jaxb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonTaskSerializationTest extends AbstractTaskSerializationTest {

    private static final Logger logger = LoggerFactory.getLogger(JsonTaskSerializationTest.class);
    
    public final static int JMS_SERIALIZATION_TYPE = 1;

    private ObjectMapper mapper = new ObjectMapper();

    public JsonTaskSerializationTest() {
    }

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
