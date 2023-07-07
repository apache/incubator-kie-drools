/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.util.StringUtils;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyReactivityMatrixTest extends BaseModelTest {

    enum Dialect {
        JAVA,
        MVEL
    }

    private Dialect dialect;

    @Parameters(name = "{0} {1}")
    public static Collection<Object[]> parameters() {
        List<Object[]> parameterData = new ArrayList<Object[]>();
        for (Object runType : PLAIN) {
            for (Dialect dialect : Dialect.values()) {
                parameterData.add(new Object[]{runType, dialect});
            }
        }
        return parameterData;
    }

    public PropertyReactivityMatrixTest(RUN_TYPE testRunType, Dialect testDialect) {
        super(testRunType);
        this.dialect = testDialect;
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

    private String setValueStatement(String property, int value) {
        if (dialect == Dialect.JAVA) {
            return "set" + StringUtils.ucFirst(property) + "(" + value + ");";
        } else {
            return property + " = " + value + ";";
        }
    }

    @Test
    public void modifyInsideIfTrueBlock_shouldTriggerReactivity() {
        statementInsideBlock_shouldTriggerReactivity("    if (true) {\n" +
                                                     "      modify($fact) {\n" +
                                                     "        " + setValueStatement("value1", 2) + "\n" +
                                                     "      }\n" +
                                                     "    }\n");
    }

    @Test
    public void modifyInsideForBlock_shouldTriggerReactivity() {
        statementInsideBlock_shouldTriggerReactivity("    for (int i = 0; i < 1; i++) {\n" +
                                                     "      modify($fact) {\n" +
                                                     "        " + setValueStatement("value1", 2) + "\n" +
                                                     "      }\n" +
                                                     "    }\n");
    }

    @Test
    public void modifyInsideCommentedIfTrueBlock_shouldTriggerReactivity() {
        statementInsideBlock_shouldTriggerReactivity("    // if (true) {\n" +
                                                     "      modify($fact) {\n" +
                                                     "        " + setValueStatement("value1", 2) + "\n" +
                                                     "      }\n" +
                                                     "    // }\n");
    }

    @Test
    public void modifyInsideIfBlockInsideAndOutsideAssignment_shouldTriggerReactivity() {
        statementInsideBlock_shouldTriggerReactivity("    $fact." + setValueStatement("value1", 2) + "\n" +
                                                     "    if (true) {\n" +
                                                     "      modify($fact) {\n" +
                                                     "        " + setValueStatement("value2", 2) + "\n" +
                                                     "      }\n" +
                                                     "    }");
    }

    @Test
    public void updateInsideIfTrueBlock_shouldTriggerReactivity() {
        statementInsideBlock_shouldTriggerReactivity("    if (true) {\n" +
                                                     "      $fact." + setValueStatement("value1", 2) + ";\n" +
                                                     "      update($fact);\n" +
                                                     "    }\n");
    }

    @Test
    public void updateInsideForBlock_shouldTriggerReactivity() {
        statementInsideBlock_shouldTriggerReactivity("    for (int i = 0; i < 1; i++) {\n" +
                                                     "      $fact." + setValueStatement("value1", 2) + "\n" +
                                                     "      update($fact);\n" +
                                                     "    }\n");
    }

    @Test
    public void updateInsideCommentedIfTrueBlock_shouldTriggerReactivity() {
        statementInsideBlock_shouldTriggerReactivity("    // if (true) {\n" +
                                                     "      $fact." + setValueStatement("value1", 2) + "\n" +
                                                     "      update($fact);\n" +
                                                     "    // }\n");
    }

    @Test
    public void updateInsideIfBlockInsideAndOutsideAssignment_shouldTriggerReactivity() {
        statementInsideBlock_shouldTriggerReactivity("    $fact." + setValueStatement("value1", 2) + "\n" +
                                                     "    if (true) {\n" +
                                                     "      $fact." + setValueStatement("value2", 2) + "\n" +
                                                     "      update($fact);\n" +
                                                     "    }");
    }

    private void statementInsideBlock_shouldTriggerReactivity(String rhs) {
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

        KieSession ksession = getKieSession(str);
        List<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Fact fact = new Fact(1);
        ksession.insert(fact);
        ksession.fireAllRules();

        assertThat(results).as("Should trigger property reactivity on value1")
                           .containsExactly("R2 fired");
    }
}
