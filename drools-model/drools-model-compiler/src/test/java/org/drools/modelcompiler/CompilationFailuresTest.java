/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler;

import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Test;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
        assertFalse(results.getMessages( Message.Level.ERROR).isEmpty());

        // line = -1 even with STANDARD_FROM_DRL (PredicateDescr)
        assertEquals(-1, results.getMessages().get(0).getLine());
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
        assertFalse(results.getMessages( Message.Level.ERROR).isEmpty());

        assertEquals(3, results.getMessages().get(0).getLine());
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
    public void testBadQueryArg() {
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "query queryWithParamWithoutType( tname , tage)\n" +
                "    person : Person(name == tname, age < tage )\n" +
                "end\n";

        Results results = getCompilationResults(drl);
        assertFalse(results.getMessages( Message.Level.ERROR).isEmpty());

        // line = -1 even with STANDARD_FROM_DRL (PredicateDescr)
        assertEquals(-1, results.getMessages().get(0).getLine());
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
        assertFalse(results.getMessages( Message.Level.ERROR).isEmpty());

        // line = 1 with STANDARD_FROM_DRL (RuleDescr)
        assertEquals(1, results.getMessages().get(0).getLine());
    }


    @Test
    public void testModifyOnFactInScope() {
        // DROOLS-5242
        String drl =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R1 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "  modify($p) { $p.setName(\"Mark\") }\n" +
                "end";

        Results results = getCompilationResults(drl);
        assertFalse(results.getMessages( Message.Level.ERROR).isEmpty());

        // RHS error : line = 1 with STANDARD_FROM_DRL (RuleDescr)
        assertEquals(1, results.getMessages().get(0).getLine());
    }
}
