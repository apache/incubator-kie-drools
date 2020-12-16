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
import org.kie.kogito.codegen.process.ProcessCodegenException;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.Sig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MessageIntermediateEventTest extends AbstractCodegenTest {
    
    @Test
    public void testMessageThrowEventProcess() throws Exception {
        
        Application app = generateCodeProcessesOnly("messageevent/IntermediateThrowEventMessage.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.get(Processes.class).processById("MessageIntermediateEvent");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();        
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();        
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("customerId");        
    }
    
    @Test
    public void testMessageCatchEventProcess() throws Exception {
        
        Application app = generateCodeProcessesOnly("messageevent/IntermediateCatchEventMessage.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.get(Processes.class).processById("IntermediateCatchEvent");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();        
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();     
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        processInstance.send(Sig.of("Message-customers", "CUS-00998877"));
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("customerId");
        assertThat(result.toMap().get("customerId")).isNotNull().isEqualTo("CUS-00998877");
    }
    
    @Test
    public void testMessageBoundaryCatchEventProcess() throws Exception {
        
        Application app = generateCodeProcessesOnly("messageevent/BoundaryMessageEventOnTask.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.get(Processes.class).processById("BoundaryMessageOnTask");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();        
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();     
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        processInstance.send(Sig.of("Message-customers", "CUS-00998877"));
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("customerId");
        assertThat(result.toMap().get("customerId")).isNotNull().isEqualTo("CUS-00998877");
    }

    @Test
    public void malformedShouldThrowException() {
        assertThrows(ProcessCodegenException.class, () -> {
            generateCodeProcessesOnly("messageevent/EventNodeMalformed.bpmn2");
        });
    }
}
