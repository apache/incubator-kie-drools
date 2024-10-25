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
package org.drools.persistence.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.core.impl.RuleBaseFactory;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.mvel.CommonTestMethodBase;
import org.drools.mvel.compiler.Cheese;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.drools.persistence.util.DroolsPersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.DroolsPersistenceUtil.OPTIMISTIC_LOCKING;
import static org.drools.persistence.util.DroolsPersistenceUtil.PESSIMISTIC_LOCKING;
import static org.drools.persistence.util.DroolsPersistenceUtil.cleanUp;
import static org.drools.persistence.util.DroolsPersistenceUtil.createEnvironment;

@RunWith(Parameterized.class)
public class MoreBatchExecutionPersistenceTest extends CommonTestMethodBase {
	

    private KieSession ksession = null;

    private Map<String, Object> context;
    private boolean locking;
    
    @Parameters(name="{0}")
    public static Collection<Object[]> persistence() {
        Object[][] locking = new Object[][] { 
                { OPTIMISTIC_LOCKING }, 
                { PESSIMISTIC_LOCKING } 
                };
        return Arrays.asList(locking);
    };
    
    public MoreBatchExecutionPersistenceTest(String locking) { 
        this.locking = PESSIMISTIC_LOCKING.equals(locking);
    }
    
    @After
    public void cleanUpPersistence() throws Exception {
        disposeKSession();
        cleanUp(context);
        context = null;
    }
    
    public void disposeKSession() {
        if( ksession != null ) { 
            ksession.dispose();
            ksession = null;
        }
    }

    protected StatefulKnowledgeSession createKnowledgeSession(KieBase kbase) {
        if( context == null ) { 
            context = DroolsPersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        }
        KieSessionConfiguration ksconf = RuleBaseFactory.newKnowledgeSessionConfiguration();
        Environment env = createEnvironment(context);
        if( this.locking ) { 
            env.set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        }
        return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, ksconf, env);    
        
    }  
    
    @Test
    public void testFireAllRules() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("org/drools/mvel/integrationtests/drl/test_ImportFunctions.drl"), ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ksession = createKnowledgeSession(kbase);

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
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("org/drools/mvel/integrationtests/simple_query_test.drl"), ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ksession = createKnowledgeSession(kbase);
        
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
