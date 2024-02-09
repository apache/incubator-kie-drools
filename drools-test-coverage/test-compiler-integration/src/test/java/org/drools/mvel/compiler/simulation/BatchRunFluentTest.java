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
package org.drools.mvel.compiler.simulation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.drools.commands.fluent.ExecutableBuilderImpl;
import org.drools.mvel.compiler.Message;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.RequestContext;
import org.kie.internal.builder.fluent.ExecutableBuilder;
import org.kie.internal.builder.fluent.KieSessionFluent;
import org.kie.internal.builder.fluent.Scope;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Parameterized.class)
public class BatchRunFluentTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    String header = "package org.drools.mvel.compiler\n" +
            "import " + Message.class.getCanonicalName() + "\n";

    String drl1 = "global String outS;\n" +
            "global String outS2;\n" +
            "global Long timeNow;\n" +
            "rule R1\n" +
            "when\n" +
            "   s : String()\n" +
            "then\n" +
            "    kcontext.getKnowledgeRuntime().setGlobal(\"outS\", s);\n" +
            "    kcontext.getKnowledgeRuntime().setGlobal(\"timeNow\", kcontext.getKnowledgeRuntime().getSessionClock().getCurrentTime() );\n" +
            "end\n\n" +
            "rule R2\n" +
            "agenda-group \"agenda2\"\n" +
            "when\n" +
            "   s : String()\n" +
            "then\n" +
            "    kcontext.getKnowledgeRuntime().setGlobal(\"outS2\", s);\n" +
            "end\n";

    String id = "org.kie";
    ReleaseId releaseId;

    public BatchRunFluentTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseStreamConfigurations(true, true);
    }

    @Before
    public void setUp() {
        releaseId = KieUtil.generateReleaseId(id);
        final List<Resource> resources = KieUtil.getResourcesFromDrls(header + drl1);
        KieUtil.getKieModuleFromResources(releaseId, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO, new HashMap<>(), resources.toArray(new Resource[]{}));
    }

    @Test
    public void testOutName() {
        ExecutableBuilder f = ExecutableBuilder.create();

        f.newApplicationContext("app1")
                .getKieContainer(releaseId).newSession()
                .insert("h1")
                .fireAllRules()
                .getGlobal("outS").out("outS")
                .dispose();

        RequestContext requestContext = ExecutableRunner.create().execute(f.getExecutable());

        assertThat(requestContext.getOutputs().get("outS")).isEqualTo("h1");
    }

    @Test
    public void testOutWithPriorSetAndNoName() {
        ExecutableBuilder f = ExecutableBuilder.create();

        f.newApplicationContext("app1")
                .getKieContainer(releaseId).newSession()
                .insert("h1")
                .fireAllRules()
                .getGlobal("outS").set("outS").out()
                .dispose();

        RequestContext requestContext = ExecutableRunner.create().execute(f.getExecutable());

        assertThat(requestContext.getOutputs().get("outS")).isEqualTo("h1");
        assertThat(requestContext.get("outS")).isEqualTo("h1");
    }

    @Test
    public void testSetAndOutBehaviour() {
        ExecutableBuilder f = ExecutableBuilder.create();

        f.newApplicationContext("app1")
                .getKieContainer(releaseId).newSession()
                .insert("h1")
                .fireAllRules()
                .getGlobal("outS").set("outS")
                .getGlobal("outS").set("outS1").out()
                .dispose();

        RequestContext requestContext = ExecutableRunner.create().execute(f.getExecutable());

        assertThat(requestContext.getOutputs().get("outS")).isNull();
        assertThat(requestContext.get("outS")).isEqualTo("h1");

        assertThat(requestContext.getOutputs().get("outS1")).isNotNull();
        assertThat(requestContext.getOutputs().get("outS1")).isEqualTo(requestContext.get("outS1"));
        assertThat(requestContext.get("outS1")).isEqualTo(requestContext.get("outS"));
    }

    @Test
    public void testOutWithoutPriorSetAndNoName() {
        ExecutableBuilder f = ExecutableBuilder.create();

        f.newApplicationContext("app1")
                .getKieContainer(releaseId).newSession()
                .insert("h1")
                .fireAllRules()
                .getGlobal("outS").out()
                .dispose();

        try {
            RequestContext requestContext = ExecutableRunner.create().execute(f.getExecutable());

            assertThat(requestContext.get("out1")).isEqualTo("h1");
            fail("Must throw Exception, as no prior set was called and no name given to out");
        } catch (Exception e) {

        }
    }

    @Test
    public void testSetAndGetWithCommandRegisterWithEnds() {
        ExecutableBuilder f = ExecutableBuilder.create();

        f.newApplicationContext("app1")
                // create two sessions, and assign names
                .getKieContainer(releaseId).newSession().set("s1").end()
                .getKieContainer(releaseId).newSession().set("s2").end()
                // initialise s1 with data
                .get("s1", KieSessionFluent.class)
                .insert("h1").fireAllRules().end()

                // initialise s2 with data
                .get("s2", KieSessionFluent.class)
                .insert("h2").fireAllRules().end()

                // assign s1 to out
                .get("s1", KieSessionFluent.class)
                .getGlobal("outS").out("outS1").dispose()

                .get("s2", KieSessionFluent.class)
                .getGlobal("outS").out("outS2").dispose();

        RequestContext requestContext = ExecutableRunner.create().execute(f.getExecutable());

        // Check that nothing went to the 'out'
        assertThat(requestContext.getOutputs().get("outS1")).isEqualTo("h1");
        assertThat(requestContext.getOutputs().get("outS2")).isEqualTo("h2");
    }

    @Test
    public void testSetAndGetWithCommandRegisterWithoutEnds() {
        ExecutableBuilder f = ExecutableBuilder.create();

        f.newApplicationContext("app1")
                // create two sessions, and assign names
                .getKieContainer(releaseId).newSession().set("s1").end() // this end is needed, it's the get(String, Class) we are checking to see if it auto ends
                .getKieContainer(releaseId).newSession().set("s2")
                // initialise s1 with data
                .get("s1", KieSessionFluent.class)
                .insert("h1").fireAllRules()

                // initialise s2 with data
                .get("s2", KieSessionFluent.class)
                .insert("h2").fireAllRules()

                // assign s1 to out
                .get("s1", KieSessionFluent.class)
                .getGlobal("outS").out("outS1").dispose()

                .get("s2", KieSessionFluent.class)
                .getGlobal("outS").out("outS2").dispose();

        RequestContext requestContext = ExecutableRunner.create().execute(f.getExecutable());

        // Check that nothing went to the 'out'
        assertThat(requestContext.getOutputs().get("outS1")).isEqualTo("h1");
        assertThat(requestContext.getOutputs().get("outS2")).isEqualTo("h2");
    }

    @Test
    public void testDifferentConversationIds() {
        ExecutableRunner<RequestContext> runner = ExecutableRunner.create();
        RequestContext requestContext = runner.createContext();

        ExecutableBuilder f = ExecutableBuilder.create();

        f.newApplicationContext("app1").startConversation()
                .getKieContainer(releaseId).newSession()
                .insert("h1")
                .fireAllRules()
                .dispose();

        runner.execute(f.getExecutable(), requestContext);

        String conversationId = requestContext.getConversationContext().getName();

        runner.execute(f.getExecutable(), requestContext);

        assertThat(requestContext.getConversationContext().getName()).isNotEqualTo(conversationId);
    }

    @Test
    public void testRequestScope() {
        ExecutableBuilder f = ExecutableBuilder.create();

        f.newApplicationContext("app1")
                .getKieContainer(releaseId).newSession()
                .insert("h1")
                .fireAllRules()
                .getGlobal("outS").set("outS1") // Request is default
                .dispose();

        RequestContext requestContext = ExecutableRunner.create().execute(f.getExecutable());

        // Check that nothing went to the 'out'
        assertThat(requestContext.get("outS")).isNull();
        assertThat(requestContext.getOutputs().get("outS1")).isNull();
        assertThat(requestContext.getApplicationContext().get("outS1")).isNull();
        assertThat(requestContext.getConversationContext()).isNull();
        assertThat(requestContext.get("outS1")).isEqualTo("h1");
    }

    @Test
    public void testApplicationScope() {
        ExecutableRunner<RequestContext> runner = ExecutableRunner.create();

        ExecutableBuilder f = ExecutableBuilder.create();

        f.newApplicationContext("app1")
                .getKieContainer(releaseId).newSession()
                .insert("h1")
                .fireAllRules()
                .getGlobal("outS").set("outS1", Scope.APPLICATION)
                .dispose();

        RequestContext requestContext = runner.execute(f.getExecutable());

        // Check that nothing went to the 'out'
        assertThat(requestContext.get("outS")).isEqualTo(null);
        assertThat(requestContext.getApplicationContext().get("outS1")).isEqualTo("h1");

        // Make another request, add to application context, assert old and new values are there.
        f = new ExecutableBuilderImpl();

        f.getApplicationContext("app1")
                .getKieContainer(releaseId).newSession()
                .insert("h2")
                .fireAllRules()
                .getGlobal("outS").set("outS2", Scope.APPLICATION)
                .dispose();

        requestContext = runner.execute(f.getExecutable());
        assertThat(requestContext.getApplicationContext().get("outS1")).isEqualTo("h1");
        assertThat(requestContext.getApplicationContext().get("outS2")).isEqualTo("h2");
    }

    @Test
    public void testConversationScope() {
        ExecutableRunner<RequestContext> runner = ExecutableRunner.create();

        ExecutableBuilder f = ExecutableBuilder.create();

        f.newApplicationContext("app1").startConversation()
                .getKieContainer(releaseId).newSession()
                .insert("h1")
                .fireAllRules()
                .getGlobal("outS").set("outS1", Scope.CONVERSATION)
                .dispose();

        RequestContext requestContext = runner.execute(f.getExecutable());

        // check that nothing went to the 'out'
        assertThat(requestContext.get("outS")).isEqualTo(null);

        String conversationId = requestContext.getConversationContext().getName();

        assertThat(requestContext.getConversationContext().get("outS1")).isEqualTo("h1");

        // Make another request, add to conversation context, assert old and new values are there.
        f = new ExecutableBuilderImpl();

        f.getApplicationContext("app1").joinConversation(conversationId)
                .getKieContainer(releaseId).newSession()
                .insert("h2")
                .fireAllRules()
                .getGlobal("outS").set("outS2", Scope.CONVERSATION)
                .dispose();

        requestContext = runner.execute(f.getExecutable());
        assertThat(requestContext.getConversationContext().get("outS1")).isEqualTo("h1");
        assertThat(requestContext.getConversationContext().get("outS2")).isEqualTo("h2");

        // End the conversation, check it's now null
        f = new ExecutableBuilderImpl();

        f.endConversation(conversationId);

        requestContext = runner.execute(f.getExecutable());
        assertThat(requestContext.getConversationContext()).isNull();
    }

    @Test
    public void testContextScopeSearching() {
        ExecutableRunner<RequestContext> runner = ExecutableRunner.create();

        ExecutableBuilder f = ExecutableBuilder.create();

        // Check that get() will search up to Application, when no request or conversation values
        f.newApplicationContext("app1")
                .getKieContainer(releaseId).newSession()
                .insert("h1")
                .fireAllRules()
                .getGlobal("outS").set("outS1", Scope.APPLICATION)
                .get("outS1").out()
                .dispose();
        RequestContext requestContext = runner.execute(f.getExecutable());

        assertThat(requestContext.get("outS1")).isEqualTo("h1");
        assertThat(requestContext.getApplicationContext().get("outS1")).isEqualTo("h1");
        assertThat(requestContext.get("outS1")).isEqualTo("h1");

        // Check that get() will search up to Conversation, thus over-riding Application scope and ignoring Request when it has no value
        f = new ExecutableBuilderImpl();

        f.getApplicationContext("app1").startConversation()
                .getKieContainer(releaseId).newSession()
                .insert("h2")
                .fireAllRules()
                .getGlobal("outS").set("outS1", Scope.CONVERSATION)
                .get("outS1").out()
                .dispose();
        requestContext = runner.execute(f.getExecutable());

        assertThat(requestContext.get("outS1")).isEqualTo("h2");
        assertThat(requestContext.getApplicationContext().get("outS1")).isEqualTo("h1");
        assertThat(requestContext.getConversationContext().get("outS1")).isEqualTo("h2");
        assertThat(requestContext.get("outS1")).isEqualTo("h2");

        // Check that get() will search directly to Request, thus over-riding Application and Conversation scoped values
        f = new ExecutableBuilderImpl();

        f.getApplicationContext("app1").joinConversation(requestContext.getConversationContext().getName())
                .getKieContainer(releaseId).newSession()
                .insert("h3")
                .fireAllRules()
                .getGlobal("outS").set("outS1", Scope.REQUEST)
                .get("outS1").out()
                .dispose();
        requestContext = runner.execute(f.getExecutable());

        assertThat(requestContext.get("outS1")).isEqualTo("h3");
        assertThat(requestContext.getApplicationContext().get("outS1")).isEqualTo("h1");
        assertThat(requestContext.getConversationContext().get("outS1")).isEqualTo("h2");
        assertThat(requestContext.get("outS1")).isEqualTo("h3");
    }

    @Test
    public void testAfter() {
        ExecutableRunner<RequestContext> runner = ExecutableRunner.create(0L);

        ExecutableBuilder f = ExecutableBuilder.create();

        // Check that get() will search up to Application, when no request or conversation values
        f.after(1000).newApplicationContext("app1")
                .getKieContainer(releaseId).newSession()
                .insert("h1")
                .fireAllRules()
                .getGlobal("outS").out("outS1")
                .getGlobal("timeNow").out("timeNow1")
                .dispose()
                .after(2000).newApplicationContext("app1")
                .getKieContainer(releaseId).newSession()
                .insert("h1")
                .fireAllRules()
                .getGlobal("outS").out("outS2")
                .getGlobal("timeNow").out("timeNow2")
                .dispose();

        RequestContext requestContext = runner.execute(f.getExecutable());

        assertThat(requestContext.getOutputs().get("timeNow1")).isEqualTo(1000l);
        assertThat(requestContext.getOutputs().get("timeNow2")).isEqualTo(2000l);
    }

    @Test
    public void testSetKieContainerTest() {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.newKieContainer(releaseId);

        ExecutableRunner<RequestContext> runner = ExecutableRunner.create(0L);

        ExecutableBuilder f = ExecutableBuilder.create();

        f.newApplicationContext("app1")
                .setKieContainer(kieContainer)
                .newSession()
                .insert("h1")
                .fireAllRules()
                .getGlobal("outS").out("outS1")
                .dispose();

        RequestContext requestContext = runner.execute(f.getExecutable());

        assertThat(requestContext.getOutputs().get("outS1")).isEqualTo("h1");
    }

    @Test
    public void testKieSessionCustomizationTest() {
        ExecutableRunner<RequestContext> runner = ExecutableRunner.create(0L);

        ExecutableBuilder f = ExecutableBuilder.create();

        f.newApplicationContext("app1")
                .getKieContainer(releaseId)
                .newSessionCustomized(null, ((sessionName, kieContainer) -> kieContainer.getKieSessionConfiguration(sessionName)))
                .insert("h1")
                .fireAllRules()
                .getGlobal("outS").out("outS1")
                .dispose();

        RequestContext requestContext = runner.execute(f.getExecutable());

        assertThat(requestContext.getOutputs().get("outS1")).isEqualTo("h1");
    }

    @Test
    public void testKieSessionByName() {
        ExecutableRunner<RequestContext> runner = ExecutableRunner.create(0L);

        ExecutableBuilder f = ExecutableBuilder.create();

        f.newApplicationContext("app1")
                .getKieContainer(releaseId)
                .newSession(KieSessionTestConfiguration.KIE_SESSION_MODEL_NAME)
                .insert("h1")
                .fireAllRules()
                .getGlobal("outS").out("outS1")
                .dispose();

        RequestContext requestContext = runner.execute(f.getExecutable());

        assertThat(requestContext.getOutputs().get("outS1")).isEqualTo("h1");
    }

    @Test
    public void testAgendaGroup() {
        ExecutableRunner<RequestContext> runner = ExecutableRunner.create(0L);

        ExecutableBuilder f = ExecutableBuilder.create();

        f.newApplicationContext("app1")
                .getKieContainer(releaseId)
                .newSession()
                .insert("h1")
                .fireAllRules()
                .getGlobal("outS2").out("outS2")
                .dispose();

        RequestContext requestContext = runner.execute(f.getExecutable());

        assertThat(requestContext.getOutputs().get("outS2")).isNotEqualTo("h1");

        // now set active agenda group
        f = ExecutableBuilder.create();

        f.newApplicationContext("app1")
                .getKieContainer(releaseId)
                .newSession()
                .setActiveAgendaGroup("agenda2")
                .insert("h1")
                .fireAllRules()
                .getGlobal("outS2").out("outS2")
                .dispose();

        requestContext = runner.execute(f.getExecutable());

        assertThat(requestContext.getOutputs().get("outS2")).isEqualTo("h1");
    }
}
