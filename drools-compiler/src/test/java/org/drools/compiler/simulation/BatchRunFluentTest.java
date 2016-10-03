package org.drools.compiler.simulation;

import org.drools.compiler.Message;
import org.drools.core.command.RequestContextImpl;
import org.drools.core.fluent.impl.FluentBuilderImpl;
import org.drools.core.fluent.impl.PsuedoClockRunner;
import org.kie.internal.fluent.runtime.FluentBuilder;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.ResourceType;
import org.drools.compiler.CommonTestMethodBase;
import org.kie.internal.fluent.Scope;
import org.kie.internal.fluent.runtime.KieSessionFluent;

public class BatchRunFluentTest extends CommonTestMethodBase {
    String header = "package org.drools.compiler\n" +
                    "import " + Message.class.getCanonicalName() + "\n";

    String drl1 = "global String outS;\n" +
                  "global Long timeNow;\n" +
                  "rule R1 when\n" +
                  "   s : String()\n" +
                  "then\n" +
                  "    kcontext.getKnowledgeRuntime().setGlobal(\"outS\", s);\n" +
                  "    kcontext.getKnowledgeRuntime().setGlobal(\"timeNow\", kcontext.getKnowledgeRuntime().getSessionClock().getCurrentTime() );\n" +
                  "end\n";

    ReleaseId    releaseId = SimulateTestBase.createKJarWithMultipleResources("org.kie", new String[]{header + drl1}, new ResourceType[] {ResourceType.DRL});

    @Test
    public void testOutName() {
        PsuedoClockRunner runner = new PsuedoClockRunner();

        FluentBuilder f          = new FluentBuilderImpl();

        f.newApplicationContext("app1")
         .getKieContainer(releaseId).newSession()
         .insert("h1")
         .fireAllRules()
         .getGlobal("outS").out("outS")
         .dispose();

        RequestContextImpl requestContext = (RequestContextImpl) runner.execute(f.getExecutable());

        assertEquals("h1", requestContext.getOut().get("outS"));
    }


    @Test
    public void testOutWithPriorSetAndNoName() {
        PsuedoClockRunner runner = new PsuedoClockRunner();

        FluentBuilder f         = new FluentBuilderImpl();

        f.newApplicationContext("app1")
         .getKieContainer(releaseId).newSession()
         .insert("h1")
         .fireAllRules()
         .getGlobal("outS").set("outS").out()
         .dispose();

        RequestContextImpl requestContext = (RequestContextImpl) runner.execute(f.getExecutable());

        assertEquals("h1", requestContext.getOut().get("outS"));
        assertEquals("h1", requestContext.get("outS"));
    }

    @Test
    public void testOutWithoutPriorSetAndNoName() {
        PsuedoClockRunner runner = new PsuedoClockRunner();

        FluentBuilder f         = new FluentBuilderImpl();

        f.newApplicationContext("app1")
         .getKieContainer(releaseId).newSession()
         .insert("h1")
         .fireAllRules()
         .getGlobal("outS").out()
         .dispose();

        try {
            RequestContextImpl requestContext = (RequestContextImpl) runner.execute(f.getExecutable());

            assertEquals("h1", requestContext.getOut().get("out1"));
            fail("Must throw Exception, as no prior set was called and no name given to out");
        } catch ( Exception e ) {

        }
    }

    @Test
    public void testSetAndGetWithCommandRegisterWithEnds() {
        PsuedoClockRunner runner = new PsuedoClockRunner();

        FluentBuilder f         = new FluentBuilderImpl();

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

        RequestContextImpl requestContext = (RequestContextImpl) runner.execute(f.getExecutable());

        // Check that nothing went to the 'out'
        assertEquals("h1", requestContext.getOut().get("outS1"));
        assertEquals("h2", requestContext.getOut().get("outS2"));
    }

    @Test
    public void testSetAndGetWithCommandRegisterWithoutEnds() {
        PsuedoClockRunner runner = new PsuedoClockRunner();

        FluentBuilder f         = new FluentBuilderImpl();

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

        RequestContextImpl requestContext = (RequestContextImpl) runner.execute(f.getExecutable());

        // Check that nothing went to the 'out'
        assertEquals("h1", requestContext.getOut().get("outS1"));
        assertEquals("h2", requestContext.getOut().get("outS2"));
    }


    @Test
    public void testConversationIdIncreases() {
        PsuedoClockRunner runner = new PsuedoClockRunner();

        FluentBuilder f         = new FluentBuilderImpl();

        f.newApplicationContext("app1").startConversation()
         .getKieContainer(releaseId).newSession()
         .insert("h1")
         .fireAllRules()
         .dispose();

        RequestContextImpl requestContext = (RequestContextImpl) runner.execute(f.getExecutable());

        long conversationId = requestContext.getConversationContext().getConversationId();
        assertEquals(0, conversationId);

        requestContext = (RequestContextImpl) runner.execute(f.getExecutable());

        conversationId = requestContext.getConversationContext().getConversationId();
        assertEquals(1, conversationId);
    }

    @Test
    public void testRequestScope() {
        PsuedoClockRunner runner = new PsuedoClockRunner();

        FluentBuilder f         = new FluentBuilderImpl();

        f.newApplicationContext("app1")
         .getKieContainer(releaseId).newSession()
         .insert("h1")
         .fireAllRules()
         .getGlobal("outS").set("outS1") // Request is default
         .dispose();

        RequestContextImpl requestContext = (RequestContextImpl) runner.execute(f.getExecutable());

        // Check that nothing went to the 'out'
        assertNull(requestContext.getOut().get("outS"));
        assertNull(requestContext.getApplicationContext().get("outS1") );
        assertNull(requestContext.getConversationContext() );
        assertEquals("h1", requestContext.get("outS1") );
    }

    @Test
    public void testApplicationScope() {
        PsuedoClockRunner runner = new PsuedoClockRunner();

        FluentBuilder f         = new FluentBuilderImpl();

        f.newApplicationContext("app1")
         .getKieContainer(releaseId).newSession()
         .insert("h1")
         .fireAllRules()
         .getGlobal("outS").set("outS1", Scope.APPLICATION)
         .dispose();

        RequestContextImpl requestContext = (RequestContextImpl) runner.execute(f.getExecutable());

        // Check that nothing went to the 'out'
        assertEquals(null, requestContext.getOut().get("outS"));
        assertEquals("h1", requestContext.getApplicationContext().get("outS1") );

        // Make another request, add to application context, assert old and new values are there.
        f         = new FluentBuilderImpl();

        f.getApplicationContext("app1")
         .getKieContainer(releaseId).newSession()
         .insert("h2")
         .fireAllRules()
         .getGlobal("outS").set("outS2", Scope.APPLICATION)
         .dispose();

        requestContext = (RequestContextImpl) runner.execute(f.getExecutable());
        assertEquals("h1", requestContext.getApplicationContext().get("outS1") );
        assertEquals("h2", requestContext.getApplicationContext().get("outS2") );
    }


    @Test
    public void testConversationScope() {
        PsuedoClockRunner runner = new PsuedoClockRunner();

        FluentBuilder f         = new FluentBuilderImpl();

        f.newApplicationContext("app1").startConversation()
         .getKieContainer(releaseId).newSession()
         .insert("h1")
         .fireAllRules()
         .getGlobal("outS").set("outS1", Scope.CONVERSATION)
         .dispose();

        RequestContextImpl requestContext = (RequestContextImpl) runner.execute(f.getExecutable());

        // check that nothing went to the 'out'
        assertEquals(null, requestContext.getOut().get("outS"));

        long conversationId = requestContext.getConversationContext().getConversationId();

        assertEquals("h1", requestContext.getConversationContext().get("outS1") );

        // Make another request, add to conversation context, assert old and new values are there.
        f         = new FluentBuilderImpl();

        f.getApplicationContext("app1").joinConversation(conversationId)
         .getKieContainer(releaseId).newSession()
         .insert("h2")
         .fireAllRules()
         .getGlobal("outS").set("outS2", Scope.CONVERSATION)
         .dispose();

        requestContext = (RequestContextImpl) runner.execute(f.getExecutable());
        assertEquals("h1", requestContext.getConversationContext().get("outS1") );
        assertEquals("h2", requestContext.getConversationContext().get("outS2") );

        // End the conversation, check it's now null
        f         = new FluentBuilderImpl();

        f.endConversation(conversationId);

        requestContext = (RequestContextImpl) runner.execute(f.getExecutable());
        assertNull(requestContext.getConversationContext());
    }

    @Test
    public void testContextScopeSearching() {
        PsuedoClockRunner runner = new PsuedoClockRunner();

        FluentBuilder f         = new FluentBuilderImpl();

        // Check that get() will search up to Application, when no request or conversation values
        f.newApplicationContext("app1")
         .getKieContainer(releaseId).newSession()
         .insert("h1")
         .fireAllRules()
         .getGlobal("outS").set("outS1", Scope.APPLICATION)
         .get("outS1").out()
         .dispose();
        RequestContextImpl requestContext = (RequestContextImpl) runner.execute(f.getExecutable());

        assertEquals("h1", requestContext.getOut().get("outS1"));
        assertEquals("h1", requestContext.getApplicationContext().get("outS1") );
        assertEquals("h1", requestContext.get("outS1") );

        // Check that get() will search up to Conversation, thus over-riding Application scope and ignoring Request when it has no value
        f         = new FluentBuilderImpl();

        f.getApplicationContext("app1").startConversation()
         .getKieContainer(releaseId).newSession()
         .insert("h2")
         .fireAllRules()
         .getGlobal("outS").set("outS1", Scope.CONVERSATION)
         .get("outS1").out()
         .dispose();
        requestContext = (RequestContextImpl) runner.execute(f.getExecutable());

        assertEquals("h2", requestContext.getOut().get("outS1"));
        assertEquals("h1", requestContext.getApplicationContext().get("outS1") );
        assertEquals("h2", requestContext.getConversationContext().get("outS1") );
        assertEquals("h2", requestContext.get("outS1") );


        // Check that get() will search directly to Request, thus over-riding Application and Conversation scoped values
        f         = new FluentBuilderImpl();

        f.getApplicationContext("app1").joinConversation(requestContext.getConversationContext().getConversationId())
         .getKieContainer(releaseId).newSession()
         .insert("h3")
         .fireAllRules()
         .getGlobal("outS").set("outS1", Scope.REQUEST)
         .get("outS1").out()
         .dispose();
        requestContext = (RequestContextImpl) runner.execute(f.getExecutable());

        assertEquals("h3", requestContext.getOut().get("outS1"));
        assertEquals("h1", requestContext.getApplicationContext().get("outS1") );
        assertEquals("h2", requestContext.getConversationContext().get("outS1") );
        assertEquals("h3", requestContext.get("outS1") );
    }



    @Test
    public void testAfter() {
        PsuedoClockRunner runner = new PsuedoClockRunner(0);

        FluentBuilder f         = new FluentBuilderImpl();

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

        RequestContextImpl requestContext = (RequestContextImpl) runner.execute(f.getExecutable());

        assertEquals(1000l, requestContext.getOut().get("timeNow1"));
        assertEquals(2000l, requestContext.getOut().get("timeNow2"));
    }

    public static KieModule createAndDeployJar( KieServices ks,
                                                ReleaseId releaseId,
                                                String... drls ) {
        byte[] jar = createKJar( ks, releaseId, null, drls );
        return deployJar( ks, jar );
    }

}
