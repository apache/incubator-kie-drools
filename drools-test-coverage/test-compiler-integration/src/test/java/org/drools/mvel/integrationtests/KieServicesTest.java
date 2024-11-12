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

import java.util.stream.Stream;

import org.drools.compiler.kie.builder.impl.KieServicesImpl;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class KieServicesTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }
    
    private KieServices ks;

    @BeforeEach
    public void init() {
        ks = KieServices.Factory.get();
        (( KieServicesImpl ) ks).nullKieClasspathContainer();
        ((KieServicesImpl) ks).nullAllContainerIds();
    }
    
    @AfterEach
    public void shutdown() {
        ((KieServicesImpl) ks).nullKieClasspathContainer(); 
        ((KieServicesImpl) ks).nullAllContainerIds();
    }
    
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testGetKieClasspathIDs(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String myId = "myId";
        
        KieContainer c1 = ks.getKieClasspathContainer(myId);

        assertThat(ks.getKieClasspathContainer()).isEqualTo(c1);
        assertThat(ks.getKieClasspathContainer(myId)).isEqualTo(c1);
        try {
            ks.getKieClasspathContainer("invalid");
            fail("this is not the containerId for the global singleton.");
        } catch (IllegalStateException is) {
            // ok.
        }
    }
    
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNewKieClasspathIDs(KieBaseTestConfiguration kieBaseTestConfiguration) {
        KieContainer c1 = ks.newKieClasspathContainer("id1");
        KieContainer c2 = ks.newKieClasspathContainer("id2");
        try {
            ks.newKieClasspathContainer("id2");
            fail("should not allow repeated container IDs.");
        } catch (IllegalStateException is) {
            // ok.
        }
    }
    
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNewKieContainerIDs(KieBaseTestConfiguration kieBaseTestConfiguration) {
        ReleaseId releaseId = ks.newReleaseId("org.kie", "test-delete", "1.0.0");
        KieModule km = KieUtil.getKieModuleFromDrls(releaseId, kieBaseTestConfiguration, createDRL("ruleA"));

        KieContainer c1 = ks.newKieContainer("id1", releaseId);
        KieContainer c2 = ks.newKieClasspathContainer("id2");
        try {
            ks.newKieContainer("id2", releaseId);
            fail("should not allow repeated container IDs.");
        } catch (IllegalStateException is) {
            // ok.
        }
        try {
            ks.newKieClasspathContainer("id1");
            fail("should not allow repeated container IDs.");
        } catch (IllegalStateException is) {
            // ok.
        }
    }
    
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testDisposeClearTheIDReference(KieBaseTestConfiguration kieBaseTestConfiguration) {
        ReleaseId releaseId = ks.newReleaseId("org.kie", "test-delete", "1.0.0");
        KieModule km = KieUtil.getKieModuleFromDrls(releaseId, kieBaseTestConfiguration, createDRL("ruleA"));

        KieContainer c1 = ks.newKieContainer("id1", releaseId);
        try {
            ks.newKieClasspathContainer("id1");
            fail("should not allow repeated container IDs.");
        } catch (IllegalStateException is) {
            // ok.
        }
        
        c1.dispose();
        
        ks.newKieClasspathContainer("id1"); // now OK.
    }

    private String createDRL(String ruleName) {
        return "package org.kie.test\n" +
               "global java.util.List list\n" +
               "rule " + ruleName + "\n" +
               "when\n" +
               "then\n" +
               "list.add( drools.getRule().getName() );\n" +
               "end\n";
    }
}
