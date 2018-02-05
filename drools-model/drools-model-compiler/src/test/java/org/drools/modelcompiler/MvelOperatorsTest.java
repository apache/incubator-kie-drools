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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.modelcompiler.domain.Person;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class MvelOperatorsTest extends BaseModelTest {

    public MvelOperatorsTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testIn() {
        String str =
            "rule R when\n" +
            "    String(this in (\"a\", \"b\"))" +
            "then\n" +
            "end ";

        KieSession ksession = getKieSession(str);

        ksession.insert( "b" );
        assertEquals(1, ksession.fireAllRules());
    }

    @Test @Ignore
    public void testStr() {
        String str =
            "rule R when\n" +
            "    String(this str[startsWith] \"M\")" +
            "then\n" +
            "end ";

        KieSession ksession = getKieSession(str);

        ksession.insert( "Mario" );
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testBinding() {
        String str =
            "import " + Person.class.getCanonicalName() + "\n" +
            "global java.util.List list\n" +
            "rule R when\n" +
            "    Person( $name : name in (\"Mario\", \"Mark\"))" +
            "then\n" +
            "    list.add($name);" +
            "end ";

        KieSession ksession = getKieSession(str);

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        Person person1 = new Person("Mario");
        ksession.insert(person1);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals("Mario", list.get(0));
    }

    @Test
    public void testMatches() {
        String str =
                "rule R when\n" +
                "    String(this matches \"\\\\w\")" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(str);

        ksession.insert( "b" );
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testExcludes() {
        String str =
                "import java.util.List\n" +
                "rule R when\n" +
                "    List(this excludes \"test\")" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(str);
        ksession.insert( Arrays.asList("ciao", "test") );
        assertEquals(0, ksession.fireAllRules());
        ksession.insert( Arrays.asList("hello", "world") );
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testNotContains() {
        String str =
                "import java.util.List\n" +
                "rule R when\n" +
                "    List(this not contains \"test\")" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(str);
        ksession.insert( Arrays.asList("ciao", "test") );
        assertEquals(0, ksession.fireAllRules());
        ksession.insert( Arrays.asList("hello", "world") );
        assertEquals(1, ksession.fireAllRules());
    }

}
