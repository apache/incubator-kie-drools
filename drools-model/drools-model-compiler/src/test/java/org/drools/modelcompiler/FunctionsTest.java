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

import java.util.Arrays;
import java.util.List;

import org.drools.modelcompiler.domain.Person;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

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
        assertEquals( 1, rulesFired ); // only R1 should fire
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
        assertEquals( 1, rulesFired );
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
        assertEquals( 1, rulesFired );
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
        assertEquals( 1, rulesFired );
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
        assertEquals( 1, rulesFired );
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
}
