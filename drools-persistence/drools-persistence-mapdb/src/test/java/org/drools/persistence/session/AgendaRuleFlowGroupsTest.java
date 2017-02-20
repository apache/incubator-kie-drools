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
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

public class AgendaRuleFlowGroupsTest {
    
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
	public void testRuleFlowGroupOnly() throws Exception {
		
		CommandBasedStatefulKnowledgeSession ksession = createSession(-1, "ruleflow-groups.drl");
		
		org.drools.core.spi.AgendaGroup[] groups = ((InternalAgenda)stripSession(ksession).getAgenda()).getAgendaGroups();
		// only main is available
		assertEquals(1, groups.length);
		assertEquals("MAIN", groups[0].getName());
		long id = ksession.getIdentifier();
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
        long id = ksession.getIdentifier();
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
        long id = ksession.getIdentifier();
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
            return ((RegistryContext)((CommandBasedStatefulKnowledgeSession) ksession).
                    getRunner().createContext()).lookup( KieSession.class );
        }
        
        return ksession;
    }
	
	private CommandBasedStatefulKnowledgeSession createSession(long id, String...rules) {

		KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
		for (String rule : rules) {
		    kfs.write(ResourceFactory.newClassPathResource(rule));
		}
		KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if ( kbuilder.getResults().hasMessages(Level.ERROR) ) {
            fail( kbuilder.getResults().toString() );
        }
        KieBase kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();
        Environment env = MapDBPersistenceUtil.createEnvironment(context);
        if (id == -1) {
            return (CommandBasedStatefulKnowledgeSession) KieServices.Factory.get().getStoreServices().newKieSession( kbase, null, env );
	    }  else {
	        return (CommandBasedStatefulKnowledgeSession) KieServices.Factory.get().getStoreServices().loadKieSession( id, kbase, null, env );
	    }
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
    public class ActivateAgendaGroupCommand implements ExecutableCommand<Object> {
        
        private String agendaGroupName;
        
        public ActivateAgendaGroupCommand(String agendaGroupName){
            this.agendaGroupName = agendaGroupName;
        }

        public Void execute(Context context) {
            KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
            ((InternalAgenda) ksession.getAgenda()).getAgendaGroup(agendaGroupName).setFocus();
            return null;
        }

    }
	
	@SuppressWarnings("serial")
	public class ExceptionCommand implements ExecutableCommand<Object> {

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
