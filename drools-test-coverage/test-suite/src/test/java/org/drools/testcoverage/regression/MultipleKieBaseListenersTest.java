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

import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.event.kiebase.DefaultKieBaseEventListener;
import org.kie.api.event.kiebase.KieBaseEventListener;

@RunWith(Parameterized.class)
public class MultipleKieBaseListenersTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public MultipleKieBaseListenersTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    @Test
    public void testKnowledgeBaseEventSupportLeak() throws Exception {
        final KieBase kieBase = KieBaseUtil.getKieBaseAndBuildInstallModuleFromDrl(TestConstants.PACKAGE_REGRESSION,
                kieBaseTestConfiguration, "");

        KieBaseEventListener listener = new DefaultKieBaseEventListener();

        kieBase.addEventListener(listener);
        kieBase.addEventListener(listener);
        kieBase.addEventListener(listener);

        Assertions.assertThat(kieBase.getKieBaseEventListeners().size()).isEqualTo(1);

        kieBase.removeEventListener(listener);

        Assertions.assertThat(kieBase.getKieBaseEventListeners()).isEmpty();
    }

}