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

package org.drools.compiler.integrationtests;

import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertEquals;

// DROOLS-4295
public class NullCheckOnExistentialNodeTest {

    public static class A {
        private B b = new B("xxx");

        public B getB() {
            return b;
        }

        public void setB( B b ) {
            this.b = b;
        }
    }

    public static class B {
        private final String value;

        public B(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private void check( String drl, int fire1, int fire2 ) {
        KieSession kieSession = new KieHelper().addContent( drl, ResourceType.DRL ).build().newKieSession();

        A a1 = new A();
        A a2 = new A();

        FactHandle fhA1 = kieSession.insert( a1 );
        FactHandle fhA2 = kieSession.insert( a2 );
        kieSession.insert( "xxx" );

        assertEquals( fire1, kieSession.fireAllRules() );

        a1.setB( null );
        kieSession.update( fhA1, a1 );

        a2.setB( new B( null ) );
        kieSession.update( fhA2, a2 );

        assertEquals( fire2, kieSession.fireAllRules() );
    }

    @Test
    public void testNot() {
        String drl =
                "import " + A.class.getCanonicalName() + ";\n" +
                "import " + B.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "    $s : String()\n" +
                "    not A(b != null,\n" +
                "          b.value != null,\n" +
                "          $s == b.value)\n" +
                "  then\n" +
                "end";

        check( drl, 0, 1 );
    }

    @Test
    public void testExists() {
        String drl =
                "import " + A.class.getCanonicalName() + ";\n" +
                "import " + B.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "    $s : String()\n" +
                "    exists A(b != null,\n" +
                "             b.value != null,\n" +
                "             $s == b.value)\n" +
                "  then\n" +
                "end";

        check( drl, 1, 0 );
    }

    @Test
    public void testNotIndexable() {
        String drl =
                "import " + NullCheckOnExistentialNodeTest.class.getCanonicalName() + ";\n" +
                "import " + A.class.getCanonicalName() + ";\n" +
                "import " + B.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "    $s : String()\n" +
                "    not A(b != null,\n" +
                "          b.value != null,\n" +
                "          NullCheckOnExistentialNodeTest.myPredicate(b.value, $s))\n" +
                "  then\n" +
                "end";

        check( drl, 0, 1 );
    }

    public static boolean myPredicate(String s1, String s2) {
        return s1 != null && s1.equals( s2 );
    }
}
