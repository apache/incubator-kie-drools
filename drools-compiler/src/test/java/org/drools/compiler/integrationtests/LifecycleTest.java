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

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.drools.compiler.StockTick;
import org.drools.core.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Before;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.runtime.conf.ClockTypeOption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

/**
 * Tests updating events using API.
 */
public class LifecycleTest {

    private KieSession kieSession;
    
    @Before
    public void initSession() {
        String drlString = "package org.jboss.brms\n" + 
                "import org.drools.compiler.StockTick\n" + 
                "declare StockTick\n" + 
                "    @role( event )\n" + 
                "    @expires( 4s )\n" + 
                "end\n" + 
                "rule \"TestEventReceived\"\n" + 
                "    when\n" + 
                "        $event : StockTick() over window:time(4s) from entry-point EventStream\n" + 
                "    then\n" + 
                "        // do something;\n" + 
                "end";
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        final Resource drl = ks.getResources().newByteArrayResource( drlString.getBytes() ).setTargetPath("org/jboss/brms/lifecycle.drl");
        kfs.write(drl);

        KieModuleModel kmoduleModel = ks.newKieModuleModel();
        kmoduleModel.newKieBaseModel("defaultKBase")
                .addPackage("*")
                .setEventProcessingMode(EventProcessingOption.STREAM)
                .newKieSessionModel("defaultKieSession")
                .setDefault(true)
                .setClockType(ClockTypeOption.get("pseudo"));
        
        kfs.writeKModuleXML(kmoduleModel.toXML());
        
        KieBuilder builder = ks.newKieBuilder(kfs).buildAll();
        assertEquals(0, builder.getResults().getMessages().size());

        this.kieSession = ks.newKieContainer(ks.getRepository()
                .getDefaultReleaseId()).newKieSession();        
    }

    @After
    public void cleanup() {
        if (this.kieSession != null) {
            this.kieSession.dispose();
        }
    }
            
    @Test
    public void testExpires() throws Exception {
        EntryPoint entryPoint = kieSession.getEntryPoint("EventStream");

        StockTick event = new StockTick();
        FactHandle handle = entryPoint.insert(event);
        assertTrue(entryPoint.getFactHandles().contains(handle));
        kieSession.fireAllRules();

        assertTrue(entryPoint.getFactHandles().contains(handle));
        advanceTime(5, TimeUnit.SECONDS);
        kieSession.fireAllRules();

        assertFalse(entryPoint.getFactHandles().contains(handle));
    }
  
    private void advanceTime(long amount, TimeUnit unit) throws InterruptedException {
        if (kieSession.getSessionClock() instanceof SessionPseudoClock) {
            SessionPseudoClock clock = kieSession.getSessionClock();
            clock.advanceTime(amount, unit);
        } else {
            Thread.sleep(TimeUnit.MILLISECONDS.convert(amount, unit));
        }
    }
}
