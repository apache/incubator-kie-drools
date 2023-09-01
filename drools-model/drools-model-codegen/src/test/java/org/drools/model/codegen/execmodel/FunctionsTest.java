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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.domain.Result;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class FunctionsTest extends BaseModelTest {

    public FunctionsTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testFunctionWithEquals() {
        // DROOLS-3653
        String str = "package com.sample\n" +
                "import " + Person.class.getName() + ";\n" +
                "function int myFunction(String expression, int value) {\n" +
                "  if (expression.equals(\"param == 10\") && value == 10) {\n" +
                "    return 1;\n" +
                "  }\n" +
                "  return 0;\n" +
                "}\n" +
                "rule R1\n" +
                "    when\n" +
                "        $p: Person(myFunction(\"param == 10\", age) == 1)\n" +
                "    then\n" +
                "end\n" +
                "\n" +
                "rule R2\n" +
                "    when\n" +
                "        $p: Person(myFunction(\"param == 20\", age) == 1)\n" +
                "    then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert(new Person("John", 10));
        int rulesFired = ksession.fireAllRules();
        assertThat(rulesFired).isEqualTo(1); // only R1 should fire
    }

    @Test
    public void testConstraintCallingStaticFunctionInsideEnum() {
        String str =
                "import " + Person.class.getName() + ";\n" +
                "import " + FunctionEnum.class.getCanonicalName() + ";\n" +
                "rule R1\n" +
                "    when\n" +
                "        $p: Person(FunctionEnum.constantEnumValue(parentP.name) == FunctionEnum.YES)\n" +
                "    then\n" +
                "end\n";

        KieSession ksession = getKieSession(str);


        Person john = new Person("John", 10);
        Person johnFather = new Person("father", 80);
        john.setParentP(johnFather);

        ksession.insert(john);
        int rulesFired = ksession.fireAllRules();
        assertThat(rulesFired).isEqualTo(1);
    }

    @Test
    public void testConstraintCallingImportedStaticFunction() {
        String str =
                "import " + Person.class.getName() + ";\n" +
                "import " + FunctionEnum.class.getCanonicalName() + ";\n" +
                "import static " + FunctionEnum.class.getCanonicalName() + ".constantEnumValue;\n" +
                "rule R1\n" +
                "    when\n" +
                "        $p: Person(constantEnumValue(parentP.name) == FunctionEnum.YES)\n" +
                "    then\n" +
                "end\n";

        KieSession ksession = getKieSession(str);


        Person john = new Person("John", 10);
        Person johnFather = new Person("father", 80);
        john.setParentP(johnFather);

        ksession.insert(john);
        int rulesFired = ksession.fireAllRules();
        assertThat(rulesFired).isEqualTo(1);
    }

    public enum FunctionEnum {
        YES, NO;

        public static FunctionEnum constantEnumValue(String input) {
            return FunctionEnum.YES;
        }
    }


    @Test
    public void testStaticMethodCall1() {
        // DROOLS-5214
        String str =
                "package com.sample\n" +
                "import " + Arrays.class.getCanonicalName() + ";\n" +
                "import " + Pojo.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    Pojo(Arrays.asList(1,2,3).containsAll(intList))\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Pojo( Arrays.asList(1,3) ) );
        int rulesFired = ksession.fireAllRules();
        assertThat(rulesFired).isEqualTo(1);
    }

    @Test
    public void testStaticMethodCall2() {
        // DROOLS-5214
        String str =
                "package com.sample\n" +
                "import " + Pojo.class.getCanonicalName() + ";\n" +
                "import " + Arrays.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    Pojo(intList.containsAll(Arrays.asList(1,3)))\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Pojo( Arrays.asList(1,2,3) ) );
        int rulesFired = ksession.fireAllRules();
        assertThat(rulesFired).isEqualTo(1);
    }

    @Test
    public void testFQNStaticMethodCall1() {
        // DROOLS-5214
        String str =
                "package com.sample\n" +
                "import " + Pojo.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    Pojo(java.util.Arrays.asList(1,2,3).containsAll(intList))\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Pojo( Arrays.asList(1,3) ) );
        int rulesFired = ksession.fireAllRules();
        assertThat(rulesFired).isEqualTo(1);
    }

    @Test
    public void testFQNStaticMethodCall2() {
        // DROOLS-5214
        String str =
                "package com.sample\n" +
                "import " + Pojo.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    Pojo(intList.containsAll(java.util.Arrays.asList(1,3)))\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Pojo( Arrays.asList(1,2,3) ) );
        int rulesFired = ksession.fireAllRules();
        assertThat(rulesFired).isEqualTo(1);
    }

    public static class Pojo {
        private final List<Integer> intList;

        public Pojo( List<Integer> intList ) {
            this.intList = intList;
        }

        public List<Integer> getIntList() {
            return intList;
        }
    }

    @Test
    public void testInvokeFunctionWithDroolsKeyword() {
        // DROOLS-5215
        String str =
                "package com.sample\n" +
                "    function printRuleName(String ruleName) {\n" +
                "      System.out.println(ruleName);\n" +
                "    }\n" +
                "    \n" +
                "    rule \"drools keyword in method call\"\n" +
                "    when\n" +
                "    then\n" +
                "        printRuleName(drools.getRule().getName());\n" +
                "    end";

        KieSession ksession = getKieSession( str );

        int rulesFired = ksession.fireAllRules();
        assertThat(rulesFired).isEqualTo(1);
    }

    @Test
    public void testBindingFieldsIndexedWithSquareBrackets() {
        // DROOLS-5216
        String str =
                "package com.sample\n" +
                "import " + Pojo.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule \"binding field indexed with square brackets\" when " +
                "    Pojo($firstItem : intList[0])\n" +
                "    Pojo(intList[this.intList.size()-2] == $firstItem)\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Pojo( Arrays.asList(1,3) ) );
        int rulesFired = ksession.fireAllRules();
        assertThat(rulesFired).isEqualTo(1);
    }

    public static String constantValue(String input) {
        return "whatever";
    }

    @Test
    public void testExternalFunctionJoin() {
        // DROOLS-5288
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "\n" +
                "import function " + FunctionsTest.class.getCanonicalName() + ".constantValue;\n" +
                "\n" +
                "rule rule1\n" +
                "when\n" +
                "    $p1 : Person() \n" +
                "    $p2: Person(name == constantValue($p1.name))\n" +
                "then\n" +
                "  insert(new Result($p2.getName()));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert(new Person("Luca"));
        ksession.insert(new Person("whatever"));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class );
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.iterator().next().getValue()).isEqualTo("whatever");
    }
}
