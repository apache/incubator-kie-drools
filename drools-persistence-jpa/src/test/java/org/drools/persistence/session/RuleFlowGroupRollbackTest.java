package org.drools.persistence.session;

import static org.drools.persistence.util.PersistenceUtil.*;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.TestCase;

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
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.impl.InternalAgenda;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public class RuleFlowGroupRollbackTest extends TestCase {
	
	PoolingDataSource ds1;

    @Override
    protected void setUp() throws Exception {
        ds1 = setupPoolingDataSource();
        ds1.init();
    }
	
	@Override
	protected void tearDown() {
		ds1.close();
	}
	
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

        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY, emf );
        env.set( EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager() );

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
