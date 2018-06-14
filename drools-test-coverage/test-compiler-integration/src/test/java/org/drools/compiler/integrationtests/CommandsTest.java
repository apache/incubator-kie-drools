/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CommandsTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public CommandsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test
    public void testSessionTimeCommands() {
        final String drl =
            "package org.drools.compiler.integrationtests \n" +
            "import " + Cheese.class.getCanonicalName() + " \n" +
            "rule StringRule \n" +
            "when \n" +
            "    $c : Cheese() \n" +
            "then \n" +
            "end \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("cep-esp-test", kieBaseTestConfiguration, drl);
        final KieSession kSession = kbase.newKieSession(KieSessionTestConfiguration.STATEFUL_PSEUDO.getKieSessionConfiguration(), null);
        try {
            final KieCommands kieCommands = KieServices.get().getCommands();
            assertEquals(0L, (long) kSession.execute(kieCommands.newGetSessionTime()));
            assertEquals(2000L, (long) kSession.execute(kieCommands.newAdvanceSessionTime(2, TimeUnit.SECONDS)));
            assertEquals(2000L, (long) kSession.execute(kieCommands.newGetSessionTime()));
        } finally {
            kSession.dispose();
        }
    }
}
