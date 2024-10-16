/**
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
package org.drools.model.codegen.execmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.drools.util.StringUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class PropertyReactivityMatrixTest extends BaseModelTest2 {

    enum Dialect {
        JAVA,
        MVEL
    }

    public static Stream<Arguments> parametersData() {
        List<Arguments> parameterData = new ArrayList<Arguments>();
        for (Object runType : PLAIN) {
            for (Dialect dialect : Dialect.values()) {
                parameterData.add(arguments(runType, dialect));
            }
        }
        return Stream.of(parameterData.toArray(new Arguments[0]));
    }


    public static class Fact {

        private int value1;
        private int value2;

        public Fact(int value1) {
            this.value1 = value1;
        }

        public Fact(int value1, int value2) {
            this.value1 = value1;
            this.value2 = value2;
        }

        public int getValue1() {
            return value1;
        }

        public void setValue1(int value1) {
            this.value1 = value1;
        }

        public int getValue2() {
            return value2;
        }

        public void setValue2(int value2) {
            this.value2 = value2;
        }
    }

    private String setValueStatement(Dialect dialect, String property, int value) {
        if (dialect == Dialect.JAVA) {
            return "set" + StringUtils.ucFirst(property) + "(" + value + ");";
        } else {
            return property + " = " + value + ";";
        }
    }

    @ParameterizedTest
	@MethodSource("parametersData")
    public void modifyInsideIfTrueBlock_shouldTriggerReactivity(RUN_TYPE runType, Dialect dialect) {
        statementInsideBlock_shouldTriggerReactivity(runType, dialect, "    if (true) {\n" +
                                                     "      modify($fact) {\n" +
                                                     "        " + setValueStatement(dialect, "value1", 2) + "\n" +
                                                     "      }\n" +
                                                     "    }\n");
    }

    @ParameterizedTest
	@MethodSource("parametersData")
    public void modifyInsideForBlock_shouldTriggerReactivity(RUN_TYPE runType, Dialect dialect) {
        statementInsideBlock_shouldTriggerReactivity(runType, dialect, "    for (int i = 0; i < 1; i++) {\n" +
                                                     "      modify($fact) {\n" +
                                                     "        " + setValueStatement(dialect, "value1", 2) + "\n" +
                                                     "      }\n" +
                                                     "    }\n");
    }

    @ParameterizedTest
	@MethodSource("parametersData")
    public void modifyInsideCommentedIfTrueBlock_shouldTriggerReactivity(RUN_TYPE runType, Dialect dialect) {
        statementInsideBlock_shouldTriggerReactivity(runType, dialect, "    // if (true) {\n" +
                                                     "      modify($fact) {\n" +
                                                     "        " + setValueStatement(dialect, "value1", 2) + "\n" +
                                                     "      }\n" +
                                                     "    // }\n");
    }

    @ParameterizedTest
	@MethodSource("parametersData")
    public void modifyInsideIfBlockInsideAndOutsideAssignment_shouldTriggerReactivity(RUN_TYPE runType, Dialect dialect) {
        statementInsideBlock_shouldTriggerReactivity(runType, dialect, "    $fact." + setValueStatement(dialect, "value1", 2) + "\n" +
                                                     "    if (true) {\n" +
                                                     "      modify($fact) {\n" +
                                                     "        " + setValueStatement(dialect, "value2", 2) + "\n" +
                                                     "      }\n" +
                                                     "    }");
    }

    @ParameterizedTest
	@MethodSource("parametersData")
    public void updateInsideIfTrueBlock_shouldTriggerReactivity(RUN_TYPE runType, Dialect dialect) {
        statementInsideBlock_shouldTriggerReactivity(runType, dialect, "    if (true) {\n" +
                                                     "      $fact." + setValueStatement(dialect, "value1", 2) + ";\n" +
                                                     "      update($fact);\n" +
                                                     "    }\n");
    }

    @ParameterizedTest
	@MethodSource("parametersData")
    public void updateInsideForBlock_shouldTriggerReactivity(RUN_TYPE runType, Dialect dialect) {
        statementInsideBlock_shouldTriggerReactivity(runType, dialect, "    for (int i = 0; i < 1; i++) {\n" +
                                                     "      $fact." + setValueStatement(dialect, "value1", 2) + "\n" +
                                                     "      update($fact);\n" +
                                                     "    }\n");
    }

    @ParameterizedTest
	@MethodSource("parametersData")
    public void updateInsideCommentedIfTrueBlock_shouldTriggerReactivity(RUN_TYPE runType, Dialect dialect) {
        statementInsideBlock_shouldTriggerReactivity(runType, dialect, "    // if (true) {\n" +
                                                     "      $fact." + setValueStatement(dialect, "value1", 2) + "\n" +
                                                     "      update($fact);\n" +
                                                     "    // }\n");
    }

    @ParameterizedTest
	@MethodSource("parametersData")
    public void updateInsideIfBlockInsideAndOutsideAssignment_shouldTriggerReactivity(RUN_TYPE runType, Dialect dialect) {
        statementInsideBlock_shouldTriggerReactivity(runType, dialect, "    $fact." + setValueStatement(dialect, "value1", 2) + "\n" +
                                                     "    if (true) {\n" +
                                                     "      $fact." + setValueStatement(dialect, "value2", 2) + "\n" +
                                                     "      update($fact);\n" +
                                                     "    }");
    }

    @ParameterizedTest
	@MethodSource("parametersData")
    public void assignmentAfterModify_shouldTriggerReactivity(RUN_TYPE runType, Dialect dialect) {
        // DROOLS-7497
        statementInsideBlock_shouldTriggerReactivity(runType, dialect, "      modify($fact) {\n" +
                                                     "      " + setValueStatement(dialect, "value2", 2) + "\n" +
                                                     "      }\n" +
                                                     "    $fact." + setValueStatement(dialect, "value1", 2) + "\n");
    }

    @ParameterizedTest
	@MethodSource("parametersData")
    public void assignmentBeforeAndAfterModify_shouldTriggerReactivity(RUN_TYPE runType, Dialect dialect) {
        // DROOLS-7497
        statementInsideBlock_shouldTriggerReactivity(runType, dialect, "      $fact." + setValueStatement(dialect, "value2", 2) + "\n" +
                                                     "      modify($fact) {\n" +
                                                     "      }\n" +
                                                     "    $fact." + setValueStatement(dialect, "value1", 2) + "\n");
    }

    @ParameterizedTest
	@MethodSource("parametersData")
    public void assignmentAfterIfBlockModify_shouldTriggerReactivity(RUN_TYPE runType, Dialect dialect) {
        // DROOLS-7497
        statementInsideBlock_shouldTriggerReactivity(runType, dialect, "      if (true) {\n" +
                                                     "      modify($fact) {\n" +
                                                     "        " + setValueStatement(dialect, "value2", 2) + "\n" +
                                                     "      }\n" +
                                                     "    }\n" +
                                                     "    $fact." + setValueStatement(dialect, "value1", 2) + "\n");
    }

    @ParameterizedTest
	@MethodSource("parametersData")
    public void assignmentBeforeAndAfterUpdate_shouldTriggerReactivity(RUN_TYPE runType, Dialect dialect) {
        // DROOLS-7497
        statementInsideBlock_shouldTriggerReactivity(runType, dialect, "      $fact." + setValueStatement(dialect, "value2", 2) + "\n" +
                                                     "      update($fact);\n" +
                                                     "    $fact." + setValueStatement(dialect, "value1", 2) + "\n");
    }

    @ParameterizedTest
	@MethodSource("parametersData")
    public void assignmentAfterIfBlockUpdate_shouldTriggerReactivity(RUN_TYPE runType, Dialect dialect) {
        // DROOLS-7497
        statementInsideBlock_shouldTriggerReactivity(runType, dialect, "      if (true) {\n" +
                                                     "      update($fact);\n" +
                                                     "    }\n" +
                                                     "    $fact." + setValueStatement(dialect, "value1", 2) + "\n");
    }

    private void statementInsideBlock_shouldTriggerReactivity(RUN_TYPE runType, Dialect dialect, String rhs) {
        final String str =
                "import " + Fact.class.getCanonicalName() + ";\n" +
                           "dialect \"" + dialect.name().toLowerCase() + "\"\n" +
                           "global java.util.List results;\n" +
                           "rule R1\n" +
                           "  when\n" +
                           "    $fact : Fact( value1 == 1 )\n" +
                           "  then\n" +
                           rhs +
                           "end\n" +
                           "rule R2\n" +
                           "  when\n" +
                           "    $fact : Fact( value1 == 2 )\n" +
                           "  then\n" +
                           "    results.add(\"R2 fired\");\n" +
                           "end\n";

        KieSession ksession = getKieSession(runType, str);
        List<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Fact fact = new Fact(1);
        ksession.insert(fact);
        ksession.fireAllRules();

        assertThat(results).as("Should trigger property reactivity on value1")
                           .containsExactly("R2 fired");
    }

    @ParameterizedTest
	@MethodSource("parametersData")
    public void modifyInsideIfFalseAndTrueBlock_shouldTriggerReactivity(RUN_TYPE runType, Dialect dialect) {
        // DROOLS-7493
        statementInsideBlock_shouldTriggerReactivityWithLoop(runType, dialect, "    if (false) {\n" +
                                                             "      $fact." + setValueStatement(dialect, "value1", 2) + "\n" + // this line is not executed, but analyzed as a property reactivity
                                                             "    }\n" +
                                                             "    if (true) {\n" +
                                                             "      modify($fact) {\n" +
                                                             "      }\n" +
                                                             "    }\n");
    }

    @ParameterizedTest
	@MethodSource("parametersData")
    public void updateInsideIfFalseAndTrueBlock_shouldTriggerReactivity(RUN_TYPE runType, Dialect dialect) {
        statementInsideBlock_shouldTriggerReactivityWithLoop(runType, dialect, "    if (false) {\n" +
                                                             "      $fact." + setValueStatement(dialect, "value1", 2) + "\n" + // this line is not executed, but analyzed as a property reactivity
                                                             "    }\n" +
                                                             "    if (true) {\n" +
                                                             "      update($fact);\n" +
                                                             "    }\n");
    }

    private void statementInsideBlock_shouldTriggerReactivityWithLoop(RUN_TYPE runType, Dialect dialect, String rhs) {
        final String str =
                "import " + Fact.class.getCanonicalName() + ";\n" +
                           "dialect \"" + dialect.name().toLowerCase() + "\"\n" +
                           "global java.util.List results;\n" +
                           "rule R1\n" +
                           "  when\n" +
                           "    $fact : Fact( value1 == 1 )\n" +
                           "  then\n" +
                           rhs +
                           "end\n";

        KieSession ksession = getKieSession(runType, str);
        List<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Fact fact = new Fact(1);
        ksession.insert(fact);
        int fired = ksession.fireAllRules(10);

        assertThat(fired).as("Should trigger property reactivity on value1 and result in a loop")
                         .isEqualTo(10);
    }

    @ParameterizedTest
	@MethodSource("parametersData")
    public void modifyInsideIfFalseBlock_shouldNotTriggerReactivity(RUN_TYPE runType, Dialect dialect) {
        // DROOLS-7493
        statementInsideIfFalseBlock_shouldNotTriggerReactivityNorSelfLoop(runType, dialect, "    if (false) {\n" +
                                                                          "      modify($fact) {\n" +
                                                                          "        " + setValueStatement(dialect, "value1", 2) + "\n" +
                                                                          "      }\n" +
                                                                          "    }\n");
    }

    @ParameterizedTest
	@MethodSource("parametersData")
    public void updateInsideIfFalseBlock_shouldNotTriggerReactivity(RUN_TYPE runType, Dialect dialect) {
        statementInsideIfFalseBlock_shouldNotTriggerReactivityNorSelfLoop(runType, dialect, "    if (false) {\n" +
                                                                          "      $fact." + setValueStatement(dialect, "value1", 2) + "\n" +
                                                                          "      update($fact);\n" +
                                                                          "    }\n");
    }

    private void statementInsideIfFalseBlock_shouldNotTriggerReactivityNorSelfLoop(RUN_TYPE runType, Dialect dialect, String rhs) {
        final String str =
                "import " + Fact.class.getCanonicalName() + ";\n" +
                           "dialect \"" + dialect.name().toLowerCase() + "\"\n" +
                           "global java.util.List results;\n" +
                           "rule R1\n" +
                           "  when\n" +
                           "    $fact : Fact( value1 == 1 )\n" +
                           "  then\n" +
                           rhs +
                           "end\n" +
                           "rule R2\n" +
                           "  when\n" +
                           "    $fact : Fact( value1 == 2 )\n" +
                           "  then\n" +
                           "    results.add(\"R2 fired\");\n" +
                           "end\n";

        KieSession ksession = getKieSession(runType, str);
        List<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Fact fact = new Fact(1);
        ksession.insert(fact);
        int fired = ksession.fireAllRules(10);
        assertThat(fired).as("Don't cause a loop")
                         .isEqualTo(1);

        assertThat(results).as("Shouldn't trigger R2, because modify is not executed")
                           .isEmpty();
    }
}
