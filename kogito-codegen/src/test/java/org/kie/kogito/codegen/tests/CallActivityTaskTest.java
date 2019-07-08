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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenTest;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;

import static org.assertj.core.api.Assertions.*;


public class CallActivityTaskTest extends AbstractCodegenTest {

    @Test
    public void testBasicCallActivityTask() throws Exception {
        
        Application app = generateCodeProcessesOnly("subprocess/CallActivity.bpmn2", "subprocess/CallActivitySubProcess.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.processes().processById("ParentProcess");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("x", "a");
        parameters.put("y", "b");
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED); 
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(2).containsKeys("x", "y");
        assertThat(result.toMap().get("y")).isNotNull().isEqualTo("new value");
        assertThat(result.toMap().get("x")).isNotNull().isEqualTo("a");
    }
    
    @Test
    public void testBasicCallActivityTaskWithTypeInfo() throws Exception {
        
        Application app = generateCodeProcessesOnly("subprocess/CallActivityWithTypeInfo.bpmn2", "subprocess/CallActivitySubProcess.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.processes().processById("ParentProcess");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("x", "a");
        parameters.put("y", "b");
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED); 
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(2).containsKeys("x", "y");
        assertThat(result.toMap().get("y")).isNotNull().isEqualTo("new value");
        assertThat(result.toMap().get("x")).isNotNull().isEqualTo("a");
    }
}
