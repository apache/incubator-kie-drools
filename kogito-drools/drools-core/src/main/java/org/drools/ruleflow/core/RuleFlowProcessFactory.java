package org.drools.ruleflow.core;

import org.drools.definition.process.Node;
import org.drools.process.core.validation.ProcessValidationError;
import org.drools.ruleflow.core.validation.RuleFlowProcessValidator;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.StartNode;

public class RuleFlowProcessFactory {
	
	private RuleFlowProcess process;
	
	private RuleFlowProcessFactory(String id) {
		process = new RuleFlowProcess();
		process.setId(id);
	}
	
	public static RuleFlowProcessFactory createProcess(String id) {
		return new RuleFlowProcessFactory(id);
	}
	
	public RuleFlowProcessFactory name(String name) {
		process.setName(name);
		return this;
	}
	
	public RuleFlowProcessFactory packageName(String packageName) {
		process.setPackageName(packageName);
		return this;
	}
	
	public StartNodeFactory startNode(long id) {
		return new StartNodeFactory(process, id);
	}
	
	public EndNodeFactory endNode(long id) {
		return new EndNodeFactory(process, id);
	}
	
	public ActionNodeFactory actionNode(long id) {
		return new ActionNodeFactory(process, id);
	}
	
	public RuleFlowProcessFactory connection(long fromId, long toId) {
		Node from = process.getNode(fromId);
		Node to = process.getNode(toId);
		new ConnectionImpl(
			from, org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE,
			to, org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
		return this;
	}
	
	public RuleFlowProcessFactory validate() {
		ProcessValidationError[] errors = RuleFlowProcessValidator.getInstance().validateProcess(process);
		for (ProcessValidationError error: errors) {
			System.err.println(error);
		}
		if (errors.length > 0) {
			throw new RuntimeException("Process could not be validated !");
		}
		return this;
	}
	
	public RuleFlowProcess done() {
		return process;
	}
	
	public abstract class NodeFactory {
		
		private org.drools.workflow.core.Node node;
		private NodeContainer nodeContainer;
		
		private NodeFactory(NodeContainer nodeContainer, long id) {
			this.nodeContainer = nodeContainer;
			this.node = createNode();
			this.node.setId(id);
		}
		
		protected abstract org.drools.workflow.core.Node createNode();
		
		public RuleFlowProcessFactory done() {
			nodeContainer.addNode(node);
			return RuleFlowProcessFactory.this;
		}
		
		protected org.drools.workflow.core.Node getNode() {
			return node;
		}
		
	}
	
	public class StartNodeFactory extends NodeFactory {
		
		private StartNodeFactory(NodeContainer nodeContainer, long id) {
			super(nodeContainer, id);
		}
		
		protected org.drools.workflow.core.Node createNode() {
			return new StartNode();
		}
		
		public StartNodeFactory name(String name) {
			getNode().setName(name);
			return this;
		}
		
	}

	public class EndNodeFactory extends NodeFactory {
		
		private EndNodeFactory(NodeContainer nodeContainer, long id) {
			super(nodeContainer, id);
		}
		
		protected org.drools.workflow.core.Node createNode() {
			return new EndNode();
		}
		
		protected EndNode getEndNode() {
			return (EndNode) getNode();
		}
		
		public EndNodeFactory name(String name) {
			getNode().setName(name);
			return this;
		}
		
		public EndNodeFactory setTerminate(boolean terminate) {
			getEndNode().setTerminate(terminate);
			return this;
		}
		
	}

	public class ActionNodeFactory extends NodeFactory {
		
		private ActionNodeFactory(NodeContainer nodeContainer, long id) {
			super(nodeContainer, id);
		}
		
		protected org.drools.workflow.core.Node createNode() {
			return new ActionNode();
		}
		
		protected ActionNode getActionNode() {
			return (ActionNode) getNode();
		}
		
		public ActionNodeFactory name(String name) {
			getNode().setName(name);
			return this;
		}
		
		public ActionNodeFactory action(String dialect, String action) {
			getActionNode().setAction(new DroolsConsequenceAction(dialect, action));
			return this;
		}
		
	}

}