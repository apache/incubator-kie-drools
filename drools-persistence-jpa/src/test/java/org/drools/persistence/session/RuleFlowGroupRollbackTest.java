/*
 * Copyright 2011 Red Hat Inc.
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
package org.drools.persistence.session;

import static org.junit.Assert.*;
import static org.drools.persistence.util.PersistenceUtil.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.command.Context;
import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.io.impl.ClassPathResource;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.persistence.util.PersistenceUtil;
import org.drools.runtime.Environment;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.impl.InternalAgenda;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RuleFlowGroupRollbackTest {
    
    private HashMap<String, Object> context;

    @Before
    public void setUp() throws Exception {
        context = PersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
    }
	
	@After
	public void tearDown() {
		PersistenceUtil.tearDown(context);
	}

    @Test	
	public void testRuleFlowGroupRollback() throws Exception {
		
		CommandBasedStatefulKnowledgeSession ksession = createSession();
		
		List<String> list = new ArrayList<String>();
		list.add("Test");
		
		ksession.insert(list);
		ksession.execute(new ActivateRuleFlowCommand("ruleflow-group"));
		assertEquals(1, ksession.fireAllRules());
		
		try {
			ksession.execute(new ExceptionCommand());
			fail("Process must throw an exception");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ksession.insert(list);
		ksession.execute(new ActivateRuleFlowCommand("ruleflow-group"));
		assertEquals(1, ksession.fireAllRules());
		
	}
	
	private CommandBasedStatefulKnowledgeSession createSession() {
		
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource("ruleflowgroup_rollback.drl"), ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        Environment env = createEnvironment(context);
        return (CommandBasedStatefulKnowledgeSession) JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
	}
	
	@SuppressWarnings("serial")
	public class ActivateRuleFlowCommand implements GenericCommand<Object> {
		
		private String ruleFlowGroupName;
		
		public ActivateRuleFlowCommand(String ruleFlowGroupName){
			this.ruleFlowGroupName = ruleFlowGroupName;
		}

	    public Void execute(Context context) {
	        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
	        ((InternalAgenda) ksession.getAgenda()).activateRuleFlowGroup(ruleFlowGroupName);
	        return null;
	    }

	}
	
	@SuppressWarnings("serial")
	public class ExceptionCommand implements GenericCommand<Object> {

	    public Void execute(Context context) {
	    	throw new RuntimeException();
	    }

	}
	
}
