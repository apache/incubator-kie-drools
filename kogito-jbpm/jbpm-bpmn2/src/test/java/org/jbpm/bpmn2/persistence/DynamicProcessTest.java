package org.jbpm.bpmn2.persistence;

import static org.jbpm.persistence.util.PersistenceUtil.setupWithPoolingDataSource;

import java.util.HashMap;

import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.jbpm.bpmn2.JbpmBpmn2TestCase;
import org.jbpm.compiler.xml.XmlRuleFlowProcessDumper;
import org.jbpm.persistence.session.objects.TestWorkItemHandler;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.Context;
import org.kie.api.definition.process.Connection;
import org.kie.event.process.ProcessCompletedEvent;
import org.kie.event.process.ProcessEventListener;
import org.kie.event.process.ProcessNodeLeftEvent;
import org.kie.event.process.ProcessNodeTriggeredEvent;
import org.kie.event.process.ProcessStartedEvent;
import org.kie.event.process.ProcessVariableChangedEvent;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.io.ResourceType;
import org.kie.runtime.Environment;

/**
 * This is a sample file to launch a process.
 */
public class DynamicProcessTest extends JbpmBpmn2TestCase {

    private HashMap<String, Object> context;
    private Environment env;

    @Test
	public void testDynamicProcess() throws Exception {		
		RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("org.jbpm.HelloWorld");
		factory
			// Header
			.name("HelloWorldProcess")
			.version("1.0")
			.packageName("org.jbpm")
			// Nodes
			.startNode(1).name("Start").done()
			.humanTaskNode(2).name("Task1").actorId("krisv").taskName("MyTask").done()
			.endNode(3).name("End").done()
			// Connections
			.connection(1, 2)
			.connection(2, 3);
		final RuleFlowProcess process = factory.validate().getProcess();
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newByteArrayResource(
			XmlRuleFlowProcessDumper.INSTANCE.dump(process).getBytes()), ResourceType.DRF);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase();
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		TestWorkItemHandler testHandler = new TestWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", testHandler);
		ksession.addEventListener(new ProcessEventListener() {
			public void beforeVariableChanged(ProcessVariableChangedEvent arg0) {
			}
			public void beforeProcessStarted(ProcessStartedEvent arg0) {
				System.out.println(arg0);
			}
			public void beforeProcessCompleted(ProcessCompletedEvent arg0) {
				System.out.println(arg0);
			}
			public void beforeNodeTriggered(ProcessNodeTriggeredEvent arg0) {
				System.out.println(arg0);
			}
			public void beforeNodeLeft(ProcessNodeLeftEvent arg0) {
				System.out.println(arg0);
			}
			public void afterVariableChanged(ProcessVariableChangedEvent arg0) {
			}
			public void afterProcessStarted(ProcessStartedEvent arg0) {
			}
			public void afterProcessCompleted(ProcessCompletedEvent arg0) {
			}
			public void afterNodeTriggered(ProcessNodeTriggeredEvent arg0) {
			}
			public void afterNodeLeft(ProcessNodeLeftEvent arg0) {
			}
		});

		final ProcessInstanceImpl processInstance = (ProcessInstanceImpl)
			ksession.startProcess("org.jbpm.HelloWorld");
		
		HumanTaskNode node = new HumanTaskNode();
		node.setName("Task2");
		node.setId(4);
		insertNodeInBetween(process, 2, 3, node);
		
		((CommandBasedStatefulKnowledgeSession) ksession).getCommandService().execute(new GenericCommand<Void>() {
			public Void execute(Context context) {
				StatefulKnowledgeSession ksession = (StatefulKnowledgeSession) ((KnowledgeCommandContext) context).getKieSession();
				((ProcessInstanceImpl) ksession.getProcessInstance(processInstance.getId())).updateProcess(process);
				return null;
			}
		});
		
		ksession.getWorkItemManager().completeWorkItem(testHandler.getWorkItem().getId(), null);
		
		ksession.getWorkItemManager().completeWorkItem(testHandler.getWorkItem().getId(), null);
	}

	private static void insertNodeInBetween(RuleFlowProcess process, long startNodeId, long endNodeId, NodeImpl node) {
		if (process == null) {
			throw new IllegalArgumentException("Process may not be null");
		}
		NodeImpl selectedNode = (NodeImpl) process.getNode(startNodeId);
		if (selectedNode == null) {
			throw new IllegalArgumentException("Node " + startNodeId + " not found in process " + process.getId());
		}
		for (Connection connection: selectedNode.getDefaultOutgoingConnections()) {
			if (connection.getTo().getId() == endNodeId) {
				process.addNode(node);
				NodeImpl endNode = (NodeImpl) connection.getTo();
				((ConnectionImpl) connection).terminate();
				new ConnectionImpl(selectedNode, NodeImpl.CONNECTION_DEFAULT_TYPE, node, NodeImpl.CONNECTION_DEFAULT_TYPE);
				new ConnectionImpl(node, NodeImpl.CONNECTION_DEFAULT_TYPE, endNode, NodeImpl.CONNECTION_DEFAULT_TYPE);
				return;
			}
		}
		throw new IllegalArgumentException("Connection to node " + endNodeId + " not found in process " + process.getId());
	}
	
}
