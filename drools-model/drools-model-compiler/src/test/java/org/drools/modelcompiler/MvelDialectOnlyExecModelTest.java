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

import org.drools.modelcompiler.domain.Person;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.kie.api.runtime.KieSession;

import static org.drools.modelcompiler.BaseModelTest.RUN_TYPE.FLOW_DSL;
import static org.drools.modelcompiler.BaseModelTest.RUN_TYPE.PATTERN_DSL;
import static org.junit.Assert.assertEquals;


public class MvelDialectOnlyExecModelTest extends BaseModelTest {

    public MvelDialectOnlyExecModelTest(RUN_TYPE testRunType ) {
        super( testRunType );
    }

    final static Object[] EXCLUDE_DRL = {
            FLOW_DSL,
            PATTERN_DSL,
    };

    @Parameterized.Parameters(name = "{0}")
    public static Object[] params() {
        return EXCLUDE_DRL;
    }

    // without B it doesn't work on MVEL - see https://issues.redhat.com/browse/DROOLS-5897
    @Test
    public void testCompoundOperator() throws Exception {
        // DROOLS-5894 // DROOLS-5901
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + BigDecimal.class.getCanonicalName() + "\n" +
                "dialect \"mvel\"\n" +
                "rule R\n" +
                "when\n" +
                "    $p : Person( age >= 26 )\n" +
                "then\n" +
                "    BigDecimal result = 0B;" +
                "    $p.money += 50000;\n" + // 50000
                "    $p.money -= 10000;\n" + // 40000
                "    $p.money /= 10;\n" + // 4000
                "    $p.money *= 10;\n" + // 40000
                "    $p.money += $p.money;\n" + // 80000
                "    $p.money /= $p.money;\n" + // 1
                "    $p.money *= $p.money;\n" + // 1
                "    $p.money -= $p.money;\n" + // 0
                "    BigDecimal anotherVar = 10B;" +
                "    $p.money += anotherVar;\n" + // 10
                "    $p.money /= anotherVar;\n" + // 1
                "    $p.money *= anotherVar;\n" + // 1
                "    $p.money -= anotherVar;\n" + // 0
                "    int intVar = 20;" +
                "    $p.money += intVar;\n" + // 20
                "    $p.money /= intVar;\n" + // 1
                "    $p.money *= intVar;\n" + // 1
                "    $p.money -= intVar;\n" + // 0
                "end";

        KieSession ksession = getKieSession(drl);

        Person john = new Person("John", 30);
        john.setMoney( new BigDecimal( 70000 ) );

        ksession.insert(john);
        assertEquals(1, ksession.fireAllRules());
        assertEquals(new BigDecimal( 0 ), john.getMoney());
    }

}
