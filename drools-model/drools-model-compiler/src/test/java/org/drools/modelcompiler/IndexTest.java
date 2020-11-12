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

import org.drools.core.base.ClassObjectType;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.util.DateUtils;
import org.drools.core.util.index.IndexUtil;
import org.drools.core.util.index.IndexUtil.ConstraintType;
import org.drools.modelcompiler.domain.Person;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testAlphaIndexWithDateEqual() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  Person( birthDay == \"01-Oct-2000\" )\n" +
                     "then\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  Person( birthDay == \"01-Nov-2000\" )\n" +
                     "then\n" +
                     "end\n" +
                     "rule R3 when\n" +
                     "  Person( birthDay == \"01-Dec-2000\" )\n" +
                     "then\n" +
                     "end\n";

        KieSession ksession = getKieSession(str);

        assertConstraintType(ksession.getKieBase(), Person.class, "R1", IndexUtil.ConstraintType.EQUAL);
        assertConstraintType(ksession.getKieBase(), Person.class, "R2", IndexUtil.ConstraintType.EQUAL);
        assertConstraintType(ksession.getKieBase(), Person.class, "R3", IndexUtil.ConstraintType.EQUAL);

        Person person = new Person("John");
        person.setBirthDay(DateUtils.parseDate("01-Nov-2000"));
        ksession.insert(person);

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testAlphaIndexWithDateRelation() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  Person( birthDay > \"01-Oct-2000\" )\n" +
                     "then\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  Person( birthDay <= \"01-Nov-2000\" )\n" +
                     "then\n" +
                     "end\n" +
                     "rule R3 when\n" +
                     "  Person( birthDay >= \"01-Dec-2000\" )\n" +
                     "then\n" +
                     "end\n";

        KieSession ksession = getKieSession(str);

        assertConstraintType(ksession.getKieBase(), Person.class, "R1", IndexUtil.ConstraintType.GREATER_THAN);
        assertConstraintType(ksession.getKieBase(), Person.class, "R2", IndexUtil.ConstraintType.LESS_OR_EQUAL);
        assertConstraintType(ksession.getKieBase(), Person.class, "R3", IndexUtil.ConstraintType.GREATER_OR_EQUAL);

        Person person = new Person("John");
        person.setBirthDay(DateUtils.parseDate("01-Nov-2000"));
        ksession.insert(person);

        assertEquals(2, ksession.fireAllRules());
    }

    private void assertConstraintType(KieBase kbase, Class<?> factClass, String ruleName, ConstraintType expectedType) {
        boolean asserted = false;
        EntryPointNode epn = ((InternalKnowledgeBase) kbase).getRete().getEntryPointNodes().values().iterator().next();
        ObjectTypeNode otn = epn.getObjectTypeNodes().get(new ClassObjectType(factClass));
        ObjectSink[] sinks = otn.getObjectSinkPropagator().getSinks();
        for (ObjectSink objectSink : sinks) {
            AlphaNode alphaNode = (AlphaNode) objectSink;
            Rule rule = alphaNode.getAssociatedRules()[0]; // assume that one rule has one AlphaNode
            if (rule.getName().equals(ruleName)) {
                IndexableConstraint constraint = (IndexableConstraint) alphaNode.getConstraint();
                assertEquals(expectedType, constraint.getConstraintType());
                asserted = true;
            }
        }
        assertTrue(asserted);
    }
}
