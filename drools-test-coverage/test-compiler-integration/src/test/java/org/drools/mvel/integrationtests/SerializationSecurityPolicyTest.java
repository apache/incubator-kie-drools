/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel.integrationtests;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Policy;
import java.util.ArrayList;
import java.util.Collection;

import org.drools.mvel.CommonTestMethodBase;
import org.drools.core.impl.InternalKnowledgeBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class SerializationSecurityPolicyTest extends CommonTestMethodBase {

    @Before
    public void init() {
        final String policy = SerializationSecurityPolicyTest.class.getResource("serialization-rules.policy").getFile();
        System.setProperty("java.security.policy", policy);
        System.setProperty("kie.security.policy", policy);
        Policy.getPolicy().refresh();
        System.setSecurityManager(new SecurityManager());
    }

    @After
    public void close() {
        System.setSecurityManager(null);
        System.clearProperty("java.security.policy");
        System.clearProperty("kie.security.policy");
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
     // kpackage serialization is not supported. But leave it for standard-drl.
        final String rule =
                " rule R " +
                " when " +
                " then " +
                "     System.out.println(\"consequence!\"); " +
                " end";

        final KieServices kieServices = KieServices.get();
        final Resource drlResource = kieServices.getResources().newByteArrayResource(
                rule.getBytes(StandardCharsets.UTF_8.name()), StandardCharsets.UTF_8.name());
        drlResource.setResourceType(ResourceType.DRL);
        drlResource.setTargetPath("test.drl");
        final KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(drlResource);
        final KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        final KieBase kieBase = kieServices.newKieContainer(kieBuilder.getKieModule().getReleaseId()).getKieBase();

        final Collection<KiePackage> kpkgs = kieBase.getKiePackages();
        final Collection<KiePackage> newCollection = new ArrayList<>();
        for (KiePackage kpkg : kpkgs) {
            kpkg = SerializationHelper.serializeObject(kpkg);
            newCollection.add(kpkg);
        }
        ((InternalKnowledgeBase) kieBase).addPackages(newCollection);

        final KieSession kieSession = kieBase.newKieSession();
        assertThat(kieSession.fireAllRules()).isEqualTo(1);
    }
}