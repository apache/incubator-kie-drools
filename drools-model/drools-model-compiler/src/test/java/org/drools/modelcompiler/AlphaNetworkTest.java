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

public class AlphaNetworkTest extends BaseModelTest {

    public AlphaNetworkTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testAlpha() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule A when\n" +
                "  $p: Person ( age < 18 )\n" +
                "then\n" +
                "  System.out.println(\"A\");\n" +
                "end\n" +
                "\n" +
                "rule B when\n" +
                "  $p: Person ( age >= 18 && < 60, name.startsWith(\"M\") )\n" +
                "then\n" +
                "  System.out.println(\"B\");\n" +
                "end\n" +
                "\n" +
                "rule C when\n" +
                "  $p: Person ( age >= 18 && < 60, !name.startsWith(\"M\") )\n" +
                "then\n" +
                "  System.out.println(\"C\");\n" +
                "end\n" +
                "\n" +
                "rule D when\n" +
                "  $p: Person ( age >= 60 )\n" +
                "then\n" +
                "  System.out.println(\"D\");\n" +
                "end\n" +
                "\n";

        KieSession ksession = getKieSession( str );

        ReteDumper.dumpRete( ksession );

        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();
    }

    @Test
    public void testAlphaIndexed() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule A when\n" +
                "  $p: Person ( age == 10 )\n" +
                "then\n" +
                "  System.out.println(\"A\");\n" +
                "end\n" +
                "\n" +
                "rule B when\n" +
                "  $p: Person ( age == 20 )\n" +
                "then\n" +
                "  System.out.println(\"B\");\n" +
                "end\n" +
                "\n" +
                "rule C when\n" +
                "  $p: Person ( age == 30 )\n" +
                "then\n" +
                "  System.out.println(\"C\");\n" +
                "end\n" +
                "\n" +
                "rule D when\n" +
                "  $p: Person ( age == 40 )\n" +
                "then\n" +
                "  System.out.println(\"D\");\n" +
                "end\n" +
                "\n";

        KieSession ksession = getKieSession( str );

        ReteDumper.dumpRete( ksession );

        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();
    }
}
