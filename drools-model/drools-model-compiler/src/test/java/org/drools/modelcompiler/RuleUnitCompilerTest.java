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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.drools.modelcompiler.domain.Person;
import org.junit.Test;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;

import static java.util.Arrays.asList;

import static org.drools.core.util.ClassUtils.getCanonicalSimpleName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RuleUnitCompilerTest extends BaseModelTest {

    public RuleUnitCompilerTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    public static class AdultUnit implements RuleUnit {
        private List<String> results = new ArrayList<String>();
        private int adultAge = 18;
        private DataSource<Person> persons;

        public AdultUnit( ) { }

        public AdultUnit( DataSource<Person> persons ) {
            this.persons = persons;
        }

        public DataSource<Person> getPersons() {
            return persons;
        }

        public int getAdultAge() {
            return adultAge;
        }

        public List<String> getResults() {
            return results;
        }
    }

    @Test
    public void testRuleUnit() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + AdultUnit.class.getCanonicalName() + "\n" +
                "rule Adult @Unit( AdultUnit.class ) when\n" +
                "    Person(age >= 18, $name : name) from persons\n" +
                "then\n" +
                "    results.add($name);\n" +
                "end\n";

        KieContainer kieContainer = getKieContainer( null, str );
        RuleUnitExecutor executor = kieContainer.newRuleUnitExecutor();

        DataSource<Person> persons = DataSource.create( new Person( "Mario", 42 ),
                                                        new Person( "Marilena", 44 ),
                                                        new Person( "Sofia", 4 ) );

        AdultUnit unit = new AdultUnit(persons);
        assertEquals(2, executor.run( unit ) );

        assertTrue( unit.getResults().containsAll( asList("Mario", "Marilena") ) );
    }

    @Test
    public void testRuleUnitWithOOPath() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + AdultUnit.class.getCanonicalName() + "\n" +
                "rule Adult @Unit( AdultUnit.class ) when\n" +
                "    $p : /persons[age >= adultAge]\n" +
                "then\n" +
                "    results.add($p.getName());\n" +
                "end\n";

        KieContainer kieContainer = getKieContainer( null, str );
        RuleUnitExecutor executor = kieContainer.newRuleUnitExecutor();

        DataSource<Person> persons = DataSource.create( new Person( "Mario", 42 ),
                                                        new Person( "Marilena", 44 ),
                                                        new Person( "Sofia", 4 ) );

        AdultUnit unit = new AdultUnit(persons);
        assertEquals(2, executor.run( unit ) );

        assertTrue( unit.getResults().containsAll( asList("Mario", "Marilena") ) );
    }

    public static class PositiveNegativeDTUnit implements RuleUnit {

        private BigDecimal a_number;

        private DataSource<BigDecimal> input1 = DataSource.create();
        private DataSource<String> output1 = DataSource.create();

        private String positive_or_negative;

        public PositiveNegativeDTUnit(long val) {
            this.a_number = new BigDecimal(val);
        }

        public DataSource<BigDecimal> getInput1() {
            return input1;
        }

        public DataSource<String> getOutput1() {
            return output1;
        }

        public String getPositive_or_negative() {
            return positive_or_negative;
        }

        @Override
        public void onStart() {
            // input1: (simple assignment)
            input1.insert(a_number);
        }

        @Override
        public void onEnd() {
            // output1: (simple assignment)
            positive_or_negative = output1.iterator().next();
        }

        public String getResult() {
            return getPositive_or_negative();
        }
    }

    @Test
    public void testWith2Rules() {
        String str = "package " + this.getClass().getPackage().getName() + ";\n" +
                "unit " + getCanonicalSimpleName(PositiveNegativeDTUnit.class) + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "rule R1 \n" +
                "when\n" +
                "  BigDecimal( intValue() >= 0 ) from input1\n" +
                "then\n" +
                "  output1.insert(\"positive\");\n" +
                "end\n" +
                "rule R2 \n" +
                "when\n" +
                "  BigDecimal( intValue() < 0 ) from input1\n" +
                "then\n" +
                "  output1.insert(\"negative\");\n" +
                "end\n";

        KieContainer kieContainer = getKieContainer( null, str );
        RuleUnitExecutor executor = kieContainer.newRuleUnitExecutor();

        PositiveNegativeDTUnit ruleUnit = new PositiveNegativeDTUnit(47);
        executor.run(ruleUnit);
        assertEquals("positive", ruleUnit.getPositive_or_negative());

        ruleUnit = new PositiveNegativeDTUnit(-999);
        executor.run(ruleUnit);
        assertEquals("negative", ruleUnit.getPositive_or_negative());
    }
}
