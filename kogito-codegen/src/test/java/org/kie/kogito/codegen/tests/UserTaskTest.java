/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenTest;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;

public class UserTaskTest extends AbstractCodegenTest {
    
    @Test
    public void testBasicUserTaskProcess() throws Exception {
        
        Application app = generateCodeProcessesOnly("usertask/UserTasksProcess.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.processes().processById("UserTasksProcess");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE); 
        
        List<WorkItem> workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        assertEquals("FirstTask", workItems.get(0).getName());
        
        processInstance.completeWorkItem(workItems.get(0).getId(), null);
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        assertEquals("SecondTask", workItems.get(0).getName());
        
        processInstance.completeWorkItem(workItems.get(0).getId(), null);
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
    
    
    @Test
    public void testRESTApiForUserTasks() throws Exception {
        
        Application app = generateCodeProcessesOnly("usertask/UserTasksProcess.bpmn2");        
        assertThat(app).isNotNull();
        
        Class<?> resourceClazz = Class.forName("org.kie.kogito.test.UserTasksProcessResource", true, testClassLoader());
        assertNotNull(resourceClazz);
        Set<String> completeTaskPaths = new LinkedHashSet<>();
        Method[] methods = resourceClazz.getMethods();
        for (Method m : methods) {
            if (m.getName().startsWith("completeTask")) {
                Annotation[] annotations = m.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().getSimpleName().equals("Path")) {
                        completeTaskPaths.add(annotation.toString().replaceAll("\\\"", ""));
                    }
                }
            }
        }
        // there must be two distinct paths for user tasks
        assertThat(completeTaskPaths).hasSize(2).containsOnly("@javax.ws.rs.Path(value=/{id}/FirstTask/{workItemId})", 
                                                                 "@javax.ws.rs.Path(value=/{id}/SecondTask/{workItemId})");
    }
}
