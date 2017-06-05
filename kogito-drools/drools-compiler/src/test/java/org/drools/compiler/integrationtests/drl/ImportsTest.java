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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.drools.compiler.Cheese;
import org.drools.compiler.Cheesery;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.FirstClass;
import org.drools.compiler.Person;
import org.drools.compiler.SecondClass;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportsTest extends CommonTestMethodBase {

    private static Logger logger = LoggerFactory.getLogger(ImportsTest.class);

    @Test
    public void testImportFunctions() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_ImportFunctions.drl"));
        KieSession session = createKnowledgeSession(kbase);

        final Cheese cheese = new Cheese("stilton",
                15);
        session.insert(cheese);
        List list = new ArrayList();
        session.setGlobal("list", list);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);
        final int fired = session.fireAllRules();

        list = (List) session.getGlobal("list");

        assertEquals(4, fired);
        assertEquals(4, list.size());

        assertEquals("rule1", list.get(0));
        assertEquals("rule2", list.get(1));
        assertEquals("rule3", list.get(2));
        assertEquals("rule4", list.get(3));
    }

    @Test()
    public void testImport() throws Exception {
        // Same package as this test
        String rule = "";
        rule += "package org.drools.compiler.integrationtests;\n";
        rule += "import java.lang.Math;\n";
        rule += "rule \"Test Rule\"\n";
        rule += "  dialect \"mvel\"\n";
        rule += "  when\n";
        rule += "  then\n";
        // Can't handle the TestFact.TEST
        rule += "    new TestFact(TestFact.TEST);\n";
        rule += "end";

        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(rule));
        final KieSession ksession = createKnowledgeSession(kbase);
        ksession.fireAllRules();
    }

    @Test
    public void testImportColision() throws Exception {
        final Collection<KnowledgePackage> kpkgs1 = loadKnowledgePackages("nested1.drl");
        final Collection<KnowledgePackage> kpkgs2 = loadKnowledgePackages("nested2.drl");
        final KnowledgeBase kbase = loadKnowledgeBase();
        kbase.addKnowledgePackages(kpkgs1);
        kbase.addKnowledgePackages(kpkgs2);

        final KieSession ksession = createKnowledgeSession(kbase);

        SerializationHelper.serializeObject(kbase);

        ksession.insert(new FirstClass());
        ksession.insert(new SecondClass());
        ksession.insert(new FirstClass.AlternativeKey());
        ksession.insert(new SecondClass.AlternativeKey());

        ksession.fireAllRules();
    }

    @Test
    public void testImportConflict() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_ImportConflict.drl"));
        createKnowledgeSession(kbase);
    }

    @Test
    public void testMissingImport() throws Exception {
        String str = "";
        str += "package org.drools.compiler \n";
        str += "import " + Person.class.getName() + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "when \n";
        str += "    $i : Cheese() \n";
        str += "         MissingClass( fieldName == $i ) \n";
        str += "then \n";
        str += "    list.add( $i ); \n";
        str += "end \n";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()),
                ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            logger.warn(kbuilder.getErrors().toString());
        }
        assertTrue(kbuilder.hasErrors());
    }

    @Test
    public void testMissingImports() {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_missing_import.drl",
                getClass() ),
                ResourceType.DRL );
        assertTrue( kbuilder.hasErrors() );
    }

    @Test
    public void testPackageImportWithMvelDialect() throws Exception {
        // JBRULES-2244
        final String str = "package org.drools.compiler.test;\n" +
                "import org.drools.compiler.*\n" +
                "dialect \"mvel\"\n" +
                "rule R1 no-loop when\n" +
                "   $p : Person( )" +
                "   $c : Cheese( )" +
                "then\n" +
                "   modify($p) { setCheese($c) };\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString( str );
        final KieSession ksession = kbase.newKieSession();

        final Person p = new Person( "Mario", 38 );
        ksession.insert( p );
        final Cheese c = new Cheese( "Gorgonzola" );
        ksession.insert( c );

        assertEquals( 1, ksession.fireAllRules() );
        assertSame( c, p.getCheese() );
    }

    @Test
    public void testImportStaticClass() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_StaticField.drl"));
        KieSession session = createKnowledgeSession(kbase);

        // will test serialisation of int and typesafe enums tests
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);

        final List list = new ArrayList();
        session.setGlobal("list", list);

        final Cheesery cheesery1 = new Cheesery();
        cheesery1.setStatus(Cheesery.SELLING_CHEESE);
        cheesery1.setMaturity(Cheesery.Maturity.OLD);
        session.insert(cheesery1);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);

        final Cheesery cheesery2 = new Cheesery();
        cheesery2.setStatus(Cheesery.MAKING_CHEESE);
        cheesery2.setMaturity(Cheesery.Maturity.YOUNG);
        session.insert(cheesery2);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);

        session.fireAllRules();

        assertEquals(2, list.size());

        assertEquals(cheesery1, list.get(0));
        assertEquals(cheesery2, list.get(1));
    }
}
