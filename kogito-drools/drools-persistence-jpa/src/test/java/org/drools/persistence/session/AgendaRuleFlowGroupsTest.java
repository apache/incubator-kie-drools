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

import static org.drools.persistence.util.PersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.PersistenceUtil.createEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.common.InternalAgenda;
import org.drools.core.io.impl.ClassPathResource;
import org.drools.persistence.util.PersistenceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.Context;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.utils.KieHelper;

@RunWith(Parameterized.class)
public class AgendaRuleFlowGroupsTest {
    
    private HashMap<String, Object> context;
    private boolean locking;

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] locking = new Object[][] { { false }, { true } };
        return Arrays.asList(locking);
    };
    
    public AgendaRuleFlowGroupsTest(boolean locking) { 
        this.locking = true;
    }
    
    @Before
    public void setUp() throws Exception {
        context = PersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
    }
	
	@After
	public void tearDown() {
		PersistenceUtil.cleanUp(context);
	}

    @Test	
	public void testRuleFlowGroupOnly() throws Exception {
		
		CommandBasedStatefulKnowledgeSession ksession = createSession(-1, "ruleflow-groups.drl");
		
		org.drools.core.spi.AgendaGroup[] groups = ((InternalAgenda)stripSession(ksession).getAgenda()).getAgendaGroups();
		// only main is available
		assertEquals(1, groups.length);
		assertEquals("MAIN", groups[0].getName());
		int id = ksession.getId();
		List<String> list = new ArrayList<String>();
		list.add("Test");
		
		ksession.insert(list);
		ksession.execute(new ActivateRuleFlowCommand("ruleflow-group"));
		
		ksession.dispose();        
        ksession = createSession(id, "ruleflow-groups.drl");
        
        groups = ((InternalAgenda)stripSession(ksession).getAgenda()).getAgendaGroups();
        // main and rule flow is now on the agenda
        assertEquals(2, groups.length);
        assertEquals("MAIN", groups[0].getName());
        assertEquals("ruleflow-group", groups[1].getName());
	}
    
    @Test   
    public void testAgendaGroupOnly() throws Exception {
        
        CommandBasedStatefulKnowledgeSession ksession = createSession(-1, "agenda-groups.drl");
        
        org.drools.core.spi.AgendaGroup[] groups = ((InternalAgenda)stripSession(ksession).getAgenda()).getAgendaGroups();
        // only main is available
        assertEquals(1, groups.length);
        assertEquals("MAIN", groups[0].getName());
        int id = ksession.getId();
        List<String> list = new ArrayList<String>();
        list.add("Test");
        
        ksession.insert(list);
        ksession.execute(new ActivateAgendaGroupCommand("agenda-group"));
        
        ksession.dispose();        
        ksession = createSession(id, "agenda-groups.drl");
        
        groups = ((InternalAgenda)stripSession(ksession).getAgenda()).getAgendaGroups();
        // main and agenda group is now on the agenda
        assertEquals(2, groups.length);
        assertEquals("MAIN", groups[0].getName());
        assertEquals("agenda-group", groups[1].getName());
        
    }
    
    @Test   
    public void testAgendaGroupAndRuleFlowGroup() throws Exception {
        
        CommandBasedStatefulKnowledgeSession ksession = createSession(-1, "agenda-groups.drl", "ruleflow-groups.drl");
        
        org.drools.core.spi.AgendaGroup[] groups = ((InternalAgenda)stripSession(ksession).getAgenda()).getAgendaGroups();
        // only main is available
        assertEquals(1, groups.length);
        assertEquals("MAIN", groups[0].getName());
        int id = ksession.getId();
        List<String> list = new ArrayList<String>();
        list.add("Test");
        
        ksession.insert(list);
        ksession.execute(new ActivateAgendaGroupCommand("agenda-group"));
        ksession.execute(new ActivateRuleFlowCommand("ruleflow-group"));
        
        ksession.dispose();        
        ksession = createSession(id, "agenda-groups.drl", "ruleflow-groups.drl");
        
        groups = ((InternalAgenda)stripSession(ksession).getAgenda()).getAgendaGroups();
        // main and agenda group is now on the agenda
        assertEquals(3, groups.length);
        assertEquals("MAIN", groups[0].getName());
        assertEquals("ruleflow-group", groups[1].getName());
        assertEquals("agenda-group", groups[2].getName());
        
    }
    
    private KieSession stripSession(KieSession ksession) {
        if (ksession instanceof CommandBasedStatefulKnowledgeSession) {
            return ((KnowledgeCommandContext)((CommandBasedStatefulKnowledgeSession) ksession).
                    getCommandService().getContext()).getKieSession();
        }
        
        return ksession;
    }
	
	private CommandBasedStatefulKnowledgeSession createSession(int id, String...rules) {
		
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		for (String rule : rules) {
		    kbuilder.add( new ClassPathResource(rule), ResourceType.DRL );
		}
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        Environment env = createEnvironment(context);
        if( locking ) { 
            env.set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        }
        if (id == -1) {
            return (CommandBasedStatefulKnowledgeSession) JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
	    }  else {
	        return (CommandBasedStatefulKnowledgeSession) JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
	    }
	}
	
	@SuppressWarnings("serial")
	public class ActivateRuleFlowCommand implements GenericCommand<Object> {
		
		private String ruleFlowGroupName;
		
		public ActivateRuleFlowCommand(String ruleFlowGroupName){
			this.ruleFlowGroupName = ruleFlowGroupName;
		}

	    public Void execute(Context context) {
	        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
	        ((InternalAgenda) ksession.getAgenda()).activateRuleFlowGroup(ruleFlowGroupName);
	        return null;
	    }

	}
	
	@SuppressWarnings("serial")
    public class ActivateAgendaGroupCommand implements GenericCommand<Object> {
        
        private String agendaGroupName;
        
        public ActivateAgendaGroupCommand(String agendaGroupName){
            this.agendaGroupName = agendaGroupName;
        }

        public Void execute(Context context) {
            KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
            ((InternalAgenda) ksession.getAgenda()).getAgendaGroup(agendaGroupName).setFocus();
            return null;
        }

    }
	
	@SuppressWarnings("serial")
	public class ExceptionCommand implements GenericCommand<Object> {

	    public Void execute(Context context) {
	    	throw new RuntimeException();
	    }

	}

    @Test
    public void testConflictingAgendaAndRuleflowGroups() throws Exception {

        String drl = "package org.drools.test; " +
                     "" +
                     "rule Test " +
                     "  agenda-group 'ag' " +
                     "  ruleflow-group 'rf' " +
                     "when " +
                     "then " +
                     "end ";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        Results res = helper.verify();

        System.err.println( res.getMessages() );

        assertEquals( 1, res.getMessages( Message.Level.WARNING ).size() );
        assertEquals( 0, res.getMessages( Message.Level.ERROR ).size() );

    }


}
