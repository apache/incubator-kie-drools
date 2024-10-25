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
package org.kie.kogito.index.event;

import java.io.IOException;
import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceStateDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceStateEventBody;
import org.kie.kogito.index.json.JsonUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@TestInstance(Lifecycle.PER_CLASS)
public class DataEventDeserializerTest {

    private ObjectMapper mapper;

    @BeforeAll

    public void beforeAll() {
        mapper = JsonUtils.configure(new ObjectMapper());
    }

    @Test
    public void testProcessInstanceDataEvent() throws IOException {
        ProcessInstanceStateDataEvent dataEvent = new ProcessInstanceStateDataEvent("source", "addons", "identity", new HashMap<>(), new ProcessInstanceStateEventBody());

        String jsonValue = mapper.writeValueAsString(dataEvent);
        ProcessInstanceDataEvent<?> readDataEvent = mapper.readValue(jsonValue.getBytes(), ProcessInstanceDataEvent.class);
        Assertions.assertEquals(readDataEvent, dataEvent);

    }

    @Test
    public void testUserTaskInstanceDataEvent() throws IOException {
        UserTaskInstanceStateDataEvent dataEvent = new UserTaskInstanceStateDataEvent("source", "addons", "identity", new HashMap<>(), new UserTaskInstanceStateEventBody());

        String jsonValue = mapper.writeValueAsString(dataEvent);
        UserTaskInstanceDataEvent<?> readDataEvent = mapper.readValue(jsonValue.getBytes(), UserTaskInstanceDataEvent.class);
        Assertions.assertEquals(readDataEvent, dataEvent);

    }

}
