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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenTest;
import org.kie.kogito.codegen.process.ProcessCodegenException;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServiceTaskTest extends AbstractCodegenTest {
    
    @Test
    public void testBasicServiceProcessTask() throws Exception {
        
        Application app = generateCodeProcessesOnly("servicetask/ServiceProcess.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.get(Processes.class).processById("ServiceProcess");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("s", "john");
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.startDate()).isNotNull();
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED); 
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("s");
        assertThat(result.toMap().get("s")).isNotNull().isEqualTo("Hello john!");
    }
    
    @Test
    public void testServiceProcessDifferentOperationsTask() throws Exception {
        
        Application app = generateCodeProcessesOnly("servicetask/ServiceProcessDifferentOperations.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.get(Processes.class).processById("ServiceProcessDifferentOperations");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("s", "john");
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.startDate()).isNotNull();
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED); 
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("s");
        assertThat(result.toMap().get("s")).isNotNull().isEqualTo("Goodbye Hello john!!");
    }
    
    @Test
    public void testServiceProcessDifferentOperationsTaskFromAnotherNode() throws Exception {
        
        Application app = generateCodeProcessesOnly("servicetask/ServiceProcessDifferentOperations.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.get(Processes.class).processById("ServiceProcessDifferentOperations");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("s", "john");
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.startFrom("_A1EE8114-BF7B-4DAF-ABD7-62EEDCFAEFD4");
        
        assertThat(processInstance.startDate()).isNotNull();
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED); 
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("s");
        assertThat(result.toMap().get("s")).isNotNull().isEqualTo("Goodbye john!");
    }
    
    @Test
    public void testServiceProcessSameOperationsTask() throws Exception {
        
        Application app = generateCodeProcessesOnly("servicetask/ServiceProcessSameOperations.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.get(Processes.class).processById("ServiceProcessSameOperations");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("s", "john");
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED); 
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("s");
        assertThat(result.toMap().get("s")).isNotNull().isEqualTo("Hello Hello john!!");
    }
    
    @Test
    public void testBasicServiceProcessTaskMultiinstance() throws Exception {
        
        Application app = generateCodeProcessesOnly("servicetask/ServiceProcessMI.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.get(Processes.class).processById("ServiceProcess");
        
        List<String> list = new ArrayList<String>();
        list.add("first");
        list.add("second");
        List<String> listOut = new ArrayList<String>();
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("list", list);
        parameters.put("listOut", listOut);
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED); 
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(3).containsKeys("list", "s", "listOut");
        assertThat((List<String>)result.toMap().get("listOut")).isNotNull().hasSize(2).contains("Hello first!", "Hello second!");
    }

    @Test
    public void malformedShouldThrowException() throws Exception {
        assertThrows(ProcessCodegenException.class, () -> {
            generateCodeProcessesOnly("servicetask/ServiceProcessMalformed.bpmn2");
        });
    }

    @Test
    public void shouldInferMethodSignatureFromClass() throws Exception {
        // should no throw
        generateCodeProcessesOnly("servicetask/ServiceProcessInferMethod.bpmn2");
    }
    
    @Test
    public void testMultiParamServiceProcessTask() throws Exception {
        
        Application app = generateCodeProcessesOnly("servicetask/MultiParamServiceProcess.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.get(Processes.class).processById("ServiceProcess");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("s", "john");
        parameters.put("x", "doe");
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED); 
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(2).containsKeys("s", "x");
        assertThat(result.toMap().get("s")).isNotNull().isEqualTo("Hello (first and lastname) john doe!");
    }
    
    @Test
    public void testMultiParamConstantServiceProcessTask() throws Exception {
        
        Application app = generateCodeProcessesOnly("servicetask/MultiParamServiceProcessConstant.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.get(Processes.class).processById("ServiceProcess");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("s", "john");
        parameters.put("x", "doe");
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED); 
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(2).containsKeys("s", "x");
        assertThat(result.toMap().get("s")).isNotNull().isEqualTo("Hello (first and lastname) john Test!");
    }
    
    @Test
    public void testMultiParamServiceProcessTaskNoOutput() throws Exception {
        
        Application app = generateCodeProcessesOnly("servicetask/MultiParamServiceProcessNoOutput.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.get(Processes.class).processById("MultiParamServiceProcessNoOutput");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", "john");
        parameters.put("age", 35);
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED); 
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(2).containsKeys("name", "age");
        
    }

    @Test
    public void testMultiParamServiceCustomResultProcessTask() throws Exception {
        
        Application app = generateCodeProcessesOnly("servicetask/MultiParamCustomResultServiceTask.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.get(Processes.class).processById("services");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", "john");
        parameters.put("age", 35);
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED); 
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(3).containsKeys("name", "age");
        
        assertThat(result.toMap().get("result")).isNotNull().isEqualTo("Hello john 35!");
        
    }

    @Test
    public void testOverloadedService() throws Exception {

        Application app = generateCodeProcessesOnly("servicetask/ServiceProcessOverloaded.bpmn2");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("ServiceProcessOverloaded");
        ProcessInstance<?> processInstance = p.createInstance(p.createModel());
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
}
