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
import java.util.List;

import org.drools.modelcompiler.domain.Person;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class TypeCoercionTest extends BaseModelTest {

    public TypeCoercionTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testEqualCoercion() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "    Person( $name : name == 40 )" +
                "then\n" +
                "    list.add($name);" +
                "end ";

        KieSession ksession = getKieSession(str);

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        Person person1 = new Person("40");
        ksession.insert(person1);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals("40", list.get(0));
    }

    @Test
    public void testComparisonCoercion() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "    Person( $name : name < \"50\" )" +
                "then\n" +
                "    list.add($name);" +
                "end ";

        KieSession ksession = getKieSession(str);

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        Person person1 = new Person("40");
        ksession.insert(person1);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals("40", list.get(0));
    }

    @Test
    public void testComparisonCoercion2() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "    Person( $name : name, age < \"50\" )" +
                "then\n" +
                "    list.add($name);" +
                "end ";

        KieSession ksession = getKieSession(str);

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        Person person1 = new Person("Mario", 40);
        ksession.insert(person1);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals("Mario", list.get(0));
    }
}
