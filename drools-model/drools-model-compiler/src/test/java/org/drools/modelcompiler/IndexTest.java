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

public class IndexTest extends BaseModelTest {

    public IndexTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testBetaIndexOnDeclaration() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule R1 when\n" +
                        "  String( $l : length )\n" +
                        "  $p : Person(age == $l)\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( "test" );
        ksession.insert( new Person("Sofia", 4) );

        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testBetaIndexWithAccessor() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule R1 when\n" +
                        "  $s : String()\n" +
                        "  $p : Person(age == $s.length)\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( "test" );
        ksession.insert( new Person("Sofia", 4) );

        assertEquals( 1, ksession.fireAllRules() );
    }

    public static class ObjectWrapper {
        private final Object object;

        public ObjectWrapper( Object object ) {
            this.object = object;
        }

        public Object getObject() {
            return object;
        }
    }

    public static class IntegerWrapper {
        private final Integer integer;

        public IntegerWrapper( Integer integer ) {
            this.integer = integer;
        }

        public Integer getInteger() {
            return integer;
        }
    }

    @Test
    public void testBetaIndexWithImplicitDowncast() {
        // DROOLS-5614
        String str =
                "import " + ObjectWrapper.class.getCanonicalName() + ";" +
                "import " + IntegerWrapper.class.getCanonicalName() + ";" +
                "rule R1 when\n" +
                "  ObjectWrapper( $o : object )\n" +
                "  IntegerWrapper( integer == $o )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new ObjectWrapper( 42 ) );
        ksession.insert( new IntegerWrapper( 42 ) );

        assertEquals( 1, ksession.fireAllRules() );
    }

}
