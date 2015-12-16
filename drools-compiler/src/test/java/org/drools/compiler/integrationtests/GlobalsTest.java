/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests;

import org.drools.core.impl.StatelessKnowledgeSessionImpl;
import org.kie.api.io.ResourceType;
import org.drools.core.base.MapGlobalResolver;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.StatelessKnowledgeSession;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class GlobalsTest {

    @Test
    public void testGlobalAccess() {

        String drl = "import org.drools.core.base.MapGlobalResolver;\n" +
                     "global java.lang.String myGlobal;\n" +
                     "global String unused; \n" ;

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession session1 = kbase.newStatefulKnowledgeSession();

        String sample = "default string";

        // Testing 1.
        System.out.println("Start testing 1.");
        session1.setGlobal("myGlobal", "Testing 1");
        session1.insert(sample);
        session1.fireAllRules();
        Map.Entry[] entries1 = ((MapGlobalResolver) session1.getGlobals()).getGlobals();
        assertEquals( 1, entries1.length );
        assertEquals( entries1[0].getValue(), "Testing 1" );
        assertEquals( 1, session1.getGlobals().getGlobalKeys().size() );
        assertTrue( session1.getGlobals().getGlobalKeys().contains("myGlobal") );
        session1.dispose();

        // Testing 2.
        System.out.println("Start testing 2.");
        StatelessKnowledgeSession session2 = session1.getKieBase().newStatelessKnowledgeSession();
        session2.setGlobal("myGlobal", "Testing 2");
        session2.execute(sample);
        Map.Entry[] entries2 = ((MapGlobalResolver) session2.getGlobals()).getGlobals();
        assertEquals(1, entries2.length);
        assertEquals( entries2[0].getValue(), "Testing 2" );
        assertEquals( 1, session2.getGlobals().getGlobalKeys().size() );
        assertTrue( session2.getGlobals().getGlobalKeys().contains("myGlobal") );

        // Testing 3.
        System.out.println("Start testing 3.");
        StatefulKnowledgeSession session3 = ((StatelessKnowledgeSessionImpl) session2).newWorkingMemory();
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
        StatefulKnowledgeSession session4 = ((StatelessKnowledgeSessionImpl) session2).newWorkingMemory();
        session4.setGlobal("myGlobal", "Testing 4");
        session4.insert(sample);
        session4.fireAllRules();
        Map.Entry[] entries4 = ((MapGlobalResolver) session4.getGlobals()).getGlobals();
        assertEquals(1, entries4.length);
        assertEquals( entries4[0].getValue(), "Testing 4" );
        assertEquals( 1, session4.getGlobals().getGlobalKeys().size() );
        assertTrue( session4.getGlobals().getGlobalKeys().contains("myGlobal") );

        session4.dispose();
    }
}
