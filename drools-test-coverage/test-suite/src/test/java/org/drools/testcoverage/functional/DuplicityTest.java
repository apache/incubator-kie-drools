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
package org.drools.testcoverage.functional;

import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.fail;

/**
 * Testing of duplicities in rule files.
 * https://bugzilla.redhat.com/show_bug.cgi?id=724753
 */
@RunWith(Parameterized.class)
public class DuplicityTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DuplicityTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    @Test
    public void testTwoRulesWithSameNameInOneFile() {
        try {
            final Resource resource =
                    KieServices.Factory.get().getResources().newClassPathResource("rule-name.drl", getClass());
            KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, true, resource);
            fail("Builder should have had errors, two rules of the same name are not allowed in one file together!");
        } catch (AssertionError e) {
            // expected
            LoggerFactory.getLogger(getClass()).info("", e);
        }
    }
}
