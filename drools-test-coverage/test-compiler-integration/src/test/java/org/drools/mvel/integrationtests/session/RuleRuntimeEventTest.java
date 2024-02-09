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
package org.drools.mvel.integrationtests.session;

import java.util.Collection;

import org.drools.mvel.compiler.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class RuleRuntimeEventTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public RuleRuntimeEventTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testEventModel() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_EventModel.drl");
        KieSession wm = kbase.newKieSession();

        final RuleRuntimeEventListener wmel = mock(RuleRuntimeEventListener.class);
        wm.addEventListener(wmel);

        final Cheese stilton = new Cheese("stilton", 15);

        final FactHandle stiltonHandle = wm.insert(stilton);

        final ArgumentCaptor<ObjectInsertedEvent> oic = ArgumentCaptor.forClass(org.kie.api.event.rule.ObjectInsertedEvent.class);
        verify(wmel).objectInserted(oic.capture());
        assertThat(oic.getValue().getFactHandle()).isSameAs(stiltonHandle);

        wm.update(stiltonHandle, stilton);
        final ArgumentCaptor<org.kie.api.event.rule.ObjectUpdatedEvent> ouc = ArgumentCaptor.forClass(org.kie.api.event.rule.ObjectUpdatedEvent.class);
        verify(wmel).objectUpdated(ouc.capture());
        assertThat(ouc.getValue().getFactHandle()).isSameAs(stiltonHandle);

        wm.delete(stiltonHandle);
        final ArgumentCaptor<ObjectDeletedEvent> orc = ArgumentCaptor.forClass(ObjectDeletedEvent.class);
        verify(wmel).objectDeleted(orc.capture());
        assertThat(orc.getValue().getFactHandle()).isSameAs(stiltonHandle);

    }

}
