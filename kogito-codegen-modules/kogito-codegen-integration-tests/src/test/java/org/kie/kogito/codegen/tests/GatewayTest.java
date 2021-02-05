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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenTest;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.Sig;

public class GatewayTest extends AbstractCodegenTest {
    
    @Test
    public void testEventBasedGatewayWithData() throws Exception {
        Map<TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("gateway/EventBasedSplit.bpmn2"));
        resourcesTypeMap.put(TYPE.RULES, Collections.singletonList("ruletask/BusinessRuleTask.drl"));
        Application app = generateCode(resourcesTypeMap);
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.get(Processes.class).processById("EventBasedSplit");
        
        Model m = p.createModel();
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        processInstance.send(Sig.of("First", "test"));
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("x");
        assertThat(result.toMap().get("x")).isEqualTo("test");
                
        assertThat(p.instances().size()).isZero();
        
        // not test the other branch
        processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        processInstance.send(Sig.of("Second", "value"));
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        
        result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("x");
        assertThat(result.toMap().get("x")).isEqualTo("value");
                
        assertThat(p.instances().size()).isZero();
    }
    
}
