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
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class GlobalTest extends BaseModelTest {

    public GlobalTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testGlobalInConsequence() {
        String str =
                "package org.mypkg;" +
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "global Result globalResult;" +
                "rule X when\n" +
                "  $p1 : Person(name == \"Mark\")\n" +
                "then\n" +
                " globalResult.setValue($p1.getName() + \" is \" + $p1.getAge());\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Result result = new Result();
        ksession.setGlobal("globalResult", result);

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        assertEquals( "Mark is 37", result.getValue() );
    }

    @Test
    public void testGlobalInConstraint() {
        String str =
                "package org.mypkg;" +
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "global java.lang.String nameG;" +
                "global Result resultG;" +
                "rule X when\n" +
                "  $p1 : Person(nameG == name)\n" +
                "then\n" +
                " resultG.setValue($p1.getName() + \" is \" + $p1.getAge());\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.setGlobal("nameG", "Mark");

        Result result = new Result();
        ksession.setGlobal("resultG", result);

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        assertEquals( "Mark is 37", result.getValue() );
    }
}
