package org.jbpm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.audit.WorkingMemoryInMemoryLogger;
import org.drools.audit.event.LogEvent;
import org.drools.audit.event.RuleFlowNodeLogEvent;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.process.audit.NodeInstanceLog;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public abstract class JbpmJUnitTestCase extends TestCase {
	
    protected final static String EOL = System.getProperty( "line.separator" );
    
	private boolean persistence = false;
	private PoolingDataSource ds1;
	private EntityManagerFactory emf;
	private static Server h2Server;
	private TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
	public StatefulKnowledgeSession ksession;
	
	private WorkingMemoryInMemoryLogger logger;
	private JPAProcessInstanceDbLog log;

	public JbpmJUnitTestCase() {
		this(true);
	}
	
	public JbpmJUnitTestCase(boolean persistence) {
		this.persistence = persistence;
		if (persistence) {
			startH2Database();
		}
	}
	
	public boolean isPersistence() {
		return persistence;
	}
    
    protected void setUp() {
    	if (persistence) {
	    	ds1 = new PoolingDataSource();
	        ds1.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
	    	ds1.setUniqueName("jdbc/testDS1");
	    	ds1.setMaxPoolSize(5);
	    	ds1.setAllowLocalTransactions(true);
	    	ds1.getDriverProperties().setProperty("driverClassName", "org.h2.Driver");
	    	ds1.getDriverProperties().setProperty("url", "jdbc:h2:tcp://localhost/JPADroolsFlow");
	    	ds1.getDriverProperties().setProperty("user", "sa");
	    	ds1.getDriverProperties().setProperty("password", "");
	        ds1.init();
	        emf = Persistence.createEntityManagerFactory( "org.jbpm.persistence.jpa" );
    	}
    }

    protected void tearDown() {
    	if (emf != null) {
    		emf.close();
    	}
    	if (ds1 != null) {
            ds1.close();
    	}
    }
    
    public synchronized void startH2Database() {
    	if (h2Server == null) {
	    	try {
				DeleteDbFiles.execute("", "", true);
				h2Server = Server.createTcpServer(new String[0]);
				h2Server.start();
			} catch (SQLException e) {
				if (h2Server != null) {
					h2Server.shutdown();
					h2Server = null;
				}
				throw new RuntimeException("can't start h2 server db",e);
			}
    	}
    }
    
	protected KnowledgeBase createKnowledgeBase(String... process) {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		for (String p: process) {
			kbuilder.add(ResourceFactory.newClassPathResource(p), ResourceType.BPMN2);
		}
		return kbuilder.newKnowledgeBase();
	}
	
	protected KnowledgeBase createKnowledgeBase(Map<String, ResourceType> resources) throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		for (Map.Entry<String, ResourceType> entry: resources.entrySet()) {
			kbuilder.add(ResourceFactory.newClassPathResource(entry.getKey()), entry.getValue());
		}
		return kbuilder.newKnowledgeBase();
	}
	
	protected KnowledgeBase createKnowledgeBaseGuvnor(String... packages) throws Exception {
		return createKnowledgeBaseGuvnor(false, "http://localhost:8080/drools-guvnor", "admin", "admin", packages);
	}

	protected KnowledgeBase createKnowledgeBaseGuvnor(boolean dynamic, String url, String username, 
													  String password, String... packages) throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		String changeSet = 
			"<change-set xmlns='http://drools.org/drools-5.0/change-set'" + EOL +
			"            xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'" + EOL +
			"            xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >" + EOL +
			"    <add>" + EOL;
		for (String p: packages) {
			changeSet += "        <resource source='" + url + "/org.drools.guvnor.Guvnor/package/" + p + "/LATEST' type='PKG' basicAuthentication=\"enabled\" username=\"" + username + "\" password=\"" + password + "\" />" + EOL;
		}
		changeSet +=
			"    </add>" + EOL +
			"</change-set>";
		kbuilder.add(ResourceFactory.newByteArrayResource(changeSet.getBytes()), ResourceType.CHANGE_SET);
		return kbuilder.newKnowledgeBase();
	}
	
	protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) {
		if (persistence) {
		    Environment env = KnowledgeBaseFactory.newEnvironment();
		    env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
		    env.set(EnvironmentName.TRANSACTION_MANAGER,
		        TransactionManagerServices.getTransactionManager());
		    StatefulKnowledgeSession result = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
		    new JPAWorkingMemoryDbLogger(result);
		    if (log == null) {
		    	log = new JPAProcessInstanceDbLog();
		    }
		    return result;
		} else {
			StatefulKnowledgeSession result = kbase.newStatefulKnowledgeSession();
			logger = new WorkingMemoryInMemoryLogger(result);
			return result;
		}
	}
	
	protected StatefulKnowledgeSession createKnowledgeSession(String... process) {
		KnowledgeBase kbase = createKnowledgeBase(process);
		return createKnowledgeSession(kbase);
	}
		
	protected StatefulKnowledgeSession restoreSession(StatefulKnowledgeSession ksession, boolean noCache) {
		if (persistence) {
			int id = ksession.getId();
			KnowledgeBase kbase = ksession.getKnowledgeBase();
			Environment env = null;
			if (noCache) {
				env = KnowledgeBaseFactory.newEnvironment();
				emf = Persistence.createEntityManagerFactory( "org.jbpm.persistence.jpa" );
			    env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
			    env.set(EnvironmentName.TRANSACTION_MANAGER,
			        TransactionManagerServices.getTransactionManager());				
			} else {
				env = ksession.getEnvironment();
			}
			KnowledgeSessionConfiguration config = ksession.getSessionConfiguration();
			StatefulKnowledgeSession result = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, config, env);
			new JPAWorkingMemoryDbLogger(result);
			return result;
		} else {
			return ksession;
		}
	}
    
	public Object getVariableValue(String name, ProcessInstance processInstance) {
		return ((WorkflowProcessInstance) processInstance).getVariable(name);
	}
    
	public void assertProcessInstanceCompleted(long processInstanceId, StatefulKnowledgeSession ksession) {
		assertNull(ksession.getProcessInstance(processInstanceId));
	}
	
	public void assertProcessInstanceAborted(long processInstanceId, StatefulKnowledgeSession ksession) {
		assertNull(ksession.getProcessInstance(processInstanceId));
	}
	
	public void assertProcessInstanceActive(long processInstanceId, StatefulKnowledgeSession ksession) {
		assertNotNull(ksession.getProcessInstance(processInstanceId));
	}
	
	public void assertNodeActive(long processInstanceId, StatefulKnowledgeSession ksession, String... name) {
		List<String> names = new ArrayList<String>();
		for (String n: name) {
			names.add(n);
		}
		ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
		if (processInstance instanceof WorkflowProcessInstance) {
			assertNodeActive((WorkflowProcessInstance) processInstance, names);
		}
		if (!names.isEmpty()) {
			String s = names.get(0);
			for (int i = 1; i < names.size(); i++) {
				s += ", " + names.get(i);
			}
			fail("Node(s) not active: " + s);
		}
	}
	
	private void assertNodeActive(NodeInstanceContainer container, List<String> names) {
		for (NodeInstance nodeInstance: container.getNodeInstances()) {
			String nodeName = nodeInstance.getNodeName();
			if (names.contains(nodeName)) {
				names.remove(nodeName);
			}
			if (nodeInstance instanceof NodeInstanceContainer) {
				assertNodeActive((NodeInstanceContainer) nodeInstance, names);
			}
		}
	}
	
	public void assertNodeTriggered(long processInstanceId, String... nodeNames) {
		List<String> names = new ArrayList<String>();
		for (String nodeName: nodeNames) {
			names.add(nodeName);
		}
		if (persistence) {
			List<NodeInstanceLog> logs = log.findNodeInstances(processInstanceId);
			if (logs != null) {
				for (NodeInstanceLog l: logs) {
					String nodeName = l.getNodeName();
					if (l.getType() == NodeInstanceLog.TYPE_ENTER && names.contains(nodeName)) {
						names.remove(nodeName);
					}
				}
			}
		} else {
			for (LogEvent event: logger.getLogEvents()) {
				if (event instanceof RuleFlowNodeLogEvent) {
					String nodeName = ((RuleFlowNodeLogEvent) event).getNodeName();
					if (names.contains(nodeName)) {
						names.remove(nodeName);
					}
				}
			}
		}
		if (!names.isEmpty()) {
			String s = names.get(0);
			for (int i = 1; i < names.size(); i++) {
				s += ", " + names.get(i);
			}
			fail("Node(s) not executed: " + s);
		}
	}
	
	protected void clearHistory() {
		if (persistence) {
			if (log == null) {
				log = new JPAProcessInstanceDbLog();
			}
			log.clear();
		} else {
			logger.clear();
		}
	}
	
	public TestWorkItemHandler getTestWorkItemHandler() {
		return workItemHandler;
	}
	
	public static class TestWorkItemHandler implements WorkItemHandler {
		
	    private List<WorkItem> workItems = new ArrayList<WorkItem>();
	    
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            workItems.add(workItem);
        }
        
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        }
        
        public WorkItem getWorkItem() {
        	if (workItems.size() == 0) {
        		return null;
        	}
        	if (workItems.size() == 1) {
        		WorkItem result = workItems.get(0);
        		this.workItems.clear();
        		return result;
        	} else {
        		throw new IllegalArgumentException("More than one work item active");
        	}
        }
        
        public List<WorkItem> getWorkItems() {
        	List<WorkItem> result = new ArrayList<WorkItem>(workItems);
        	workItems.clear();
        	return result;
        }
        
	}
}
