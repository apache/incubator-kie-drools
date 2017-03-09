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

import java.util.concurrent.TimeUnit;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QueryCepTest {
    
    private KieSession ksession;
    
    private SessionPseudoClock clock;
    
    private EntryPoint firstEntryPoint, secondEntryPoint;

    @Before
    public void prepare() {
        String drl = "package org.drools.compiler.integrationtests\n" +
                "import " + TestEvent.class.getCanonicalName() + "\n" +
                "declare TestEvent\n" +
                "    @role( event )\n" + 
                "end\n" + 
                "query EventsFromStream\n" + 
                "    $event : TestEvent() from entry-point FirstStream\n" + 
                "end\n" + 
                "query ZeroToNineteenSeconds\n" + 
                "    $event : TestEvent() from entry-point FirstStream\n" + 
                "    $result : TestEvent ( this after [0s, 19s] $event) from entry-point SecondStream\n" + 
                "end\n";
        
        final KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieModuleModel kmodule = ks.newKieModuleModel();

        KieBaseModel baseModel = kmodule.newKieBaseModel("defaultKBase")
                .setDefault(true)
                .setEventProcessingMode(EventProcessingOption.STREAM);
        baseModel.newKieSessionModel("defaultKSession")
                .setDefault(true)
                .setClockType(ClockTypeOption.get("pseudo"));

        kfs.writeKModuleXML(kmodule.toXML());
        kfs.write( ResourceFactory.newByteArrayResource(drl.getBytes())
                                  .setTargetPath("org/drools/compiler/integrationtests/queries.drl") );

        assertTrue(ks.newKieBuilder(kfs).buildAll().getResults().getMessages().isEmpty());
        ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
        
        firstEntryPoint = ksession.getEntryPoint("FirstStream");
        secondEntryPoint = ksession.getEntryPoint("SecondStream"); 
        clock = ksession.getSessionClock();
    }

    @Test
    public void noResultTest() {
        QueryResults results = ksession.getQueryResults("EventsFromStream");
        assertEquals(0, results.size());
    }
    
    @Test
    public void withResultTest() {
        secondEntryPoint.insert(new TestEvent("minusOne"));
        clock.advanceTime(5, TimeUnit.SECONDS);

        firstEntryPoint.insert(new TestEvent("zero"));
        secondEntryPoint.insert(new TestEvent("one"));
        clock.advanceTime(10, TimeUnit.SECONDS);

        secondEntryPoint.insert(new TestEvent("two"));
        clock.advanceTime(5, TimeUnit.SECONDS);

        secondEntryPoint.insert(new TestEvent("three"));
        QueryResults results = ksession.getQueryResults("ZeroToNineteenSeconds");
        
        assertEquals(1, results.size());
    }
    
    @Test
    public void withNoResultTest() {
        secondEntryPoint.insert(new TestEvent("minusOne"));
        clock.advanceTime(5, TimeUnit.SECONDS);

        firstEntryPoint.insert(new TestEvent("zero"));
        secondEntryPoint.insert(new TestEvent("one"));
        clock.advanceTime(10, TimeUnit.SECONDS);

        secondEntryPoint.insert(new TestEvent("two"));
        // the following expires event "zero" and "one", causing the query to no longer match
        clock.advanceTime(10, TimeUnit.SECONDS); 

        secondEntryPoint.insert(new TestEvent("three"));
        QueryResults results = ksession.getQueryResults("ZeroToNineteenSeconds");
        
        assertEquals(0, results.size());
    }
    
    @After
    public void cleanup() {
        
        if (ksession != null) {
            ksession.dispose();
        }
    }
    
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
            return "TestEvent[" + name + "]";
        }
    }
}
