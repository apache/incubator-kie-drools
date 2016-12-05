/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.regression;

import org.assertj.core.api.Assertions;
import org.drools.core.time.SessionPseudoClock;
import org.drools.testcoverage.common.model.Message;
import org.drools.testcoverage.common.model.MessageEvent;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * A test performing logical insert inside rule on an event, then advances time
 * and checks that event has expired and logically inserted fact has been retracted.
 */
@RunWith(Parameterized.class)
public class InsertLogicalEventExpirationTest {

    private KieSession kieSession;

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public InsertLogicalEventExpirationTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseStreamConfigurations();
    }

    @Before
    public void setup() {
        final Resource drlResource =
                KieServices.Factory.get().getResources().newClassPathResource("logicalInsertEventExpiration.drl", getClass());
        final KieBase kieBase = KieBaseUtil.getKieBaseAndBuildInstallModule(TestConstants.PACKAGE_REGRESSION,
                kieBaseTestConfiguration, drlResource);

        final KieSessionConfiguration ksconf = KieSessionUtil.getKieSessionConfigurationWithClock(ClockTypeOption.get("pseudo"), null);
        kieSession = kieBase.newKieSession(ksconf, null);
    }

    @After
    public void cleanup() {
        if (this.kieSession != null) {
            this.kieSession.dispose();
        }
    }

    @Test
    public void testExpireLogicallyInsertedEvent() {
        kieSession.insert(new MessageEvent(MessageEvent.Type.received, new Message("test message")));

        // Fact count before firing = 1, one inserted
        Assertions.assertThat(kieSession.getFactCount()).isEqualTo((long) 1);

        kieSession.fireAllRules();

        // Fact count after firing = 2, one inserted, one logicalInserted
        Assertions.assertThat(kieSession.getFactCount()).isEqualTo((long) 2);

        SessionPseudoClock clock = kieSession.getSessionClock();
        clock.advanceTime(2, TimeUnit.SECONDS);
        kieSession.fireAllRules();

        // Fact count after expiration = 0, expired and retracted
        Assertions.assertThat(kieSession.getFactCount()).isEqualTo((long) 0);
    }
}
