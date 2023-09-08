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
package org.drools.testcoverage.regression;

import org.drools.testcoverage.common.KieSessionTest;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.*;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.kie.api.command.Command;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.drools.testcoverage.common.util.KieUtil.getCommands;
import static org.mockito.Mockito.*;

public class InternalMatchTest extends KieSessionTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(InternalMatchTest.class);

    private static final String DRL =
            "package org.drools;\n" +
            "import org.drools.testcoverage.common.model.Cheese;\n" +
            "import org.drools.testcoverage.common.model.Person;\n" +
            "global org.slf4j.Logger LOGGER;\n" +
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
            "        LOGGER.debug(\"noop\");\n" +
            "end\n";

    public InternalMatchTest(final KieBaseTestConfiguration kieBaseTestConfiguration,
                             final KieSessionTestConfiguration kieSessionTestConfiguration) {
        super(kieBaseTestConfiguration, kieSessionTestConfiguration);
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
        session.addEventListener(ael);
        session.setGlobal("LOGGER", LOGGER);
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(getCommands().newInsert(new Person("Bob", 19)));
        commands.add(getCommands().newInsert(new Cheese("brie", 10)));
        commands.add(getCommands().newFireAllRules());

        session.execute(getCommands().newBatchExecution(commands, null));

        // both rules should fire exactly once
        verify(ael, times(2)).afterMatchFired(any(AfterMatchFiredEvent.class));
        // no cancellations should have happened
        verify(ael, never()).matchCancelled(any(MatchCancelledEvent.class));
    }

    @Override
    protected Resource[] createResources() {
        return KieUtil.createResources(DRL);
    }
}
