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

import org.drools.modelcompiler.domain.Address;
import org.drools.modelcompiler.domain.Person;
import org.drools.ruleunit.DataSource;
import org.drools.ruleunit.RuleUnit;
import org.drools.ruleunit.RuleUnitExecutor;
import org.junit.Test;
import org.kie.api.runtime.KieContainer;

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
        RuleUnitExecutor executor = RuleUnitExecutor.newRuleUnitExecutor( kieContainer );

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
        RuleUnitExecutor executor = RuleUnitExecutor.newRuleUnitExecutor( kieContainer );

        DataSource<Person> persons = DataSource.create( new Person( "Mario", 42 ),
                                                        new Person( "Marilena", 44 ),
                                                        new Person( "Sofia", 4 ) );

        AdultUnit unit = new AdultUnit(persons);
        assertEquals(2, executor.run( unit ) );

        assertTrue( unit.getResults().containsAll( asList("Mario", "Marilena") ) );
    }

    @Test
    public void testAccumulateWithOOPath() {
        // DROOLS-5179

        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + AdultUnit.class.getCanonicalName() + "\n" +
                "rule Adult @Unit( AdultUnit.class ) when\n" +
                "  accumulate ( $p: /persons[age >= adultAge]; \n" +
                "                $sum : sum($p.getAge())  \n" +
                "              )                          \n" +
                "then\n" +
                "  results.add(\"\" + $sum);\n" +
                "end";

        KieContainer kieContainer = getKieContainer( null, str );
        RuleUnitExecutor executor = RuleUnitExecutor.newRuleUnitExecutor( kieContainer );

        DataSource<Person> persons = DataSource.create( new Person( "Mario", 42 ),
                                                        new Person( "Marilena", 44 ),
                                                        new Person( "Sofia", 4 ) );

        AdultUnit unit = new AdultUnit(persons);
        assertEquals(1, executor.run( unit ) );

        assertEquals( 1, unit.getResults().size() );
        assertEquals( "86", unit.getResults().get(0) );
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
        RuleUnitExecutor executor = RuleUnitExecutor.newRuleUnitExecutor( kieContainer );

        PositiveNegativeDTUnit ruleUnit = new PositiveNegativeDTUnit(47);
        executor.run(ruleUnit);
        assertEquals("positive", ruleUnit.getPositive_or_negative());

        ruleUnit = new PositiveNegativeDTUnit(-999);
        executor.run(ruleUnit);
        assertEquals("negative", ruleUnit.getPositive_or_negative());
    }

    @Test
    public void testOOPath2Chunks() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + AdultUnit.class.getCanonicalName() + "\n" +
                "rule Adult @Unit( AdultUnit.class ) when\n" +
                "    $a : /persons/addresses[city == \"Milano\"]\n" +
                "then\n" +
                "    results.add($a.getCity());\n" +
                "end\n";

        checkOopathBinding( str, "Milano" );
    }

    @Test
    public void testOOPath1ChunksBindOnThis() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + AdultUnit.class.getCanonicalName() + "\n" +
                "rule Adult @Unit( AdultUnit.class ) when\n" +
                "    /persons[$p : this, name == \"Mario\"]\n" +
                "then\n" +
                "    results.add($p.getName());\n" +
                "end\n";

        checkOopathBinding( str, "Mario" );
    }

    @Test
    public void testOOPath1ChunksBindOnField() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + AdultUnit.class.getCanonicalName() + "\n" +
                "rule Adult @Unit( AdultUnit.class ) when\n" +
                "    /persons[$name : name == \"Mario\"]\n" +
                "then\n" +
                "    results.add($name);\n" +
                "end\n";

        checkOopathBinding( str, "Mario" );
    }

    @Test
    public void testOOPath2ChunksBindOnThis() {
        // DROOLS-5207
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + AdultUnit.class.getCanonicalName() + "\n" +
                "rule Adult @Unit( AdultUnit.class ) when\n" +
                "    /persons/addresses[$a : this, city == \"Milano\"]\n" +
                "then\n" +
                "    results.add($a.getCity());\n" +
                "end\n";

        checkOopathBinding( str, "Milano" );
    }

    @Test
    public void testOOPath2ChunksBindOnPattern() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + AdultUnit.class.getCanonicalName() + "\n" +
                "rule Adult @Unit( AdultUnit.class ) when\n" +
                "    $a : /persons/addresses[city == \"Milano\"]\n" +
                "then\n" +
                "    results.add($a.getCity());\n" +
                "end\n";

        checkOopathBinding( str, "Milano" );
    }

    @Test
    public void testOOPath2ChunksBindOnField() {
        // DROOLS-5207
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + AdultUnit.class.getCanonicalName() + "\n" +
                "rule Adult @Unit( AdultUnit.class ) when\n" +
                "    /persons/addresses[$city : city == \"Milano\"]\n" +
                "then\n" +
                "    results.add($city);\n" +
                "end\n";

        checkOopathBinding( str, "Milano" );
    }

    private void checkOopathBinding( String drl, String expected ) {
        KieContainer kieContainer = getKieContainer( null, drl );
        RuleUnitExecutor executor = RuleUnitExecutor.newRuleUnitExecutor( kieContainer );

        Person mario = new Person( "Mario", 42 );
        mario.addAddress( new Address( "Milano" ) );

        DataSource<Person> persons = DataSource.create( mario );

        AdultUnit unit = new AdultUnit( persons );
        assertEquals( 1, executor.run( unit ) );

        assertEquals( 1, unit.getResults().size() );
        assertEquals( expected, unit.getResults().get( 0 ) );
    }

    @Test
    public void testOOPath1Binding() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + AdultUnit.class.getCanonicalName() + "\n" +
                "rule Adult @Unit( AdultUnit.class ) when\n" +
                "    $p : /persons;\n" +
                "    /$p/addresses[city == \"Milano\"]\n" +
                "then\n" +
                "    results.add($p.getName());\n" +
                "end\n";

        checkDRLoopath( str );
    }

    @Test
    public void testOOPath2Bindings() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + AdultUnit.class.getCanonicalName() + "\n" +
                "rule Adult @Unit( AdultUnit.class ) when\n" +
                "    $p : /persons\n" +
                "    $a : /$p/addresses[city == \"Milano\"]\n" +
                "then\n" +
                "    results.add($p.getName());\n" +
                "end\n";

        checkDRLoopath( str );
    }

    private void checkDRLoopath( String str ) {
        KieContainer kieContainer = getKieContainer( null, str );
        RuleUnitExecutor executor = RuleUnitExecutor.newRuleUnitExecutor( kieContainer );

        Person mario = new Person( "Mario", 42 );
        mario.addAddress( new Address( "Milano" ) );
        mario.addAddress( new Address( "Milano" ) );
        Person mark = new Person( "Mark", 40 );
        mark.addAddress( new Address( "London" ) );
        mark.addAddress( new Address( "Milano" ) );
        Person edson = new Person( "Edson", 37 );
        edson.addAddress( new Address( "Toronto" ) );

        DataSource<Person> persons = DataSource.create( mario, mark, edson );

        AdultUnit unit = new AdultUnit(persons);
        assertEquals( 3, executor.run( unit ) );

        assertEquals( 3, unit.getResults().size() );
        assertTrue( unit.getResults().containsAll( asList("Mario", "Mark") ) );
    }

    @Test
    public void testOOPathBooleanFunction() {
        String str =
                "import " + RuleUnitCompilerTest.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + AdultUnit.class.getCanonicalName() + "\n" +
                "rule Adult @Unit( AdultUnit.class ) when\n" +
                "    $p: /persons[RuleUnitCompilerTest.hasName(name, \"Mario\")]\n" +
                "then\n" +
                "    results.add($p.getName());\n" +
                "end\n";

        checkOopathBinding( str, "Mario" );
    }

    @Test
    public void testOOPathNegatedBooleanFunction() {
        // DROOLS-5213
        String str =
                "import " + RuleUnitCompilerTest.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + AdultUnit.class.getCanonicalName() + "\n" +
                "rule Adult @Unit( AdultUnit.class ) when\n" +
                "    $p: /persons[!RuleUnitCompilerTest.hasName(name, \"Pippo\")]\n" +
                "then\n" +
                "    results.add($p.getName());\n" +
                "end\n";

        checkOopathBinding( str, "Mario" );
    }

    public static boolean hasName(String actual, String expected) {
        return expected.equals( actual );
    }
}
