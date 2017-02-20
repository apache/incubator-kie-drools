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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.common.InternalAgenda;
import org.drools.persistence.mapdb.util.MapDBPersistenceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleFlowGroupRollbackTest {

    private static Logger logger = LoggerFactory.getLogger(RuleFlowGroupRollbackTest.class);
    
    private Map<String, Object> context;

    @Before
    public void setUp() throws Exception {
        context = MapDBPersistenceUtil.setupMapDB();
    }
	
	@After
	public void tearDown() {
		MapDBPersistenceUtil.cleanUp(context);
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
			logger.info("The above " + RuntimeException.class.getSimpleName() + " was expected in this test.");
		}
		
		ksession.insert(list);
		ksession.execute(new ActivateRuleFlowCommand("ruleflow-group"));
		assertEquals(1, ksession.fireAllRules());
		
	}
	
	private CommandBasedStatefulKnowledgeSession createSession() {
		
		KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write("src/main/resources/r1.drl", ResourceFactory.newClassPathResource("ruleflowgroup_rollback.drl"));
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
        	fail (kbuilder.getResults().toString());
        }
        KieBaseConfiguration kconf = KieServices.Factory.get().newKieBaseConfiguration();
        kconf.setOption(EqualityBehaviorOption.IDENTITY);
        KieBase kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).newKieBase(kconf);

        Environment env = MapDBPersistenceUtil.createEnvironment(context);
        return (CommandBasedStatefulKnowledgeSession) KieServices.Factory.get().getStoreServices().newKieSession( kbase, null, env );
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
