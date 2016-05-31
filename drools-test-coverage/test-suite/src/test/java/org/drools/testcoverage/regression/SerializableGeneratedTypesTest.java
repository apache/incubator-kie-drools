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

import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;

/**
 * Test to verify BRMS-360 (Generated types should be serializable) is fixed
 */
@RunWith(Parameterized.class)
public class SerializableGeneratedTypesTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public SerializableGeneratedTypesTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    @Test
    public void testSerializability() throws Exception {
        final Resource drlResource =
                KieServices.Factory.get().getResources().newClassPathResource("serializableGeneratedTypesTest.drl", getClass());
        final KieBase kieBase = KieBaseUtil.getKieBaseAndBuildInstallModule(TestConstants.PACKAGE_REGRESSION,
                kieBaseTestConfiguration, drlResource);
        final KieSession session = kieBase.newKieSession();

        final FactType testEventType = session.getKieBase().getFactType(TestConstants.PACKAGE_REGRESSION, "TestEvent");
        for (int i = 0; i < 10; i++) {
            final Object testEvent = testEventType.newInstance();
            testEventType.set(testEvent, "id", "id" + i);
            final EntryPoint mainStream = session.getEntryPoint("test");
            mainStream.insert(testEvent);
            session.fireAllRules();
        }
    }

}
