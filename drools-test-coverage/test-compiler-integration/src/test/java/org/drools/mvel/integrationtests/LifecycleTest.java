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

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.drools.mvel.compiler.StockTick;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.time.SessionPseudoClock;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests updating events using API.
 */
public class LifecycleTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseStreamConfigurations(true).stream();
    }

    private KieSession kieSession;
    
    public void initSession(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String drlString = "package org.jboss.brms\n" + 
                "import org.drools.mvel.compiler.StockTick\n" +
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
                .setClockType(ClockTypeOption.PSEUDO);
        
        kfs.writeKModuleXML(kmoduleModel.toXML());
        
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        assertThat(kieBuilder.getResults().getMessages().size()).isEqualTo(0);

        this.kieSession = ks.newKieContainer(ks.getRepository()
                .getDefaultReleaseId()).newKieSession();
    }

    @AfterEach
    public void cleanup() {
        if (this.kieSession != null) {
            this.kieSession.dispose();
        }
    }
            
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testExpires(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        initSession(kieBaseTestConfiguration);
        EntryPoint entryPoint = kieSession.getEntryPoint("EventStream");

        StockTick event = new StockTick();
        FactHandle handle = entryPoint.insert(event);
        assertThat(entryPoint.getFactHandles().contains(handle)).isTrue();
        kieSession.fireAllRules();

        assertThat(entryPoint.getFactHandles().contains(handle)).isTrue();
        advanceTime(5, TimeUnit.SECONDS);
        kieSession.fireAllRules();

        assertThat(entryPoint.getFactHandles().contains(handle)).isFalse();
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
