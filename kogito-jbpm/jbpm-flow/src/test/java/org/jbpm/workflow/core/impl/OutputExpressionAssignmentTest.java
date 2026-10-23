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
package org.jbpm.workflow.core.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OutputExpressionAssignmentTest {

    public static class Person {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // The output <to> carries a generated element id (e.g. FormalExpression_3) that is NOT a process variable.
    private static final String TO_ID = "FormalExpression_3";

    private Map<String, Object> run(String toExpression, Function<String, Object> sourceResolver, Function<String, Object> targetResolver) throws Exception {
        DataDefinition from = new DataDefinition("DataOutput_1", "personOut", "java.lang.Object");
        DataDefinition to = new DataDefinition(TO_ID, "EXPRESSION (" + toExpression + ")", (String) null, toExpression);
        OutputExpressionAssignment assignment = new OutputExpressionAssignment(from, to);

        Map<String, Object> produced = new LinkedHashMap<>();
        assignment.execute(sourceResolver, targetResolver, produced::put);
        return produced;
    }

    @Test
    void bareVariable_isProducedUnderVariableName() throws Exception {
        Map<String, Object> produced = run("#{y}", name -> "personOut".equals(name) ? "value" : null, name -> null);

        assertThat(produced).containsOnly(Map.entry("y", "value"));
    }

    @Test
    void bareVariable_withSurroundingWhitespace_isProducedUnderVariableName() throws Exception {
        Map<String, Object> produced = run("#{ y }", name -> "personOut".equals(name) ? "value" : null, name -> null);

        assertThat(produced).containsOnly(Map.entry("y", "value"));
    }

    @Test
    void propertyPath_writesIntoTargetObjectAndProducesNothing() throws Exception {
        Person person = new Person();

        Map<String, Object> produced = run("#{person.name}",
                name -> "personOut".equals(name) ? "john" : null,
                name -> "person".equals(name) ? person : null);

        assertThat(person.getName()).isEqualTo("john");
        // must not leak the <to> element id (FormalExpression_N) as an undeclared process variable
        assertThat(produced).isEmpty();
    }
}
