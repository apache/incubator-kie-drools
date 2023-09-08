/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.drools.mvel.compiler.Message;
import org.drools.mvel.expr.MVELDebugHandler;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieSession;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;
import org.mvel2.MVELRuntime;
import org.mvel2.debug.Debugger;
import org.mvel2.debug.Frame;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is a sample class to launch a rule.
 */
@RunWith(Parameterized.class)
public class HelloWorldTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public HelloWorldTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        // not for exec-model
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test
    public void testHelloWorld() throws Exception {
        // load up the knowledge base
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = kbase.newKieSession();
        File testTmpDir = new File("target/test-tmp/");
        testTmpDir.mkdirs();
        KieRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger( ksession, "target/test-tmp/testHelloWorld" );
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
                knownVariables.addAll(frame.getFactory().getKnownVariables());
                return 0;
            }
        });
        String source = "org.drools.integrationtests.Rule_Hello_World";
        MVELRuntime.registerBreakpoint(source, 1);
        // load up the knowledge base
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = kbase.newKieSession();
        File testTmpDir = new File("target/test-tmp/");
        testTmpDir.mkdirs();
        KieRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger( ksession, "target/test-tmp/testHelloWorldDebug" );
        // go !
        Message message = new Message();
        message.setMessage("Hello World");
        message.setStatus(Message.HELLO);
        ksession.insert(message);
        ksession.fireAllRules();
        logger.close();
        assertThat(knownVariables.size()).isEqualTo(6);
        assertThat(knownVariables.contains("drools")).isTrue();
        assertThat(knownVariables.contains("myMessage")).isTrue();
        assertThat(knownVariables.contains("rule")).isTrue();
        assertThat(knownVariables.contains("kcontext")).isTrue();
        assertThat(knownVariables.contains("this")).isTrue();
        assertThat(knownVariables.contains("m")).isTrue();
        assertThat(knownVariables.contains("myMessage")).isTrue();
    }

    private KieBase readKnowledgeBase() throws Exception {
        return KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "Sample.drl");
    }

}
