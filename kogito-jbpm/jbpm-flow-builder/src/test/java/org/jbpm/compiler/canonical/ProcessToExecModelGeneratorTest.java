/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.compiler.canonical;

import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProcessToExecModelGeneratorTest {
    
    private static final Logger logger = LoggerFactory.getLogger(ProcessToExecModelGeneratorTest.class);

    @Test
    public void testScriptAndWorkItemGeneration() {

        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("demo.orders");
        factory
            .variable("order", new ObjectDataType("com.myspace.demo.Order"))
            .variable("approver", new ObjectDataType("String"))
            .name("orders")
            .packageName("com.myspace.demo")
            .dynamic(false)
            .version("1.0")
        .workItemNode(1)
            .name("Log")
            .workName("Log")
            .done()
        .actionNode(2)
            .name("Dump order")
            .action("java", "System.out.println(\"Order has been created \" + order);")
            .done()
        .endNode(3)
            .name("end")
            .terminate(false)
            .done()
        .startNode(4)
            .name("start")
            .done()
        .connection(2, 1)
        .connection(4, 2)
        .connection(1, 3);
        
        Process process = factory.validate().getProcess();
        
        ProcessMetaData processMetadata = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process);
        assertNotNull(processMetadata, "Dumper should return non null class for process");
        
        logger.debug(processMetadata.getGeneratedClassModel().toString());
        
        assertEquals("orders", processMetadata.getExtractedProcessId());
        assertEquals("demo.orders", processMetadata.getProcessId());
        assertEquals("orders", processMetadata.getProcessName());
        assertEquals("1.0", processMetadata.getProcessVersion());
        assertEquals("com.myspace.demo.OrdersProcess", processMetadata.getProcessClassName());
        assertNotNull(processMetadata.getGeneratedClassModel());
        assertEquals(1, processMetadata.getWorkItems().size());
    }
    
    @Test
    public void testScriptAndWorkItemModelGeneration() {

        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("demo.orders");
        factory
            .variable("order", new ObjectDataType("com.myspace.demo.Order"))
            .variable("approver", new ObjectDataType("String"))
            .name("orders")
            .packageName("com.myspace.demo")
            .dynamic(false)
            .version("1.0")
        .workItemNode(1)
            .name("Log")
            .workName("Log")
            .done()
        .actionNode(2)
            .name("Dump order")
            .action("java", "System.out.println(\"Order has been created \" + order);")
            .done()
        .endNode(3)
            .name("end")
            .terminate(false)
            .done()
        .startNode(4)
            .name("start")
            .done()
        .connection(2, 1)
        .connection(4, 2)
        .connection(1, 3);
        
        Process process = factory.validate().getProcess();
        
        ModelMetaData modelMetadata = ProcessToExecModelGenerator.INSTANCE.generateModel((WorkflowProcess) process);
        assertNotNull(modelMetadata, "Dumper should return non null class for process");
        
        logger.info(modelMetadata.generate());
        assertEquals("com.myspace.demo.OrdersModel", modelMetadata.getModelClassName());
    }

}
