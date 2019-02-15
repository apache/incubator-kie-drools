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
}
