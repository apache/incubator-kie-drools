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
import org.drools.testcoverage.common.model.Message;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

/**
 * Tests problems with large numbers to String conversion. See DROOLS-167.
 */
@RunWith(Parameterized.class)
public class InaccurateComparisonTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public InaccurateComparisonTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    @Test
    public void testStringCoercionComparison() {
        final String rule = "package " + TestConstants.PACKAGE_REGRESSION + "\n" +
                " import " + TestConstants.PACKAGE_TESTCOVERAGE_MODEL + ".Message;\n" +
                " rule \"string coercion\" \n" +
                " when\n" +
                "     m : Message( message < \"90201304122000000000000017\" )\n" +
                " then \n" +
                " end";

        final KieBase kieBase = KieBaseUtil.getKieBaseAndBuildInstallModuleFromDrl(TestConstants.PACKAGE_REGRESSION,
                kieBaseTestConfiguration, rule);
        KieSession ksession = kieBase.newKieSession();

        ksession.insert(new Message("90201304122000000000000015"));
        Assertions.assertThat(ksession.fireAllRules()).isEqualTo(1);
    }
}
