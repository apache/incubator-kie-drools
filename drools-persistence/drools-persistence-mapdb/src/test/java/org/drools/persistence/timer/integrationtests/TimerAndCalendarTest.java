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

package org.drools.persistence.timer.integrationtests;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.drools.core.ClockType;
import org.drools.core.time.SessionPseudoClock;
import org.drools.persistence.mapdb.util.MapDBPersistenceUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.time.SessionClock;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;

public class TimerAndCalendarTest {
    
    private Map<String, Object> context;

    @Before
    public void before() throws Exception {
        context = MapDBPersistenceUtil.setupMapDB();
    }

    @After
    public void after() throws Exception {
        MapDBPersistenceUtil.cleanUp(context);
    }

    @Test @Ignore("beta4 phreak")
    public void testTimerRuleAfterIntReloadSession() throws Exception {
        KieBase kbase = KieServices.Factory.get().getKieClasspathContainer().newKieBase(null);
        KieSession ksession = createSession( kbase );

        // must advance time or it won't save.
        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 300,
                           TimeUnit.MILLISECONDS );

        // if we do not call 'ksession.fireAllRules()', this test will run successfully.
        ksession.fireAllRules();

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 300,
                           TimeUnit.MILLISECONDS );

        ksession = disposeAndReloadSession( ksession,
                                            kbase );

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 300,
                           TimeUnit.MILLISECONDS );

        // build timer rule, if the rule is fired, the list size will increase every 500ms
        String timerRule = "package org.drools.test\n" +
                           "global java.util.List list \n" +
                           "rule TimerRule \n" +
                           "   timer (int:1000 500) \n" +
                           "when \n" + "then \n" +
                           "        list.add(list.size()); \n" +
                           " end";
        Resource resource = ResourceFactory.newByteArrayResource(timerRule.getBytes());
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write("src/main/resources/r1.drl", resource);
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
        	fail (kbuilder.getResults().toString());
        }
        kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 10, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 10, TimeUnit.MILLISECONDS );

        ksession = disposeAndReloadSession( ksession, kbase );

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 10, TimeUnit.MILLISECONDS );

        List<Integer> list = Collections.synchronizedList( new ArrayList<Integer>() );
        ksession.setGlobal( "list",
                            list );
        Assert.assertEquals( 0, list.size() );

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 1700, TimeUnit.MILLISECONDS );
        Assert.assertEquals( 2, list.size() );

        ksession = disposeAndReloadSession( ksession, kbase );
        ksession.setGlobal( "list", list );

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 1000, TimeUnit.MILLISECONDS );

        // if the rule is fired, the list size will greater than one.
        Assert.assertEquals( 4, list.size() );
    }

    @Test @Ignore("beta4 phreak")
    public void testTimerRuleAfterCronReloadSession() throws Exception {
        KieBase kbase = KieServices.Factory.get().getKieClasspathContainer().newKieBase(null);
        KieSession ksession = createSession( kbase );

        // must advance time or it won't save.
        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 300, TimeUnit.MILLISECONDS );

        // if we do not call 'ksession.fireAllRules()', this test will run successfully.
        ksession.fireAllRules();

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 300, TimeUnit.MILLISECONDS );

        ksession = disposeAndReloadSession( ksession, kbase );

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 300, TimeUnit.MILLISECONDS );

        // build timer rule, if the rule is fired, the list size will increase every 300ms
        String timerRule = "package org.drools.test\n" +
                           "global java.util.List list \n" +
                           "rule TimerRule \n" +
                           "   timer (cron: * * * * * ?) \n" +
                           "when \n" + "then \n" +
                           "        list.add(list.size()); \n" +
                           " end";
        Resource resource = ResourceFactory.newByteArrayResource( timerRule.getBytes() );
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write("src/main/resources/r1.drl", resource);
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
        	fail (kbuilder.getResults().toString());
        }
        kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();

        List<Integer> list = Collections.synchronizedList( new ArrayList<Integer>() );
        ksession.setGlobal( "list", list );

        ksession.setGlobal( "list", list );
        clock.advanceTime( 10, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 10,
                           TimeUnit.MILLISECONDS );

        ksession = disposeAndReloadSession( ksession,
                                            kbase );
        ksession.setGlobal( "list",
                            list );

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 10,
                           TimeUnit.MILLISECONDS );

        Assert.assertEquals( 1, list.size() );

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 3, TimeUnit.SECONDS );
        Assert.assertEquals( 4, list.size() );

        ksession = disposeAndReloadSession( ksession, kbase );
        ksession.setGlobal( "list", list );

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 2, TimeUnit.SECONDS );

        // if the rule is fired, the list size will greater than one.
        Assert.assertEquals( 6, list.size() );
    }

    @Test
    public void testEventExpires() throws Exception {
        String timerRule = "package org.drools.test\n" +
                           "declare TestEvent \n" +
                           "    @role( event )\n" +
                           "    @expires( 10s )\n" +
                           "end\n" +
                           "" +
                           "rule TimerRule \n" +
                           "    when \n" +
                           "        TestEvent( ) from entry-point \"Test\"\n" +
                           "    then \n" +
                           "end";
        Resource resource = ResourceFactory.newByteArrayResource( timerRule.getBytes() );
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write("src/main/resources/r1.drl", resource);
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
        	fail (kbuilder.getResults().toString());
        }
        KieBase kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();
        KieSession ksession = createSession( kbase );
        FactType type = kbase.getFactType( "org.drools.test", "TestEvent" );
        Assert.assertNotNull( "could not get type", type );
        ksession = disposeAndReloadSession( ksession, kbase );
        ksession.getEntryPoint( "Test" ).insert( type.newInstance() );
        ksession.fireAllRules();
        ksession = disposeAndReloadSession( ksession, kbase );
        ksession = disposeAndReloadSession( ksession, kbase );
    }

    @Test
    public void testTimerWithRemovingRule() throws Exception {
        // DROOLS-576
        // Only reproducible with RETEOO
        KnowledgeBase kbase1  = KnowledgeBaseFactory.newKnowledgeBase();

        String str1 = "package org.test; " +
                "import java.util.*; " +

                "global java.util.List list; " +

                "rule R1\n" +
                "    timer ( int: 5s )\n" +
                "when\n" +
                "    $s : String( )\n" +
                "then\n" +
                "    list.add( $s );\n" +
                "end\n";

        Resource resource1 = ResourceFactory.newByteArrayResource(str1.getBytes());
        Collection<KnowledgePackage> kpackages1 = buildKnowledgePackage( resource1,
                ResourceType.DRL );
        kbase1.addKnowledgePackages( kpackages1 );
        
        KieSession ksession1 = KieServices.Factory.get().getStoreServices().newKieSession(kbase1, null,
                MapDBPersistenceUtil.createEnvironment(context));
        long ksessionId = ksession1.getIdentifier();

        ArrayList<String> list = new ArrayList<String>();
        ksession1.setGlobal( "list", list );

        ksession1.insert("hello");
        ksession1.fireAllRules();

        ksession1.dispose(); // dispose before firing

        Assert.assertEquals(0, list.size());

        Thread.sleep(5000);

        // A new kbase without the timer's activated rule
        KnowledgeBase kbase2  = KnowledgeBaseFactory.newKnowledgeBase();

        String str2 = "package org.test; " +
                "import java.util.*; " +

                "global java.util.List list; " +

                "rule R2\n" +
                "when\n" +
                "    $s : Integer( )\n" +
                "then\n" +
                "    list.add( $s );\n" +
                "end\n";

        Resource resource2 = ResourceFactory.newByteArrayResource(str2.getBytes());
        Collection<KnowledgePackage> kpackages2 = buildKnowledgePackage( resource2,
                                                                        ResourceType.DRL );
        kbase2.addKnowledgePackages( kpackages2 );
        
        KieSession ksession2 = KieServices.Factory.get().getStoreServices().loadKieSession(ksessionId, kbase2, null,
                MapDBPersistenceUtil.createEnvironment(context));

        ksession2.setGlobal( "list", list );

        ksession2.fireAllRules();

        ksession2.dispose();

        Assert.assertEquals(0, list.size());
    }

    private Collection<KnowledgePackage> buildKnowledgePackage(Resource resource,
            ResourceType resourceType) {
    	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    	kbuilder.add( resource, resourceType );
    	KnowledgeBuilderErrors errors = kbuilder.getErrors();
    	if ( errors != null && errors.size() > 0 ) {
    		for ( KnowledgeBuilderError error : errors ) {
    			System.err.println( "Error: " + error.getMessage() );
    		}
    		Assert.fail( "KnowledgeBase did not build" );
    	}
    	Collection<KnowledgePackage> packages = kbuilder.getKnowledgePackages();
    	return packages;
    }

    private KieSession createSession(KieBase kbase) {
        final KieSessionConfiguration conf = KieServices.Factory.get().newKieSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        Environment env = MapDBPersistenceUtil.createEnvironment(context);
        KieSession ksession = KieServices.Factory.get().getStoreServices().newKieSession( kbase, conf, env );
        return ksession;
    }

    private KieSession disposeAndReloadSession(KieSession ksession, KieBase kbase) {
        long ksessionId = ksession.getIdentifier();
        ksession.dispose();
        final KieSessionConfiguration conf = KieServices.Factory.get().newKieSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        KieSession newksession = KieServices.Factory.get().getStoreServices().loadKieSession( ksessionId,
                                                                                                 kbase,
                                                                                                 conf,
                                                                                                 MapDBPersistenceUtil.createEnvironment(context) );
        return newksession;
    }

    /*private Collection<KnowledgePackage> buildKnowledgePackage(Resource resource,
                                                               ResourceType resourceType) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( resource,
                      resourceType );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors != null && errors.size() > 0 ) {
            for ( KnowledgeBuilderError error : errors ) {
                System.err.println( "Error: " + error.getMessage() );
            }
            Assert.fail( "KnowledgeBase did not build" );
        }
        Collection<KnowledgePackage> packages = kbuilder.getKnowledgePackages();
        return packages;
    }*/

}
