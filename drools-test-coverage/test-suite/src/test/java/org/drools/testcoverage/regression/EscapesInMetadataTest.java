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
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test to verify BRMS-312 (Allow escaping characters in metadata value) is
 * fixed
 */
@RunWith(Parameterized.class)
public class EscapesInMetadataTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(EscapesInMetadataTest.class);

    private static final String RULE_NAME = "hello world";
    private static final String RULE_KEY = "output";
    private static final String RULE_VALUE = "Hello world!";

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public EscapesInMetadataTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    @Test
    public void testMetadataEscapes() {
        final String rule = "package " + TestConstants.PACKAGE_REGRESSION + "\n"
                + " rule \"" + RULE_NAME + "\"\n"
                + " @" + RULE_KEY + "(\"\\\""+ RULE_VALUE + "\\\"\")\n"
                + " when\n"
                + " then\n"
                + "     System.out.println(\"Hello world!\");\n"
                + " end";

        final KieBase kieBase = KieBaseUtil.getKieBaseAndBuildInstallModuleFromDrl(TestConstants.PACKAGE_REGRESSION,
                kieBaseTestConfiguration, rule);
        final Map<String, Object> metadata = kieBase.getRule(TestConstants.PACKAGE_REGRESSION, RULE_NAME).getMetaData();
        LOGGER.debug(rule);

        Assertions.assertThat(metadata.containsKey(RULE_KEY)).isTrue();
        Assertions.assertThat(metadata.get(RULE_KEY)).isEqualTo("\"" + RULE_VALUE + "\"");
    }

}
