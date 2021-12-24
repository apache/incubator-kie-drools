/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.compiler.canonical;

import java.util.stream.Stream;

import org.jbpm.process.builder.dialect.feel.FeelCompilationException;
import org.jbpm.process.core.datatype.impl.type.IntegerDataType;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.core.datatype.impl.type.StringDataType;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.workflow.core.node.Split;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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

        WorkflowProcess process = factory.validate().getProcess();

        ProcessMetaData processMetadata = ProcessToExecModelGenerator.INSTANCE.generate(process);
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

    public static Stream<Arguments> invalidVariables() {
        return Stream.of(
                Arguments.of(new String[] {
                        "com.myspace.demo.Order order2 = null; System.out.println(\"Order has been created \" + order);java.util.Arrays.toString(new int[]{1, 2});System.out.println(orders);",
                        "uses unknown variable in the script: orders" }),
                Arguments.of(new String[] {
                        "a = 2",
                        "unable to parse Java content: Parse error. Found \"}\", expected one of  \"!=\" \"%\" \"%=\" \"&\" \"&&\" \"&=\" \"*\" \"*=\" \"+\" \"+=\" \"-\" \"-=\" \"->\" \"/\" \"/=\" \"::\" \";\" \"<\" \"<<=\" \"<=\" \"=\" \"==\" \">\" \">=\" \">>=\" \">>>=\" \"?\" \"^\" \"^=\" \"instanceof\" \"|\" \"|=\" \"||\"" }),
                Arguments.of(new String[] {
                        "a = 2;",
                        "uses unknown variable in the script: a" }),
                Arguments.of(new String[] {
                        "a.toString(); Integer i = Integer.valueOf(\"1\");",
                        "uses unknown variable in the script: a" }),
                Arguments.of(new String[] {
                        "System.out.println(\"Order has been created \" + x);",
                        "uses unknown variable in the script: x" }),
                Arguments.of(new String[] {
                        "System.out.println(\"[\" + (new java.util.Date()) + \"] [\" + java.lang.Thread.currentThread().getName() +\"]\");\n" +
                                "java.util.ArrayList list = new java.util.ArrayList();\n" +
                                "System.out.println(Integer.valueOf(x));",
                        "uses unknown variable in the script: x" }));
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("invalidVariables")
    public void testScriptInvalidVariable(String script, String message) {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("demo.orders");
        factory
                .variable("order", new ObjectDataType("com.myspace.demo.Order"))
                .variable("approver", new ObjectDataType("String"))
                .name("orders")
                .packageName("com.myspace.demo")
                .dynamic(false)
                .version("1.0")
                .startNode(1)
                .name("start")
                .done()
                .actionNode(2)
                .name("Dump order 1")
                .action("java", script)
                .done()
                .endNode(3)
                .name("end")
                .terminate(false)
                .done()
                .connection(1, 2)
                .connection(2, 3);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> factory.validate())
                .withMessage(format("Process could not be validated ![Process 'orders' [demo.orders]: Node 'Dump order 1' [2] %s]", message));
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

    @Test
    public void testGatewayFEEL() {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("demo.orders");
        factory
                .variable("approver", new StringDataType())
                .name("orders")
                .packageName("com.myspace.demo")
                .dynamic(false)
                .version("1.0")
                .splitNode(1)
                .type(Split.TYPE_XOR)
                .constraint(2, "cA", "code", "FEEL", "if true then true else false")
                .constraint(3, "cB", "code", "java", "return false; ")
                .done()
                .endNode(2)
                .name("end A")
                .terminate(false)
                .done()
                .endNode(3)
                .name("end B")
                .terminate(false)
                .done()
                .startNode(4)
                .name("start")
                .done()
                .connection(4, 1)
                .connection(1, 2, "cA")
                .connection(1, 3, "cB");

        WorkflowProcess process = factory.validate().getProcess();

        ProcessMetaData processMetadata = ProcessToExecModelGenerator.INSTANCE.generate(process);
        assertNotNull(processMetadata, "Dumper should return non null class for process");

        logger.debug(processMetadata.getGeneratedClassModel().toString());
        assertNotNull(processMetadata.getGeneratedClassModel());
    }

    @Test
    public void testGatewayFEELWrongIfMissingElse() {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("demo.orders");
        factory
                .variable("approver", new StringDataType())
                .name("orders")
                .packageName("com.myspace.demo")
                .dynamic(false)
                .version("1.0")
                .splitNode(1)
                .type(Split.TYPE_XOR)
                .constraint(2, "cA", "code", "FEEL", "if true then true") // missing else
                .constraint(3, "cB", "code", "java", "return false; ")
                .done()
                .endNode(2)
                .name("end A")
                .terminate(false)
                .done()
                .endNode(3)
                .name("end B")
                .terminate(false)
                .done()
                .startNode(4)
                .name("start")
                .done()
                .connection(4, 1)
                .connection(1, 2, "cA")
                .connection(1, 3, "cB");

        assertThatExceptionOfType(FeelCompilationException.class)
                .isThrownBy(() -> {
                    WorkflowProcess process = factory.validate().getProcess();
                    ProcessMetaData processMetadata = ProcessToExecModelGenerator.INSTANCE.generate(process);
                })
                .withStackTraceContaining("Detected 'if' expression without 'else' part");
    }

    @Test
    public void testGatewayFEELwrongUnkVariable() {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("demo.orders");
        factory
                .variable("approver", new StringDataType())
                .name("orders")
                .packageName("com.myspace.demo")
                .dynamic(false)
                .version("1.0")
                .splitNode(1)
                .type(Split.TYPE_XOR)
                .constraint(2, "cA", "code", "FEEL", "approver=\"Matteo\"")
                .constraint(3, "cB", "code", "FEEL", "unexisting=1") // unknown variable unexisting
                .done()
                .endNode(2)
                .name("end A")
                .terminate(false)
                .done()
                .endNode(3)
                .name("end B")
                .terminate(false)
                .done()
                .startNode(4)
                .name("start")
                .done()
                .connection(4, 1)
                .connection(1, 2, "cA")
                .connection(1, 3, "cB");

        assertThatExceptionOfType(FeelCompilationException.class)
                .isThrownBy(() -> {
                    WorkflowProcess process = factory.validate().getProcess();
                    ProcessMetaData processMetadata = ProcessToExecModelGenerator.INSTANCE.generate(process);
                })
                .withStackTraceContaining("Unknown variable 'unexisting'");
    }

    @Test
    public void testScriptVariablewithDefaultValue() {

        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("demo.orders");
        factory
                .variable("order", new ObjectDataType("com.myspace.demo.Order"))
                .variable("approver", new StringDataType(), "john", "customTags", null)
                .variable("age", new IntegerDataType(), "1", "customTags", null)
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

        WorkflowProcess process = factory.validate().getProcess();

        ProcessMetaData processMetadata = ProcessToExecModelGenerator.INSTANCE.generate(process);
        assertNotNull(processMetadata, "Dumper should return non null class for process");
    }
}
