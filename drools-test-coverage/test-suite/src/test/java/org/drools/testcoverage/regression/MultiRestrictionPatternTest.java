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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.drools.testcoverage.common.KieSessionTest;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.command.Command;
import org.kie.api.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.testcoverage.common.util.KieUtil.getCommands;

/**
 * Test to verify BRMS-364 (multi-restriction pattern throws UnsupportedOpEx) is
 * fixed
 */
@DisabledIfSystemProperty(named = "drools.drl.antlr4.parser.enabled", matches = "true")
public class MultiRestrictionPatternTest extends KieSessionTest {
	
    private static final String DRL_FILE = "BRMS-364.drl";

    public static Stream<Arguments> parameters() {
        return TestParametersUtil2.getKieBaseAndStatefulKieSessionConfigurations().stream();
    }

    @ParameterizedTest(name = "{1}" + " (from " + "{0}" + ")")
	@MethodSource("parameters")
    public void multiRestriction1(KieBaseTestConfiguration kieBaseTestConfiguration,
            KieSessionTestConfiguration kieSessionTestConfiguration) throws Exception {
    	createKieSession(kieBaseTestConfiguration, kieSessionTestConfiguration);
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(getCommands().newInsert(new Person("multi")));
        commands.add(getCommands().newFireAllRules());

        session.execute(getCommands().newBatchExecution(commands, null));

        assertThat(firedRules.isRuleFired("or1")).isTrue();
    }

    @ParameterizedTest(name = "{1}" + " (from " + "{0}" + ")")
	@MethodSource("parameters")
    public void multiRestriction2(KieBaseTestConfiguration kieBaseTestConfiguration,
            KieSessionTestConfiguration kieSessionTestConfiguration) throws Exception {
    	createKieSession(kieBaseTestConfiguration, kieSessionTestConfiguration);
    	List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(getCommands().newInsert(new Person("MULTIRESTRICTION")));
        commands.add(getCommands().newFireAllRules());

        session.execute(getCommands().newBatchExecution(commands, null));

        assertThat(firedRules.isRuleFired("or2")).isTrue();
    }

    @ParameterizedTest(name = "{1}" + " (from " + "{0}" + ")")
	@MethodSource("parameters")
    public void multiRestriction3(KieBaseTestConfiguration kieBaseTestConfiguration,
            KieSessionTestConfiguration kieSessionTestConfiguration) throws Exception {
    	createKieSession(kieBaseTestConfiguration, kieSessionTestConfiguration);
    	List<Command<?>> commands = new ArrayList<Command<?>>();
        Person p = new Person();
        p.setId(3);
        commands.add(getCommands().newInsert(p));
        commands.add(getCommands().newFireAllRules());

        session.execute(getCommands().newBatchExecution(commands, null));

        assertThat(firedRules.isRuleFired("and")).isTrue();
    }

    @Override
    protected Resource[] createResources() {
        return KieUtil.createResources(DRL_FILE, MultiRestrictionPatternTest.class);
    }
}
