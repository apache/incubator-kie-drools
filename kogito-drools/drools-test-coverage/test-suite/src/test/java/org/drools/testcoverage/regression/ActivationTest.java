/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import org.drools.testcoverage.common.listener.TrackingAgendaEventListener;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class ActivationTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;
    private final KieSessionTestConfiguration kieSessionTestConfiguration;

    private Session ksession;

    private static final String DRL =
            "package org.drools;\n" +
                    "import org.drools.testcoverage.common.model.Cheese\n" +
                    "import org.drools.testcoverage.common.model.Person\n" +
                    " rule R1\n" +
                    "    salience 10\n" +
                    "    when\n" +
                    "        $c : Cheese( price == 10 )\n" +
                    "        $p : Person( )\n" +
                    "    then\n" +
                    "        modify($c) { setPrice( 5 ) }\n" +
                    "        modify($p) { setAge( 20 ) }\n" +
                    "end\n" +
                    "rule R2\n" +
                    "    when\n" +
                    "        $p : Person( )\n" +
                    "    then \n" +
                    "        // noop\n" +
                    "end\n";

    public ActivationTest(KieBaseTestConfiguration kieBaseTestConfiguration, KieSessionTestConfiguration kieSessionTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
        this.kieSessionTestConfiguration = kieSessionTestConfiguration;
    }

    @Before
    public void createKieSession() {
        ksession = getKieSessionForTest();
    }

    @Parameterized.Parameters(name = "{1}" + " (from " + "{0}" + ")")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseAndKieSessionConfigurations();
    }

    /**
     * Tests improper deactivation of already activated rule on the agenda. See
     * BZ 862325.
     */
    @Test
    public void noDormantCheckOnModifies() throws Exception {
        AgendaEventListener ael = mock(AgendaEventListener.class);
        ksession.addEventListener(ael);
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(KieServices.Factory.get().getCommands().newInsert(new Person("Bob", 19)));
        commands.add(KieServices.Factory.get().getCommands().newInsert(new Cheese("brie", 10)));
        commands.add(KieServices.Factory.get().getCommands().newFireAllRules());

        ksession.execute(KieServices.Factory.get().getCommands().newBatchExecution(commands, null));

        // both rules should fire exactly once
        verify(ael, times(2)).afterMatchFired(any(AfterMatchFiredEvent.class));
        // no cancellations should have happened
        verify(ael, never()).matchCancelled(any(MatchCancelledEvent.class));
    }

    private Session getKieSessionForTest() {
        return KieSessionUtil.getKieSessionAndBuildInstallModuleFromDrl(TestConstants.PACKAGE_REGRESSION,
                kieBaseTestConfiguration, kieSessionTestConfiguration, DRL);
    }
}
