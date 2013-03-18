package org.jbpm.bpmn2;

import static org.jbpm.persistence.util.PersistenceUtil.*;

import java.util.*;

import junit.framework.TestCase;

import org.drools.core.SessionConfiguration;
import org.drools.core.audit.WorkingMemoryInMemoryLogger;
import org.drools.core.audit.event.LogEvent;
import org.drools.core.audit.event.RuleFlowNodeLogEvent;
import org.drools.core.impl.EnvironmentFactory;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.audit.*;
import org.jbpm.process.audit.AuditLoggerFactory.Type;
import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.internal.KnowledgeBase;
import org.kie.api.definition.process.Node;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.api.runtime.*;
import org.kie.api.runtime.process.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base test case for the jbpm-bpmn2 module. 
 *
 * Please keep this test class in the org.jbpm.bpmn2 package or otherwise give it a unique name. 
 *
 */
public abstract class JbpmBpmn2TestCase extends TestCase {
	
    protected final static String EOL = System.getProperty( "line.separator" );
    
	protected boolean persistence = true;
	private HashMap<String, Object> context;

	private TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
	public StatefulKnowledgeSession ksession;
	
	private WorkingMemoryInMemoryLogger logger;
	private Logger consoleLogger = null;

	public JbpmBpmn2TestCase() {
		this(true);
		consoleLogger = LoggerFactory.getLogger(this.getClass());
	}
	
	public JbpmBpmn2TestCase(boolean persistence) {
		this.persistence = persistence;
	}
	
	public boolean isPersistence() {
		return persistence;
	}
    
    protected void setUp() {
        System.out.println( "RUNNING: " + getName() );
    	if (persistence) {
	    	context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
    	}
    }

    protected void tearDown() {
        if(persistence) { 
            cleanUp(context);
        }
    }
    
	protected KnowledgeBase createKnowledgeBase(String... process) {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		for (String p: process) {
			kbuilder.add(ResourceFactory.newClassPathResource(p), ResourceType.BPMN2);
		}
        
        // Check for errors
        if (kbuilder.hasErrors()) {
            if (kbuilder.getErrors().size() > 0) {
                boolean errors = false;
                for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                    consoleLogger.error(error.toString());
                    errors = true;
                }
                assertFalse("Could not build knowldge base.", errors);
            }
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
	
	protected KnowledgeBase createKnowledgeBaseGuvnorAssets(String pkg, String...assets) throws Exception {
	    return createKnowledgeBaseGuvnor(false, "http://localhost:8080/drools-guvnor", "admin", "admin", pkg, assets);
	}
	
	protected KnowledgeBase createKnowledgeBaseGuvnor(boolean dynamic, String url, String username, 
            String password, String pkg, String... assets) throws Exception {
	    KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
	    String changeSet = 
	        "<change-set xmlns='http://drools.org/drools-5.0/change-set'" + EOL +
	        "            xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'" + EOL +
	        "            xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >" + EOL +
	        "    <add>" + EOL;
	    for(String a : assets) {
	        if(a.indexOf(".bpmn") >= 0) {
	            a = a.substring(0, a.indexOf(".bpmn"));
	        } 
	        changeSet += "        <resource source='" + url + "/rest/packages/" + pkg + "/assets/" + a + "/binary' type='BPMN2' basicAuthentication=\"enabled\" username=\"" + username + "\" password=\"" + password + "\" />" + EOL;
	    }
	    changeSet +=
	        "    </add>" + EOL +
	        "</change-set>";
	    kbuilder.add(ResourceFactory.newByteArrayResource(changeSet.getBytes()), ResourceType.CHANGE_SET);
	    return kbuilder.newKnowledgeBase();
	}

	protected KnowledgeBase createKnowledgeBaseGuvnor(boolean dynamic, String url, String username, 
													  String password, String... packages) throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		String changeSet = 
			"<change-set xmlns='http://drools.org/drools-5.0/change-set'" + EOL +
			"            xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'" + EOL +
			"            xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >" + EOL +
			"    <add>" + EOL;
		for (String p : packages) {
			changeSet += "        <resource source='" + url + "/rest/packages/" + p + "/binary' type='PKG' basicAuthentication=\"enabled\" username=\"" + username + "\" password=\"" + password + "\" />" + EOL;
		}
		changeSet +=
			"    </add>" + EOL +
			"</change-set>";
		kbuilder.add(ResourceFactory.newByteArrayResource(changeSet.getBytes()), ResourceType.CHANGE_SET);
		return kbuilder.newKnowledgeBase();
	}
	
	protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) {
		if (persistence) {
		    Environment env = createEnvironment(context);
		    
		    StatefulKnowledgeSession result = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
		    AuditLoggerFactory.newInstance(Type.JPA, result, null);
		    JPAProcessInstanceDbLog.setEnvironment(result.getEnvironment());
		    return result;
		} else {
		    Properties defaultProps = new Properties();
		    defaultProps.setProperty("drools.processSignalManagerFactory", DefaultSignalManagerFactory.class.getName());
		    defaultProps.setProperty("drools.processInstanceManagerFactory", DefaultProcessInstanceManagerFactory.class.getName());
		    SessionConfiguration sessionConfig = new SessionConfiguration(defaultProps);

		    StatefulKnowledgeSession result = kbase.newStatefulKnowledgeSession(sessionConfig, EnvironmentFactory.newEnvironment());
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
			KnowledgeBase kbase = ksession.getKieBase();
			Environment env = null;
			if (noCache) {
				env = createEnvironment(context);
			} else {
				env = ksession.getEnvironment();
			}
			KieSessionConfiguration config = ksession.getSessionConfiguration();
			StatefulKnowledgeSession result = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, config, env);
			AuditLoggerFactory.newInstance(Type.JPA, result, null);
			return result;
		} else {
			return ksession;
		}
	}
    
	public Object getVariableValue(String name, long processInstanceId, StatefulKnowledgeSession ksession) {
		return ((WorkflowProcessInstance) ksession.getProcessInstance(processInstanceId)).getVariable(name);
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
			List<NodeInstanceLog> logs = JPAProcessInstanceDbLog.findNodeInstances(processInstanceId);
			if (logs != null) {
				for (NodeInstanceLog l: logs) {
					String nodeName = l.getNodeName();
					// needs to check both types as catch events will not have TYPE_ENTER entries
					if ((l.getType() == NodeInstanceLog.TYPE_ENTER || l.getType() == NodeInstanceLog.TYPE_EXIT) && names.contains(nodeName)) {
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
			JPAProcessInstanceDbLog.clear();
		} else {
			logger.clear();
		}
	}
	
	public TestWorkItemHandler getTestWorkItemHandler() {
		return workItemHandler;
	}
	
	public void assertProcessVarExists(ProcessInstance process, String... processVarNames) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        List<String> names = new ArrayList<String>();
        for (String nodeName: processVarNames) {
            names.add(nodeName);
        }
        
        for(String pvar : instance.getVariables().keySet()) {
            if (names.contains(pvar)) {
                names.remove(pvar);
            }
        }
        
        if (!names.isEmpty()) {
            String s = names.get(0);
            for (int i = 1; i < names.size(); i++) {
                s += ", " + names.get(i);
            }
            fail("Process Variable(s) do not exist: " + s);
        }

    }
    
    public void assertNodeExists(ProcessInstance process, String... nodeNames) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        List<String> names = new ArrayList<String>();
        for (String nodeName: nodeNames) {
            names.add(nodeName);
        }
        
        for(Node node : instance.getNodeContainer().getNodes()) {
            if (names.contains(node.getName())) {
                names.remove(node.getName());
            }
        }
        
        if (!names.isEmpty()) {
            String s = names.get(0);
            for (int i = 1; i < names.size(); i++) {
                s += ", " + names.get(i);
            }
            fail("Node(s) do not exist: " + s);
        }
    }
    
    public void assertNumOfIncommingConnections(ProcessInstance process, String nodeName, int num) {
        assertNodeExists(process, nodeName);
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        for(Node node : instance.getNodeContainer().getNodes()) {
            if(node.getName().equals(nodeName)) {
                if(node.getIncomingConnections().size() != num) {
                    fail("Expected incomming connections: " + num + " - found " + node.getIncomingConnections().size());
                } else {
                    break;
                }
            }
        }
    }
    
    public void assertNumOfOutgoingConnections(ProcessInstance process, String nodeName, int num) {
        assertNodeExists(process, nodeName);
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        for(Node node : instance.getNodeContainer().getNodes()) {
            if(node.getName().equals(nodeName)) {
                if(node.getOutgoingConnections().size() != num) {
                    fail("Expected outgoing connections: " + num + " - found " + node.getOutgoingConnections().size());
                } else {
                    break;
                }
            }
        }
    }
    
    public void assertVersionEquals(ProcessInstance process, String version) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        if(!instance.getWorkflowProcess().getVersion().equals(version)) {
            fail("Expected version: " + version + " - found " + instance.getWorkflowProcess().getVersion());
        }
    }
    
    public void assertProcessNameEquals(ProcessInstance process, String name) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        if(!instance.getWorkflowProcess().getName().equals(name)) {
            fail("Expected name: " + name + " - found " + instance.getWorkflowProcess().getName());
        }
    }
    
    public void assertPackageNameEquals(ProcessInstance process, String packageName) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        if(!instance.getWorkflowProcess().getPackageName().equals(packageName)) {
            fail("Expected package name: " + packageName + " - found " + instance.getWorkflowProcess().getPackageName());
        }
    }
    
}
