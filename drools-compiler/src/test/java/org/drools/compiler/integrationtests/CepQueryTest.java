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

import java.util.concurrent.TimeUnit;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.internal.io.ResourceFactory;

/**
 * Tests queries using temporal operators on events from two entry points.
 */
public class CepQueryTest extends CommonTestMethodBase {

    private KieSession ksession;

    private SessionPseudoClock clock;
    
    private EntryPoint firstEntryPoint, secondEntryPoint;
    
    @Before
    public void prepare() {
        final String drl = "package org.drools.compiler.integrationtests\n" + 
                "import org.drools.compiler.integrationtests.CepQueryTest.TestEvent;\n" + 
                "declare TestEvent\n" + 
                "    @role( event )\n" +
                "end\n" + 
                "query EventsAfterZeroToNineSeconds\n" + 
                "    $event : TestEvent() from entry-point FirstStream\n" + 
                "    $result : TestEvent( this after [0s, 9s] $event) from entry-point SecondStream\n" + 
                "end\n";
        
        final KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieModuleModel module = ks.newKieModuleModel();

        KieBaseModel baseModel = module.newKieBaseModel("defaultKBase")
                .setDefault(true)
                .setEventProcessingMode(EventProcessingOption.STREAM);
        baseModel.newKieSessionModel("defaultKSession")
                .setDefault(true)
                .setClockType(ClockTypeOption.get("pseudo"));

        kfs.writeKModuleXML(module.toXML());
        kfs.write(ResourceFactory.newByteArrayResource( drl.getBytes() ).setTargetPath("defaultPkg/query.drl") );

        assertTrue(ks.newKieBuilder(kfs).buildAll().getResults().getMessages().isEmpty());
        ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
        
        clock = ksession.getSessionClock();
        firstEntryPoint = ksession.getEntryPoint("FirstStream");
        secondEntryPoint = ksession.getEntryPoint("SecondStream");        
    }

    @After
    public void cleanup() {
        if (ksession != null) {
            ksession.dispose();
        }
    }

    private void eventsInitialization() {
        secondEntryPoint.insert(new TestEvent("minusOne"));
        clock.advanceTime(5, TimeUnit.SECONDS);

        firstEntryPoint.insert(new TestEvent("zero"));
        secondEntryPoint.insert(new TestEvent("one"));
//        clock.advanceTime(10, TimeUnit.SECONDS);
//
//        secondEntryPoint.insert(new TestEvent("two"));
//        clock.advanceTime(10, TimeUnit.SECONDS);
//
//        secondEntryPoint.insert(new TestEvent("three"));
//        ksession.fireAllRules();
    }

    /**
     * Tests query using temporal operator 'after' on events from two entry points.
     */
    @Test//(timeout=10000)
    public void testQueryWithAfter() {
        this.eventsInitialization();
        QueryResults results = ksession.getQueryResults("EventsAfterZeroToNineSeconds");

        assertEquals("Unexpected query result length", 1, results.size());
        assertEquals("Unexpected query result content", 
                "one", ((TestEvent) results.iterator().next().get("$result")).getName());
    }

    /**
     * Simple event used in the test.
     */
    public static class TestEvent {
        private final String name;
        
        public TestEvent(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }        
        @Override
        public String toString() {
            return "TestEvent["+name+"]";
        }
    }
}