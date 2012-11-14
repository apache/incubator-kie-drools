package org.jbpm.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.kie.KnowledgeBase;
import org.kie.SystemEventListenerFactory;
import org.drools.impl.EnvironmentFactory;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.Environment;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.StatefulKnowledgeSession;
import org.h2.tools.Server;
import org.jbpm.process.workitem.wsht.HornetQHTWorkItemHandler;
import org.jbpm.process.workitem.wsht.MinaHTWorkItemHandler;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.hornetq.HornetQTaskServer;
import org.jbpm.task.service.mina.MinaTaskServer;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public final class JBPMHelper {
	
    public static String [] processStateName = { "PENDING", "ACTIVE", "COMPLETED", "ABORTED", "SUSPENDED" };
    
    public static String [] txStateName = { "ACTIVE",
        "MARKED_ROLLBACK", 
        "PREPARED",
        "COMMITTED",
        "ROLLEDBACK", 
        "UNKNOWN", 
        "NO_TRANSACTION",
        "PREPARING",
        "COMMITTING",
        "ROLLING_BACK" };
    
	private JBPMHelper() {
	}
	
	public static void startUp() {
		Properties properties = getProperties();
		String driverClassName = properties.getProperty("persistence.datasource.driverClassName", "org.h2.Driver");
		if (driverClassName.startsWith("org.h2")) {
			JBPMHelper.startH2Server();
		}
		String persistenceEnabled = properties.getProperty("persistence.enabled", "false");
		String humanTaskEnabled = properties.getProperty("taskservice.enabled", "false");
		if ("true".equals(persistenceEnabled) || "true".equals(humanTaskEnabled)) {
			JBPMHelper.setupDataSource();
		}
		if ("true".equals(humanTaskEnabled)) {
			JBPMHelper.startTaskService();
		}
	}
	
	public static Server startH2Server() {
		try {
			// start h2 in memory database
			Server server = Server.createTcpServer(new String[0]);
	        server.start();
	        return server;
		} catch (Throwable t) {
			throw new RuntimeException("Could not start H2 server", t);
		}
	}
	
	public static PoolingDataSource setupDataSource() {
		Properties properties = getProperties();
        // create data source
		PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName(properties.getProperty("persistence.datasource.name", "jdbc/jbpm-ds"));
        pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
        pds.setMaxPoolSize(5);
        pds.setAllowLocalTransactions(true);
        pds.getDriverProperties().put("user", properties.getProperty("persistence.datasource.user", "sa"));
        pds.getDriverProperties().put("password", properties.getProperty("persistence.datasource.password", ""));
        pds.getDriverProperties().put("url", properties.getProperty("persistence.datasource.url", "jdbc:h2:tcp://localhost/~/jbpm-db;MVCC=TRUE"));
        pds.getDriverProperties().put("driverClassName", properties.getProperty("persistence.datasource.driverClassName", "org.h2.Driver"));
        pds.init();
        return pds;
	}
	
	public static TaskService startTaskService() {
		Properties properties = getProperties();
		String dialect = properties.getProperty("persistence.persistenceunit.dialect", "org.hibernate.dialect.H2Dialect");
		Map<String, String> map = new HashMap<String, String>();
		map.put("hibernate.dialect", dialect);
        EntityManagerFactory emf =
        	Persistence.createEntityManagerFactory(properties.getProperty("taskservice.datasource.name", "org.jbpm.task"), map);
        System.setProperty("jbpm.user.group.mapping",properties.getProperty("taskservice.usergroupmapping", "classpath:/usergroups.properties"));
        System.setProperty("jbpm.usergroup.callback",
			properties.getProperty("taskservice.usergroupcallback", "org.jbpm.task.identity.DefaultUserGroupCallbackImpl"));
        TaskService taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
        String transport = properties.getProperty("taskservice.transport", "mina");
        if ("mina".equals(transport)) {
    		MinaTaskServer taskServer = new MinaTaskServer(taskService);
            Thread thread = new Thread(taskServer);
            thread.start();
        } else if ("hornetq".equals(transport)) {
            HornetQTaskServer taskServer = new HornetQTaskServer(taskService, Integer.parseInt(properties.getProperty("taskservice.port", "5153")));
            Thread thread = new Thread(taskServer);
            thread.start();
        } else {
        	throw new RuntimeException("Unknown task service transport " + transport);
        }
        return taskService;
	}
	
	public static void registerTaskService(StatefulKnowledgeSession ksession) {
		Properties properties = getProperties();
		String transport = properties.getProperty("taskservice.transport", "hornetq");
        if ("mina".equals(transport)) {
        	ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
    			new MinaHTWorkItemHandler(ksession));
        } else if ("hornetq".equals(transport)) {
            ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new HornetQHTWorkItemHandler(ksession));
        } else {
        	throw new RuntimeException("Unknown task service transport " + transport);
        }	
	}
	   
    protected static Environment createEnvironment(EntityManagerFactory emf) { 
        Environment env = EnvironmentFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        env.set(EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager());
        return env;
    }
    
	public static StatefulKnowledgeSession newStatefulKnowledgeSession(KnowledgeBase kbase) {
		return loadStatefulKnowledgeSession(kbase, -1);
	}
	
	public static StatefulKnowledgeSession loadStatefulKnowledgeSession(KnowledgeBase kbase, int sessionId) {
		Properties properties = getProperties();
		String persistenceEnabled = properties.getProperty("persistence.enabled", "false");
		StatefulKnowledgeSession ksession;
		if ("true".equals(persistenceEnabled)) {
			String dialect = properties.getProperty("persistence.persistenceunit.dialect", "org.hibernate.dialect.H2Dialect");
			Map<String, String> map = new HashMap<String, String>();
			map.put("hibernate.dialect", dialect);
			EntityManagerFactory emf =
			    Persistence.createEntityManagerFactory(properties.getProperty("persistence.persistenceunit.name", "org.jbpm.persistence.jpa"), map);
			Environment env = createEnvironment(emf);
			
			/** At the moment, we still need a real Thread.sleep() to test things, 
			 * since the pseudo clock is attached to the ksession 
			 */
	        // final KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
	        // conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
			
			// create a new knowledge session that uses JPA to store the runtime state
	        if (sessionId == -1) {
	        	ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
	        } else {
	        	ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( sessionId, kbase, null, env);
	        }
			String humanTaskEnabled = properties.getProperty("taskservice.enabled", "false");
			if ("true".equals(humanTaskEnabled)) {
				String transport = properties.getProperty("taskservice.transport", "hornetq");
		        if ("mina".equals(transport)) {
					ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
						new MinaHTWorkItemHandler(ksession));
		        }  else if ("hornetq".equals(transport)) {
		            ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
	                    new HornetQHTWorkItemHandler(ksession));
	            } else {
		        	throw new RuntimeException("Unknown task service transport " + transport);
		        }
			}
		} else {
			ksession = kbase.newStatefulKnowledgeSession();
			String humanTaskEnabled = properties.getProperty("taskservice.enabled", "false");
			if ("true".equals(humanTaskEnabled)) {
				String transport = properties.getProperty("taskservice.transport", "hornetq");
		        if ("mina".equals(transport)) {
					ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
						new MinaHTWorkItemHandler(ksession));
		        } else if ("hornetq".equals(transport)) {
		            ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
	                    new HornetQHTWorkItemHandler(ksession));
	            } else {
		        	throw new RuntimeException("Unknown task service transport " + transport);
		        }	
			}
		}
		KnowledgeSessionCleanup.knowledgeSessionSetLocal.get().add(ksession);
		return ksession;
	}

	public static Properties getProperties() {
	    Properties properties = new Properties();
		try {
			properties.load(JBPMHelper.class.getResourceAsStream("/jBPM.properties"));
		} catch (Throwable t) {
			// do nothing, use defaults
		}
		return properties;
	}

}
