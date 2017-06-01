/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests.drl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.FactA;
import org.drools.compiler.FactB;
import org.drools.compiler.FactC;
import org.drools.compiler.Person;
import org.drools.compiler.Sensor;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.core.common.InternalFactHandle;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.RemoveIdentitiesOption;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBaseFactory;

public class PatternTest extends CommonTestMethodBase {

    @Test
    public void testDeclaringAndUsingBindsInSamePattern() throws IOException, ClassNotFoundException {
        final KieBaseConfiguration kbc = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbc.setOption(RemoveIdentitiesOption.YES);
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase(kbc, "test_DeclaringAndUsingBindsInSamePattern.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List sensors = new ArrayList();

        ksession.setGlobal("sensors", sensors);

        final Sensor sensor1 = new Sensor(100, 150);
        ksession.insert(sensor1);
        ksession.fireAllRules();
        assertEquals(0, sensors.size());

        final Sensor sensor2 = new Sensor(200, 150);
        ksession.insert(sensor2);
        ksession.fireAllRules();
        assertEquals(3, sensors.size());
    }

    @Test
    public void testEmptyPattern() throws Exception {
        final KieBase kbase = loadKnowledgeBase("test_EmptyPattern.drl");
        KieSession session = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        session.setGlobal("list", list);

        final Cheese stilton = new Cheese("stilton", 5);
        session.insert(stilton);

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);
        session.fireAllRules();

        assertEquals(5, ((List) session.getGlobal("list")).get(0));
    }

    @Test
    public void testPatternMatchingOnThis() throws Exception {
        final String rule = "package org.drools.compiler\n" +
                "rule R1 when\n" +
                "    $i1: Integer()\n" +
                "    $i2: Integer( this > $i1 )\n" +
                "then\n" +
                "   System.out.println( $i2 + \" > \" + $i1 );\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(rule);
        final KieSession ksession = kbase.newKieSession();

        ksession.insert(1);
        ksession.insert(2);

        final int rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }

    @Test
    public void testPatternOffset() throws Exception {
        // JBRULES-3427
        final String str = "package org.drools.compiler.test; \n" +
                "declare A\n" +
                "end\n" +
                "declare B\n" +
                "   field : int\n" +
                "end\n" +
                "declare C\n" +
                "   field : int\n" +
                "end\n" +
                "rule R when\n" +
                "( " +
                "   A( ) or ( A( ) and B( ) ) " +
                ") and (\n" +
                "   A( ) or ( B( $bField : field ) and C( field != $bField ) )\n" +
                ")\n" +
                "then\n" +
                "    System.out.println(\"rule fired\");\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString( str );
        final KieSession ksession = kbase.newKieSession();

        final FactType typeA = kbase.getFactType( "org.drools.compiler.test", "A" );
        final FactType typeB = kbase.getFactType( "org.drools.compiler.test", "B" );
        final FactType typeC = kbase.getFactType( "org.drools.compiler.test", "C" );

        final Object a = typeA.newInstance();
        ksession.insert( a );

        final Object b = typeB.newInstance();
        typeB.set( b, "field", 1 );
        ksession.insert( b );

        final Object c = typeC.newInstance();
        typeC.set( c, "field", 1 );
        ksession.insert( c );

        ksession.fireAllRules();
    }

    @Test
    public void testPatternOnClass() throws Exception {
        final String rule = "import org.drools.core.reteoo.InitialFactImpl\n" +
                "import org.drools.compiler.FactB\n" +
                "rule \"Clear\" when\n" +
                "   $f: Object(class != FactB.class)\n" +
                "then\n" +
                "   if( ! ($f instanceof InitialFactImpl) ){\n" +
                "     delete( $f );\n" +
                "   }\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(rule);
        final KieSession ksession = kbase.newKieSession();

        ksession.insert(new FactA());
        ksession.insert(new FactA());
        ksession.insert(new FactB());
        ksession.insert(new FactB());
        ksession.insert(new FactC());
        ksession.insert(new FactC());
        ksession.fireAllRules();

        for (final FactHandle fact : ksession.getFactHandles()) {
            final InternalFactHandle internalFact = (InternalFactHandle) fact;
            assertTrue(internalFact.getObject() instanceof FactB);
        }
    }

    @Test
    public void testPredicateAsFirstPattern() throws Exception {
        final KieBase kbase = loadKnowledgeBase("predicate_as_first_pattern.drl");
        final KieSession ksession = kbase.newKieSession();

        final Cheese mussarela = new Cheese("Mussarela", 35);
        ksession.insert(mussarela);
        final Cheese provolone = new Cheese("Provolone", 20);
        ksession.insert(provolone);

        ksession.fireAllRules();

        assertEquals("The rule is being incorrectly fired", 35, mussarela.getPrice());
        assertEquals("Rule is incorrectly being fired", 20, provolone.getPrice());
    }

    @Test
    public void testConstantLeft() {
        // JBRULES-3627
        final String str = "import org.drools.compiler.*;\n" +
                "rule R1 when\n" +
                "   $p : Person( \"Mark\" == name )\n" +
                "then\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person(null));
        ksession.insert(new Person("Mark"));

        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }
}
