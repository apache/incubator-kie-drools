/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import org.drools.core.impl.KnowledgeBaseImpl;
import org.junit.Test;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.io.Resource;
import org.drools.core.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Before;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import java.util.Collection;

import static org.junit.Assert.assertTrue;

public class DynamicEvalTest {
    KieBase kbase;
    KieSession session;
    SessionPseudoClock clock;
    Collection<? extends Object> effects;
    KnowledgeBuilder kbuilder;
    KieBaseConfiguration baseConfig;
    KieSessionConfiguration sessionConfig;

    @Before
    public void setUp() throws Exception {

        baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        // use stream mode to enable proper event processing (see Drools Fusion 5.5.0 Doc "Event Processing Modes")
        baseConfig.setOption( EventProcessingOption.STREAM );
        kbase = KnowledgeBaseFactory.newKnowledgeBase(baseConfig);

        // config
        sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        // use a pseudo clock, which starts at 0 and can be advanced manually
        sessionConfig.setOption( ClockTypeOption.get("pseudo") );

        // create and return session
        session = kbase.newKieSession(sessionConfig, null);
        clock = session.getSessionClock();

    }

    public void loadPackages( Resource res, ResourceType type ) {
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( res, type );

        KnowledgeBuilderErrors errors = kbuilder.getErrors();

        if (errors.size() > 0) {
            for (KnowledgeBuilderError error : errors) {
                System.err.println( error );
            }
            throw new IllegalArgumentException( "Could not parse knowledge." );
        }

    }

    @After
    public void tearDown() {
        if (session != null) {
            session.dispose();
        }
        kbase = null;
        effects = null;
        clock = null;
        kbuilder = null;
        baseConfig = null;
        sessionConfig = null;
    }

    @Test
    public void testDynamicAdd() {
        String test =
                "\nrule id3" +
                "\nwhen" +
                "\neval(0 < 1)" + // this eval works
                "\nthen" +
                "\ninsertLogical( \"done\" );" +
                "\nend";

        loadPackages( ResourceFactory.newByteArrayResource( test.getBytes() ), ResourceType.DRL );
        ((KnowledgeBaseImpl)session.getKieBase()).addKnowledgePackages(kbuilder.getKnowledgePackages());
        session.addEventListener( new DebugRuleRuntimeEventListener( ) );

        int fired = session.fireAllRules(); // 1
        System.out.println(fired);
        effects = session.getObjects();
        assertTrue("fired", effects.contains("done"));

        // so the above works, let's try it again
        String test2 =
                "\nrule id4" +
                "\nwhen" +
                "\neval(0 == 0 )" + // this eval doesn't
                "\nthen" +
                "\ninsertLogical( \"done2\" );" +
                "\nend";

        loadPackages(ResourceFactory.newByteArrayResource(test2.getBytes()), ResourceType.DRL);
        ((KnowledgeBaseImpl)session.getKieBase()).addKnowledgePackages(kbuilder.getKnowledgePackages());


        fired = session.fireAllRules(); // 0
        System.out.println(fired);
        effects = session.getObjects();
        assertTrue("fired", effects.contains("done2")); // fails
    }

    @Test
    public void testDynamicAdd2() {
        String test =
                "rule id3\n" +
                "when\n" +
                "eval(0 == 0)\n" +
                "String( this == \"go\" )\n" + // this eval works
                "then\n" +
                "insertLogical( \"done\" );\n" +
                "end\n" +
                "rule id5\n" +
                "when\n" +
                "eval(0 == 0)\n" +
                "Integer( this == 7 )\n" + // this eval works
                "then\n" +
                "insertLogical( \"done3\" );\n" +
                "end\n";


        loadPackages( ResourceFactory.newByteArrayResource( test.getBytes() ), ResourceType.DRL );
        ((KnowledgeBaseImpl)session.getKieBase()).addKnowledgePackages(kbuilder.getKnowledgePackages());
        session.addEventListener( new DebugRuleRuntimeEventListener( ) );

        session.insert( "go" );
        session.insert( 5 );
        session.insert( 7 );

        int fired = session.fireAllRules(); // 1
        System.out.println(fired);
        effects = session.getObjects();
        assertTrue("fired", effects.contains("done"));

        // so the above works, let's try it again
        String test2 =
                "\nrule id4" +
                "\nwhen" +
                "\neval(0 == 0 )" + // this eval doesn't
                "\nInteger( this == 5 )" +
                "\nthen" +
                "\ninsertLogical( \"done2\" );" +
                "\nend";

        loadPackages(ResourceFactory.newByteArrayResource(test2.getBytes()), ResourceType.DRL);
        ((KnowledgeBaseImpl)session.getKieBase()).addKnowledgePackages(kbuilder.getKnowledgePackages());


        fired = session.fireAllRules(); // 0
        System.out.println(fired);
        effects = session.getObjects();
        assertTrue("fired", effects.contains("done2")); // fails

        for ( Object o : session.getObjects() ) {
            System.out.println( o );
        }
    }
}
