/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.jbpm.process.instance.impl.humantask.HumanTaskWorkItemHandler;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.kogito.Application;
import org.kie.kogito.Config;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.WorkItemHandlerConfig;
import org.kie.kogito.process.workitem.Policy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JsonSchemaUtilTest {
    
    
    private final static String example ="{\"$schema\": \"http://json-schema.org/draft-07/schema#\",\n" + 
            "    \"type\": \"object\",\n" + 
            "    \"properties\": {\n" + 
            "        \"traveller\": {\n" + 
            "            \"type\": \"object\",\n" + 
            "            \"properties\": {\n" + 
            "                \"address\": {\n" + 
            "                    \"type\": \"object\",\n" + 
            "                    \"properties\": {\n" + 
            "                        \"city\": {\n" + 
            "                            \"type\": \"string\"\n" + 
            "                        },\n" + 
            "                        \"country\": {\n" + 
            "                            \"type\": \"string\"\n" + 
            "                        },\n" + 
            "                        \"street\": {\n" + 
            "                            \"type\": \"string\"\n" + 
            "                        },\n" + 
            "                        \"zipCode\": {\n" + 
            "                            \"type\": \"string\"\n" + 
            "                        }\n" + 
            "                    }\n" + 
            "                },\n" + 
            "                \"email\": {\n" + 
            "                    \"type\": \"string\"\n" + 
            "                },\n" + 
            "                \"firstName\": {\n" + 
            "                    \"type\": \"string\"\n" + 
            "                },\n" + 
            "                \"lastName\": {\n" + 
            "                    \"type\": \"string\"\n" + 
            "                },\n" + 
            "                \"nationality\": {\n" + 
            "                    \"type\": \"string\"\n" + 
            "                }\n" + 
            "            },\n" + 
            "            \"input\": true\n" + 
            "        },\n" + 
            "        \"approved\": {\n" + 
            "            \"type\": \"boolean\",\n" + 
            "            \"output\": true\n" + 
            "        }\n" + 
            "    }}";

    @Test
    void testJsonSchema() throws IOException {
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Map<String,Object> schemaMap = JsonSchemaUtil.load(in);
        in.close();
        assertEquals ("object", schemaMap.get("type"));
        Map<String,Object> properties = (Map<String,Object>)schemaMap.get("properties");
        assertEquals(2,properties.size());
        assertTrue((Boolean)((Map)properties.get("approved")).get("output"));
        assertTrue((Boolean)((Map)properties.get("traveller")).get("input"));
    }

    @Test
    <T> void testJsonSchemaPhases() throws IOException {
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Policy<T>[] policies = new Policy[0];
        Map<String, Object> schemaMap = JsonSchemaUtil.load(in);
        in.close();
        Process<T> process = mock(Process.class);
        ProcessInstances<T> processInstances = mock(ProcessInstances.class);
        when(process.instances()).thenReturn(processInstances);
        ProcessInstance<T> processInstance = mock(ProcessInstance.class);
        when(processInstances.findById("pepe", ProcessInstanceReadMode.READ_ONLY)).thenReturn((Optional) Optional.of(processInstance));
        WorkItem task = mock(WorkItem.class);
        when(processInstance.workItem("task", policies)).thenReturn(task);
        when(task.getPhase()).thenReturn("active");
        Application application = mock(Application.class);
        Config config = mock(Config.class);
        ProcessConfig processConfig = mock(ProcessConfig.class);
        when(application.config()).thenReturn(config);
        when(config.process()).thenReturn(processConfig);
        WorkItemHandlerConfig workItemHandlerConfig = mock(WorkItemHandlerConfig.class);
        when(processConfig.workItemHandlers()).thenReturn(workItemHandlerConfig);
        WorkItemHandler workItemHandler = new HumanTaskWorkItemHandler();
        when(workItemHandlerConfig.forName("Human Task")).thenReturn(workItemHandler);
        schemaMap = JsonSchemaUtil.addPhases(process, application, "pepe", "task", policies, schemaMap);
        assertFalse(((Collection) schemaMap.get("phases")).isEmpty());
    }
}
