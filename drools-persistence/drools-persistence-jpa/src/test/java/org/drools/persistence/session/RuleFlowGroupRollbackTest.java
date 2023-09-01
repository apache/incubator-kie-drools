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
package org.drools.persistence.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.commands.impl.CommandBasedStatefulKnowledgeSessionImpl;
import org.drools.core.common.InternalAgenda;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.io.ClassPathResource;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.RegistryContext;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.drools.persistence.util.DroolsPersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.DroolsPersistenceUtil.OPTIMISTIC_LOCKING;
import static org.drools.persistence.util.DroolsPersistenceUtil.PESSIMISTIC_LOCKING;
import static org.drools.persistence.util.DroolsPersistenceUtil.createEnvironment;

@RunWith(Parameterized.class)
public class RuleFlowGroupRollbackTest {

    private static Logger logger = LoggerFactory.getLogger(RuleFlowGroupRollbackTest.class);
    
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
    
    public RuleFlowGroupRollbackTest(String locking) { 
        this.locking = PESSIMISTIC_LOCKING.equals(locking);
    }
    
    @Before
    public void setUp() throws Exception {
        context = DroolsPersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
    }
	
	@After
	public void tearDown() {
		DroolsPersistenceUtil.cleanUp(context);
	}

    @Test	
	public void testRuleFlowGroupRollback() throws Exception {
		
		CommandBasedStatefulKnowledgeSessionImpl ksession = createSession();
		
		List<String> list = new ArrayList<String>();
		list.add("Test");
		
		ksession.insert(list);
		ksession.execute(new ActivateRuleFlowCommand("ruleflow-group"));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
		
		try {
			ksession.execute(new ExceptionCommand());
			fail("Process must throw an exception");
		} catch (Exception e) {
			logger.info("The above " + RuntimeException.class.getSimpleName() + " was expected in this test.");
		}
		
		ksession.insert(list);
		ksession.execute(new ActivateRuleFlowCommand("ruleflow-group"));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
		
	}
	
	private CommandBasedStatefulKnowledgeSessionImpl createSession() {
		
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource("ruleflowgroup_rollback.drl"), ResourceType.DRL );
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        kbase.addPackages( kbuilder.getKnowledgePackages() );

        Environment env = createEnvironment(context);
        if( locking ) { 
            env.set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        }
        return (CommandBasedStatefulKnowledgeSessionImpl) JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
	}
	
	@SuppressWarnings("serial")
	public class ActivateRuleFlowCommand implements ExecutableCommand<Object> {
		
		private String ruleFlowGroupName;
		
		public ActivateRuleFlowCommand(String ruleFlowGroupName){
			this.ruleFlowGroupName = ruleFlowGroupName;
		}

	    public Void execute(Context context) {
	        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
	        ((InternalAgenda) ksession.getAgenda()).activateRuleFlowGroup(ruleFlowGroupName);
	        return null;
	    }

	}
	
	@SuppressWarnings("serial")
	public class ExceptionCommand implements ExecutableCommand<Object> {

	    public Void execute(Context context) {
	    	throw new RuntimeException("(Expected) exception thrown by test");
	    }

	}
	
}
