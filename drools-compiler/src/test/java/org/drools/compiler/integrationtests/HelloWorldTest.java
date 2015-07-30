/*
 * Copyright 2015 JBoss Inc
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

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Message;
import org.drools.core.base.mvel.MVELDebugHandler;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.logger.KnowledgeRuntimeLogger;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;
import org.mvel2.MVELRuntime;
import org.mvel2.debug.Debugger;
import org.mvel2.debug.Frame;

/**
 * This is a sample class to launch a rule.
 */
public class HelloWorldTest extends CommonTestMethodBase {

    @Test
    public void testHelloWorld() throws Exception {
        // load up the knowledge base
        KnowledgeBase kbase = readKnowledgeBase();
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        File testTmpDir = new File("target/test-tmp/");
        testTmpDir.mkdirs();
        KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger( ksession,
                 "target/test-tmp/testHelloWorld" );
        ksession.getAgendaEventListeners().size();
        // go !
        Message message = new Message();
        message.setMessage("Hello World");
        message.setStatus(Message.HELLO);
        ksession.insert(message);
        ksession.fireAllRules();
        logger.close();
    }

    @Test
    public void testHelloWorldDebug() throws Exception {
        final Set<String> knownVariables = new HashSet<String>();
        MVELRuntime.resetDebugger();
        MVELDebugHandler.setDebugMode(true);
        MVELRuntime.setThreadDebugger(new Debugger() {
            public int onBreak(Frame frame) {
                System.out.println("onBreak");
                for (String var: frame.getFactory().getKnownVariables()) {
                    knownVariables.add(var);
                }
                return 0;
            }
        });
        String source = "org.drools.integrationtests.Rule_Hello_World";
        MVELRuntime.registerBreakpoint(source, 1);
        // load up the knowledge base
        KnowledgeBase kbase = readKnowledgeBase();
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        File testTmpDir = new File("target/test-tmp/");
        testTmpDir.mkdirs();
        KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger( ksession,
                 "target/test-tmp/testHelloWorldDebug" );
        // go !
        Message message = new Message();
        message.setMessage("Hello World");
        message.setStatus(Message.HELLO);
        ksession.insert(message);
        ksession.fireAllRules();
        logger.close();
        assertEquals( 6, knownVariables.size() );
        assertTrue(knownVariables.contains("drools"));
        assertTrue(knownVariables.contains("myMessage"));
        assertTrue(knownVariables.contains("rule"));
        assertTrue(knownVariables.contains("kcontext"));
        assertTrue(knownVariables.contains("this"));        
        assertTrue(knownVariables.contains("m"));
        assertTrue(knownVariables.contains("myMessage"));
    }

    private KnowledgeBase readKnowledgeBase() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(
            ResourceFactory.newClassPathResource("Sample.drl", HelloWorldTest.class),
            ResourceType.DRL);
        if (kbuilder.hasErrors()) {
           fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;
    }

}
