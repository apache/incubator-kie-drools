/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.quarkus.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieRuntimeBuilder;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.SessionPseudoClock;

@Path("/test")
public class TestableResource {

    @Inject
    KieRuntimeBuilder runtimeBuilder;

    @GET
    @Path("testCepEvaluation")
    public Response testCepEvaluation() {
        KieSession ksession = runtimeBuilder.newKieSession("cepKS");

        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert(new StockTick("DROO"));
        clock.advanceTime(6, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));
        
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        clock.advanceTime(4, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));

        assertThat(ksession.fireAllRules()).isEqualTo(0);
     
        return Response.ok().build();
    }
    
    @GET
    @Path("testFireUntiHalt")
    public Response testFireUntiHalt() throws InterruptedException {
        KieSession ksession = runtimeBuilder.newKieSession("probeKS");

        new Thread(ksession::fireUntilHalt).start();
        final ProbeCounter pc = new ProbeCounter();

        ksession.insert(pc);
        for (int i = 0; i < 10; i++) {
            ksession.insert(new ProbeEvent(i));
        }

        synchronized (pc) {
            if (pc.getTotal() < 10) {
                try {
                    pc.wait();
                } catch (InterruptedException e) {
                    fail("couldn't lock in test ProbeCounter", e);
                    throw e; // for SonarCloud
                }
            }
        }

        assertThat(pc.getTotal()).isEqualTo(10);
        
        return Response.ok().build();
    }
    
    @GET
    @Path("testAllPkgsKBase")
    public Response testAllPkgsKBase() {
        KieBase kBase = runtimeBuilder.getKieBase("allKB");

        List<String> pkgNames = kBase.getKiePackages().stream().map(KiePackage::getName).collect(Collectors.toList());
        assertThat(pkgNames)
            .hasSize(5)
            .contains("org.drools.quarkus.test","org.drools.drl","org.drools.dtable","org.drools.cep","org.drools.probe");
        
        return Response.ok().build();
    }
}