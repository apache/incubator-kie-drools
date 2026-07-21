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
package org.kie.kogito.codegen.process;

import java.util.stream.Stream;

import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.core.validation.ProcessValidator;
import org.jbpm.process.core.validation.ProcessValidatorRegistry;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.ruleflow.core.WorkflowElementIdentifierFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.definition.process.WorkflowElementIdentifier;
import org.kie.kogito.process.validation.ValidationException;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

public class JavaRuleFlowProcessValidatorTest {

    private static WorkflowElementIdentifier one = WorkflowElementIdentifierFactory.fromExternalFormat("one");
    private static WorkflowElementIdentifier two = WorkflowElementIdentifierFactory.fromExternalFormat("two");
    private static WorkflowElementIdentifier three = WorkflowElementIdentifierFactory.fromExternalFormat("three");

    @BeforeAll
    static void init() {
        ProcessValidatorRegistry.getInstance().registerAdditonalValidator(JavaRuleFlowProcessValidator.getInstance());
    }

    public static Stream<Arguments> invalidVariables() {
        return Stream.of(
                Arguments.of(new String[] {
                        "com.myspace.demo.Order order2 = null; System.out.println(\"Order has been created \" + order);java.util.Arrays.toString(new int[]{1, 2});System.out.println(orders);",
                        "uses unknown variable in the script: orders" }),
                Arguments.of(new String[] {
                        "a = 2",
                        "Parse error. Found \"}\", expected one of  \"!=\" \"%\" \"%=\" \"&\" \"&&\" \"&=\" \"*\" \"*=\" \"+\" \"+=\" \"-\" \"-=\" \"->\" \"/\" \"/=\" \"::\" \";\" \"<\" \"<<=\" \"<=\" \"=\" \"==\" \">\" \">=\" \">>=\" \">>>=\" \"?\" \"^\" \"^=\" \"instanceof\" \"|\" \"|=\" \"||\"" }),
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
                .startNode(one)
                .name("start")
                .done()
                .actionNode(two)
                .name("Dump order 1")
                .action("java", script)
                .done()
                .endNode(three)
                .name("end")
                .terminate(false)
                .done()
                .connection(one, two)
                .connection(two, three);
        RuleFlowProcess process = factory.getProcess();
        ProcessValidator validator = ProcessValidatorRegistry.getInstance().getValidator(process, null);
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> validator.validate(process))
                .withMessageContaining(message);
    }

    @Test
    public void testScriptWithTrailingCommentLine() {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("demo.orders");
        factory
                .variable("order", new ObjectDataType("com.myspace.demo.Order"))
                .variable("approver", new ObjectDataType("String"))
                .name("orders")
                .packageName("com.myspace.demo")
                .dynamic(false)
                .version("1.0")
                .startNode(one)
                .name("start")
                .done()
                .actionNode(two)
                .name("Dump order 1")
                .action("java", "System.out.println(\"test\");\n// this is a comment")
                .done()
                .endNode(three)
                .name("end")
                .terminate(false)
                .done()
                .connection(one, two)
                .connection(two, three);
        RuleFlowProcess process = factory.getProcess();
        ProcessValidator validator = ProcessValidatorRegistry.getInstance().getValidator(process, null);
        assertThatNoException()
                .isThrownBy(() -> validator.validate(process));
    }

    @Test
    public void testScriptWithLambdaParameterRecognized() {
        // 'i' is declared by the lambda validator should NOT complain
        String script =
                "java.util.List<Integer> list = java.util.Arrays.asList(1,2,3);" +
                        "Integer first = list.stream().filter(i -> i > 0).findFirst().orElse(null);";

        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("demo.lambda.ok");
        factory
                .name("lambda")
                .packageName("com.myspace.demo")
                .dynamic(false)
                .version("1.0")
                .startNode(one).name("start").done()
                .actionNode(two).name("Lambda")
                .action("java", script).done()
                .endNode(three).name("end").terminate(false).done()
                .connection(one, two).connection(two, three);

        RuleFlowProcess process = factory.getProcess();
        ProcessValidator validator = ProcessValidatorRegistry.getInstance().getValidator(process, null);
        assertThatNoException().isThrownBy(() -> validator.validate(process));
    }

    @Test
    public void testScriptWithUnknownVariableInsideLambdaFails() {
        // 'j' is NOT declared anywhere validator should still catch unknowns
        String script =
                "java.util.List<Integer> list = java.util.Arrays.asList(1,2,3);" +
                        "list.stream().filter(i -> j > 0).findFirst().orElse(null);";

        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("demo.lambda.bad");
        factory
                .name("lambda-exception")
                .packageName("com.myspace.demo")
                .dynamic(false)
                .version("1.0")
                .startNode(one).name("start").done()
                .actionNode(two).name("Lambda")
                .action("java", script).done()
                .endNode(three).name("end").terminate(false).done()
                .connection(one, two).connection(two, three);

        RuleFlowProcess process = factory.getProcess();
        ProcessValidator validator = ProcessValidatorRegistry.getInstance().getValidator(process, null);
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> validator.validate(process))
                .withMessageContaining("uses unknown variable in the script: j");
    }

}
