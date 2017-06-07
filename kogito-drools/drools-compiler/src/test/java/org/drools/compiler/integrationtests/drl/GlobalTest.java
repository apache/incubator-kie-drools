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
import java.util.List;
import java.util.Map;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.core.base.MapGlobalResolver;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.StatelessKnowledgeSessionImpl;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.kie.internal.utils.KieHelper;

public class GlobalTest extends CommonTestMethodBase {

    @Test
    public void testReturnValueAndGlobal() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_ReturnValueAndGlobal.drl" ) );
        final KieSession ksession = createKnowledgeSession( kbase );

        final List matchlist = new ArrayList();
        ksession.setGlobal( "matchingList",
                matchlist );

        final List nonmatchlist = new ArrayList();
        ksession.setGlobal( "nonMatchingList",
                nonmatchlist );

        ksession.setGlobal( "cheeseType",
                "stilton" );

        final Cheese stilton1 = new Cheese( "stilton",
                5 );
        final Cheese stilton2 = new Cheese( "stilton",
                7 );
        final Cheese brie = new Cheese( "brie",
                4 );
        ksession.insert( stilton1 );
        ksession.insert( stilton2 );
        ksession.insert( brie );

        ksession.fireAllRules();

        assertEquals( 2,
                matchlist.size() );
        assertEquals( 1,
                nonmatchlist.size() );
    }

    @Test
    public void testGlobalAccess() {

        final String drl = "import org.drools.core.base.MapGlobalResolver;\n" +
                "global java.lang.String myGlobal;\n" +
                "global String unused; \n" ;

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        final InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        final KieSession session1 = kbase.newKieSession();

        final String sample = "default string";

        // Testing 1.
        System.out.println("Start testing 1.");
        session1.setGlobal("myGlobal", "Testing 1");
        session1.insert(sample);
        session1.fireAllRules();
        final Map.Entry[] entries1 = ((MapGlobalResolver) session1.getGlobals()).getGlobals();
        assertEquals( 1, entries1.length );
        assertEquals( entries1[0].getValue(), "Testing 1" );
        assertEquals( 1, session1.getGlobals().getGlobalKeys().size() );
        assertTrue( session1.getGlobals().getGlobalKeys().contains("myGlobal") );
        session1.dispose();

        // Testing 2.
        System.out.println("Start testing 2.");
        final StatelessKieSession session2 = session1.getKieBase().newStatelessKieSession();
        session2.setGlobal("myGlobal", "Testing 2");
        session2.execute(sample);
        final Map.Entry[] entries2 = ((MapGlobalResolver) session2.getGlobals()).getGlobals();
        assertEquals(1, entries2.length);
        assertEquals( entries2[0].getValue(), "Testing 2" );
        assertEquals( 1, session2.getGlobals().getGlobalKeys().size() );
        assertTrue( session2.getGlobals().getGlobalKeys().contains("myGlobal") );

        // Testing 3.
        System.out.println("Start testing 3.");
        final StatefulKnowledgeSession session3 = ((StatelessKnowledgeSessionImpl) session2).newWorkingMemory();
        session3.insert(sample);
        session3.fireAllRules();
        Map.Entry[] entries3 = ((MapGlobalResolver) session3.getGlobals()).getGlobals();
        assertEquals( 1, entries3.length );
        assertEquals( entries3[0].getValue(), "Testing 2" );
        assertEquals( 1, session3.getGlobals().getGlobalKeys().size() );
        assertTrue( session3.getGlobals().getGlobalKeys().contains("myGlobal") );


        session3.setGlobal("myGlobal", "Testing 3 Over");
        entries3 = ((MapGlobalResolver) session3.getGlobals()).getGlobals();
        assertEquals(1, entries3.length);
        assertEquals( entries3[0].getValue(), "Testing 3 Over" );
        assertEquals( 1, session3.getGlobals().getGlobalKeys().size() );
        assertTrue( session3.getGlobals().getGlobalKeys().contains("myGlobal") );

        session3.dispose();

        // Testing 4.
        System.out.println("Start testing 4.");
        final StatefulKnowledgeSession session4 = ((StatelessKnowledgeSessionImpl) session2).newWorkingMemory();
        session4.setGlobal("myGlobal", "Testing 4");
        session4.insert(sample);
        session4.fireAllRules();
        final Map.Entry[] entries4 = ((MapGlobalResolver) session4.getGlobals()).getGlobals();
        assertEquals(1, entries4.length);
        assertEquals( entries4[0].getValue(), "Testing 4" );
        assertEquals( 1, session4.getGlobals().getGlobalKeys().size() );
        assertTrue( session4.getGlobals().getGlobalKeys().contains("myGlobal") );

        session4.dispose();
    }

    @Test
    public void testEvalNullGlobal() {
        // RHBPMS-4649
        final String str =
                "import org.drools.compiler.Cheese\n" +
                        "global Boolean b\n" +
                        "rule R when\n" +
                        "  eval(b)\n" +
                        "then\n" +
                        "end\n";

        final KieSession ksession = new KieHelper().addContent( str, ResourceType.DRL ).build().newKieSession();

        ksession.setGlobal( "b", null );
        assertEquals( 0, ksession.fireAllRules() );
    }

}
