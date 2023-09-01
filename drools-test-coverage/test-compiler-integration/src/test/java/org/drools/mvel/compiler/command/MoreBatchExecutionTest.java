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
package org.drools.mvel.compiler.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.mvel.compiler.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.internal.command.CommandFactory;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class MoreBatchExecutionTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public MoreBatchExecutionTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    private KieSession ksession = null;
    
    @After
    public void disposeKSession() {
        if( ksession != null ) { 
            ksession.dispose();
            ksession = null;
        }
    }
    
    @Test
    public void testFireAllRules() {

        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "org/drools/mvel/integrationtests/drl/test_ImportFunctions.drl");
        ksession = kbase.newKieSession();

        final Cheese cheese = new Cheese("stilton", 15);
        ksession.insert(cheese);
        List<?> list = new ArrayList();
        ksession.setGlobal("list", list);

        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newFireAllRules("fired"));
        Command<?> cmds = CommandFactory.newBatchExecution(commands);
        ExecutionResults result = (ExecutionResults) ksession.execute(cmds);
        assertThat(result).as("Batch execution result is null!").isNotNull();

        Object firedObject = result.getValue("fired");
        assertThat(firedObject instanceof Integer).as("Retrieved object is null or incorrect!").isTrue();
        assertThat(firedObject).isEqualTo(4);

        list = (List<?>) ksession.getGlobal("list");
        assertThat(list.size()).isEqualTo(4);

        assertThat(list.get(0)).isEqualTo("rule1");
        assertThat(list.get(1)).isEqualTo("rule2");
        assertThat(list.get(2)).isEqualTo("rule3");
        assertThat(list.get(3)).isEqualTo("rule4");
    }

    @Test
    public void testQuery() {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "org/drools/mvel/integrationtests/simple_query_test.drl");
        KieSession ksession = kbase.newKieSession();
        
        ksession.insert( new Cheese( "stinky", 5 ) );
        ksession.insert( new Cheese( "smelly", 7 ) );
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newQuery("numStinkyCheeses", "simple query"));
        Command<?> cmds = CommandFactory.newBatchExecution(commands);
        ExecutionResults result = (ExecutionResults) ksession.execute(cmds);
        assertThat(result).as("Batch execution result is null!").isNotNull();

        Object queryResultsObject = result.getValue("numStinkyCheeses");
        assertThat(queryResultsObject instanceof QueryResults).as("Retrieved object is null or incorrect!").isTrue();

        assertThat(((QueryResults) queryResultsObject).size()).isEqualTo(1);
    }
}
