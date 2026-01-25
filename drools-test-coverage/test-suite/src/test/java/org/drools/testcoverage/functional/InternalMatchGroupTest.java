/*
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
package org.drools.testcoverage.functional;

import java.util.stream.Stream;

import org.drools.core.event.TrackingAgendaEventListener;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class InternalMatchGroupTest {

    private TrackingAgendaEventListener listener;

	public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseConfigurations().stream();
    }

    /**
     * Only one rule from activation group fires. 
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void basicTestActivationGroup(KieBaseTestConfiguration kieBaseTestConfiguration) {
        prepareKSession(kieBaseTestConfiguration, "basicActivationGroup");

        assertThat(listener.getAfterMatchFired()).contains("basic2").doesNotContain("basic1", "basic3");
    }
    
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void recursiveTestActivationGroup(KieBaseTestConfiguration kieBaseTestConfiguration) {
        prepareKSession(kieBaseTestConfiguration, "recursiveActivationGroup");

        assertThat(listener.getAfterMatchFired()).contains("simplyRecursive2", "simplyRecursive3").doesNotContain("simplyRecursive1");
    }
    
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testActivationGroupWithDefaultSalience(KieBaseTestConfiguration kieBaseTestConfiguration) {
        prepareKSession(kieBaseTestConfiguration, "defaultSalienceActivationGroup");
        
        assertThat(listener.getAfterMatchFired()).hasSize(1);
    }
    
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testActivationGroupRecursivelyWithDefaultSalience(KieBaseTestConfiguration kieBaseTestConfiguration) {
        prepareKSession(kieBaseTestConfiguration, "defaultSalienceWithRecursion");
        
        assertThat(listener.getAfterMatchFired()).hasSize(2);
    }
    
    private void prepareKSession(KieBaseTestConfiguration kieBaseTestConfiguration, String startingRule) {
        listener = new TrackingAgendaEventListener();

        final KieSession ksession = getKieBaseForTest(kieBaseTestConfiguration).newKieSession();
        try {
            ksession.addEventListener(listener);

            ksession.insert(startingRule);
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }

    private KieBase getKieBaseForTest(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final Resource drlResource =
                KieServices.Factory.get().getResources().newClassPathResource("activation-group.drl", getClass());
        return KieBaseUtil.getKieBaseFromKieModuleFromResources(TestConstants.PACKAGE_FUNCTIONAL,
                                                                kieBaseTestConfiguration, drlResource);
    }

}
