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

import java.math.BigDecimal;

import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.domain.Result;
import org.junit.Test;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;

import static org.assertj.core.api.Assertions.assertThat;

public class CompilationFailuresTest extends BaseModelTest {

    public CompilationFailuresTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testNonQuotedStringComapre() {
        String drl =
                "declare Fact\n" +
                "    field : String\n" +
                "end\n" +
                "rule R when\n" +
                "    Fact( field == someString )\n" +
                "then\n" +
                "end\n";

        Results results = getCompilationResults(drl);
        assertThat(results.getMessages(Message.Level.ERROR).isEmpty()).isFalse();

        // line = -1 even with STANDARD_FROM_DRL (PredicateDescr)
        assertThat(results.getMessages().get(0).getLine()).isEqualTo(-1);
    }

    @Test
    public void testUseNotExistingEnum() {
        String drl =
                "import " + NumberRestriction.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "    NumberRestriction( valueType == Field.INT || == Field.DOUBLE )\n" +
                "then\n" +
                "end\n";

        Results results = getCompilationResults(drl);
        assertThat(results.getMessages(Message.Level.ERROR).isEmpty()).isFalse();

        assertThat(results.getMessages().get(0).getLine()).isEqualTo(3);
    }

    private Results getCompilationResults( String drl ) {
        return createKieBuilder( drl ).getResults();
    }

    public static class NumberRestriction {

        private Number value;

        public void setValue(Number number) {
            this.value = number;
        }

        public boolean isInt() {
            return value instanceof Integer;
        }

        public Number getValue() {
            return value;
        }

        public String getValueAsString() {
            return value.toString();
        }

        public String getValueType() {
            return value.getClass().getName();
        }
    }

    @Test
    public void testMaxIntegerResultOnDoublePatternShouldntCompile() {
        checkCompilationFailureOnMismatchingAccumulate("Integer", "max");
    }

    @Test
    public void testMinIntegerResultOnDoublePatternShouldntCompile() {
        checkCompilationFailureOnMismatchingAccumulate("Integer", "min");
    }

    @Test
    public void testMaxLongResultOnDoublePatternShouldntCompile() {
        checkCompilationFailureOnMismatchingAccumulate("Long", "max");
    }

    @Test
    public void testMinLongResultOnDoublePatternShouldntCompile() {
        checkCompilationFailureOnMismatchingAccumulate("Long", "min");
    }

    private void checkCompilationFailureOnMismatchingAccumulate(String type, String accFunc) {
        String drl =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule X when\n" +
                "  $max : Double() from accumulate ( $num : " + type + "(); " + accFunc + "($num) ) \n" +
                "then\n" +
                "  Double res = null;" +
                "  res = $max;\n" +
                "  System.out.println($max);\n" +
                "end";

        Results results = getCompilationResults(drl);
        assertThat(results.getMessages(Message.Level.ERROR).isEmpty()).isFalse();

        // line = 1 with STANDARD_FROM_DRL (RuleDescr)
        assertThat(results.getMessages().get(0).getLine()).isEqualTo(1);
    }


    @Test
    public void modify_factInScope_java() {
        // DROOLS-5242, DROOLS-7195
        String drl =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R1\n" +
                "when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "  modify($p) { $p.setName(\"Mark\") }\n" +
                "end";

        Results results = getCompilationResults(drl);
        assertThat(results.getMessages(Message.Level.ERROR).isEmpty()).isFalse();
    }

    @Test
    public void modify_factInScope_mvel() {
        // DROOLS-5242, DROOLS-7195
        String drl =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R1\n" +
                "dialect 'mvel'\n" +
                "when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "  modify($p) { $p.setName(\"Mark\") }\n" +
                "end";

        Results results = getCompilationResults(drl);
        assertThat(results.getMessages(Message.Level.ERROR).isEmpty()).isFalse();
    }

    @Test
    public void modify_factInScope_nestedPropertySetter_java() {
        // DROOLS-5242, DROOLS-7195
        String drl =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R1\n" +
                "when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "  modify($p) { $p.address.setCity(\"London\") }\n" +
                "end";

        Results results = getCompilationResults(drl);
        assertThat(results.getMessages(Message.Level.ERROR).isEmpty()).isFalse();
    }

    @Test
    public void modify_factInScope_nestedPropertySetter_mvel() {
        // DROOLS-5242, DROOLS-7195
        String drl =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R1\n" +
                "dialect 'mvel'\n" +
                "when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "  modify($p) { $p.address.setCity(\"London\") }\n" +
                "end";

        Results results = getCompilationResults(drl);
        assertThat(results.getMessages(Message.Level.ERROR).isEmpty()).isFalse();
    }

    @Test
    public void modify_factInScope_nestedPropertyAssign_java() {
        // DROOLS-5242, DROOLS-7195
        String drl =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R1\n" +
                "when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "  modify($p) { $p.address.city = \"London\" }\n" +
                "end";

        Results results = getCompilationResults(drl);
        assertThat(results.getMessages(Message.Level.ERROR).isEmpty()).isFalse();
    }

    @Test
    public void modify_factInScope_nestedPropertyAssign_mvel() {
        // DROOLS-5242, DROOLS-7195
        String drl =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R1\n" +
                "dialect 'mvel'\n" +
                "when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "  modify($p) { $p.address.city = \"London\" }\n" +
                "end";

        Results results = getCompilationResults(drl);
        assertThat(results.getMessages(Message.Level.ERROR).isEmpty()).isFalse();
    }

    @Test
    public void testVariableInsideBinding() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + NameLengthCount.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  $nlc : NameLengthCount() \n" +
                        "  Person ( $nameLength : $nlc.self.getNameLength(name))" +
                        "then\n" +
                        "end";

        Results results = createKieBuilder(str ).getResults();
        assertThat(results.getMessages(Message.Level.ERROR).stream().map(Message::getText))
                .contains("Variables can not be used inside bindings. Variable [$nlc] is being used in binding '$nlc.self.getNameLength(name)'");
    }

    @Test
    public void testVariableInsideBindingInParameter() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + NameLengthCount.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  $nlc : NameLengthCount() \n" +
                        "  Person ( $nameLength : identityBigDecimal($nlc.fortyTwo))" +
                        "then\n" +
                        "end";

        Results results = createKieBuilder(str ).getResults();
        assertThat(results.getMessages(Message.Level.ERROR).stream().map(Message::getText))
                .contains("Variables can not be used inside bindings. Variable [$nlc] is being used in binding 'identityBigDecimal($nlc.fortyTwo)'");
    }

    public static class NameLengthCount {

        public NameLengthCount getSelf() {
            return this;
        }

        public int getNameLength(String name) {
            return name.length();
        }

        public BigDecimal getFortyTwo() {
            return BigDecimal.valueOf(42);
        }
    }

    @Test
    public void testTypeSafe() {
        String str =
                "import " + Parent.class.getCanonicalName() + ";" +
                     "declare\n" +
                     "   Parent @typesafe(false)\n" +
                     "end\n" +
                     "rule R1\n" +
                     "when\n" +
                     "   $a : Parent( x == 1 )\n" +
                     "then\n" +
                     "end\n";

        Results results = createKieBuilder(str).getResults();
        if (testRunType.isExecutableModel()) {
            assertThat(results.getMessages(Message.Level.ERROR).get(0).getText().contains("@typesafe(false) is not supported in executable model"));
        } else {
            assertThat(results.getMessages(Message.Level.ERROR).isEmpty()).isTrue();
        }
    }

    public static class Parent {
    }
}
