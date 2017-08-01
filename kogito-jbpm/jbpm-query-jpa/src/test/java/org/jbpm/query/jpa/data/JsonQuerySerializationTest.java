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

package org.jbpm.query.jpa.data;

import com.fasterxml.jackson.databind.ObjectMapper;

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
