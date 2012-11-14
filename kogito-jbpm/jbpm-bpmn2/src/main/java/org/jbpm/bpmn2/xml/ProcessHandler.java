/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.bpmn2.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.kie.definition.process.Node;
import org.kie.definition.process.NodeContainer;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.jbpm.bpmn2.core.DataStore;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.bpmn2.core.Error;
import org.jbpm.bpmn2.core.Escalation;
import org.jbpm.bpmn2.core.Interface;
import org.jbpm.bpmn2.core.IntermediateLink;
import org.jbpm.bpmn2.core.ItemDefinition;
import org.jbpm.bpmn2.core.Lane;
import org.jbpm.bpmn2.core.Message;
import org.jbpm.bpmn2.core.SequenceFlow;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.exception.ActionExceptionHandler;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.core.context.swimlane.Swimlane;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.process.instance.impl.CancelNodeInstanceAction;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Connection;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.impl.ConstraintImpl;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StateBasedNode;
import org.jbpm.workflow.core.node.StateNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ProcessHandler extends BaseAbstractHandler implements Handler {
	
	public static final String CONNECTIONS = "BPMN.Connections";
    public static final String LINKS = "BPMN.ThrowLinks";

	@SuppressWarnings("unchecked")
	public ProcessHandler() {
		if ((this.validParents == null) && (this.validPeers == null)) {
			this.validParents = new HashSet();
			this.validParents.add(Definitions.class);

			this.validPeers = new HashSet();
			this.validPeers.add(null);
            this.validPeers.add(ItemDefinition.class);
            this.validPeers.add(Message.class);
            this.validPeers.add(Interface.class);
            this.validPeers.add(Escalation.class);
            this.validPeers.add(Error.class);
            this.validPeers.add(DataStore.class);
            this.validPeers.add(RuleFlowProcess.class);

			this.allowNesting = false;
		}
	}

	public Object start(final String uri, final String localName,
			            final Attributes attrs, final ExtensibleXmlParser parser)
			throws SAXException {
		parser.startElementBuilder(localName, attrs);

		String id = attrs.getValue("id");
		String name = attrs.getValue("name");
		String packageName = attrs.getValue("http://www.jboss.org/drools", "packageName");
		String dynamic = attrs.getValue("http://www.jboss.org/drools", "adHoc");
		String version = attrs.getValue("http://www.jboss.org/drools", "version");

		RuleFlowProcess process = new RuleFlowProcess();
		process.setAutoComplete(true);
		process.setId(id);
		if (name == null) {
		    name = id;
		}
		process.setName(name);
		process.setType("RuleFlow");
		if (packageName == null) {
			packageName = "org.drools.bpmn2";
		}
		process.setPackageName(packageName);
		if ("true".equals(dynamic)) {
			process.setDynamic(true);
			process.setAutoComplete(false);
		}
		if (version != null) {
			process.setVersion(version);
		}

		((ProcessBuildData) parser.getData()).addProcess(process);
		// register the definitions object as metadata of process.
		process.setMetaData("Definitions", parser.getParent());
		// register bpmn2 imports as meta data of process
		Object typedImports = ((ProcessBuildData) parser.getData()).getMetaData("Bpmn2Imports");
		if (typedImports != null) {
		    process.setMetaData("Bpmn2Imports", typedImports);
		}
		return process;
	}

	@SuppressWarnings("unchecked")
	public Object end(final String uri, final String localName,
			          final ExtensibleXmlParser parser) throws SAXException {
		parser.endElementBuilder();
		
		RuleFlowProcess process = (RuleFlowProcess) parser.getCurrent();
		 List<IntermediateLink> throwLinks = (List<IntermediateLink>) process
         .getMetaData(LINKS);
        linkIntermediateLinks(process, throwLinks);

 		List<SequenceFlow> connections = (List<SequenceFlow>) process.getMetaData(CONNECTIONS);
 		linkConnections(process, connections);
		linkBoundaryEvents(process);
        List<Lane> lanes = (List<Lane>)
            process.getMetaData(LaneHandler.LANES);
        assignLanes(process, lanes);
        postProcessNodes(process);
		return process;
	}
	
	 public static void linkIntermediateLinks(NodeContainer process,
	            List<IntermediateLink> links) {

	        if (null != links) {

	            // Search throw links
	            ArrayList<IntermediateLink> throwLinks = new ArrayList<IntermediateLink>();
	            for (IntermediateLink aLinks : links) {
	                if (aLinks.isThrowLink()) {
	                    throwLinks.add(aLinks);
	                }
	            }

	            // Look for catch links for a throw link
	            for (IntermediateLink throwLink : throwLinks) {

	                ArrayList<IntermediateLink> linksWithSharedNames = new ArrayList<IntermediateLink>();
	                for (IntermediateLink aLink : links) {
	                    if (throwLink.getName().equals(aLink.getName())) {
	                        linksWithSharedNames.add(aLink);
	                    }
	                }

	                if (linksWithSharedNames.size() < 2) {
	                    throw new IllegalArgumentException(
	                            "There should be at least 2 link events to make a connection");
	                }

	                linksWithSharedNames.remove(throwLink);

	                // Make the connections
	                Node t = findNodeByIdOrUniqueIdInMetadata(process,
	                        throwLink.getUniqueId());

	                // connect throw to catch
	                for (IntermediateLink catchLink : linksWithSharedNames) {

	                    Node c = findNodeByIdOrUniqueIdInMetadata(process,
	                            catchLink.getUniqueId());
	                    if (t != null && c != null) {
	                        Connection result = new ConnectionImpl(t,
	                                NodeImpl.CONNECTION_DEFAULT_TYPE, c,
	                                NodeImpl.CONNECTION_DEFAULT_TYPE);
	                        result.setMetaData("linkNodeHidden", "yes");
	                    }
	                }

	                // Remove processed links
	                links.remove(throwLink);
	                links.removeAll(linksWithSharedNames);
	            }

	            if (links.size() > 0) {
	                throw new IllegalArgumentException(links.size()
	                        + " links were not processed");
	            }

	        }
	    }
	 
	 
	  private static Node findNodeByIdOrUniqueIdInMetadata(
	            NodeContainer nodeContainer, String targetRef) {

	        try {
	            // remove starting _
	            String targetId = targetRef.substring(1);
	            // remove ids of parent nodes
	            targetId = targetId.substring(targetId.lastIndexOf("-") + 1);
	            return nodeContainer.getNode(new Integer(targetId));
	        } catch (NumberFormatException e) {
	            // try looking for a node with same "UniqueId" (in metadata)
	            Node targetNode = null;
	            for (Node node : nodeContainer.getNodes()) {
	                if (targetRef.equals(node.getMetaData().get("UniqueId"))) {
	                    targetNode = node;
	                    break;
	                }
	            }

	            if (targetNode != null) {
	                return targetNode;
	            } else {
	                throw new IllegalArgumentException(
	                        "Could not find target node for connection:"
	                                + targetRef);
	            }
	        }

	    }


	public Class<?> generateNodeFor() {
		return RuleFlowProcess.class;
	}
	
	public static void linkConnections(NodeContainer nodeContainer, List<SequenceFlow> connections) {
		if (connections != null) {
			for (SequenceFlow connection: connections) {
				String sourceRef = connection.getSourceRef();
				String targetRef = connection.getTargetRef();
				Node source = null;
				Node target = null;
				try {
    				// remove starting _
    				sourceRef = sourceRef.substring(1);
    				// remove ids of parent nodes
    				sourceRef = sourceRef.substring(sourceRef.lastIndexOf("-") + 1);
    				source = nodeContainer.getNode(new Integer(sourceRef));
				} catch (NumberFormatException e) {
				    // try looking for a node with same "UniqueId" (in metadata)
				    for (Node node: nodeContainer.getNodes()) {
				        if (connection.getSourceRef().equals(node.getMetaData().get("UniqueId"))) {
				            source = node;
				            break;
				        }
				    }
                    if (source == null) {
                        throw new IllegalArgumentException("Could not find source node for connection:" + connection.getSourceRef());
                    }
				}
				try {
    				// remove starting _
    				targetRef = targetRef.substring(1);
    		        // remove ids of parent nodes
    				targetRef = targetRef.substring(targetRef.lastIndexOf("-") + 1);
    				target = nodeContainer.getNode(new Integer(targetRef));
				} catch (NumberFormatException e) {
				    // try looking for a node with same "UniqueId" (in metadata)
                    for (Node node: nodeContainer.getNodes()) {
                        if (connection.getTargetRef().equals(node.getMetaData().get("UniqueId"))) {
                            target = node;
                            break;
                        }
                    }
                    if (target == null) {
                        throw new IllegalArgumentException("Could not find target node for connection:" + connection.getTargetRef());
                    }
				}
				Connection result = new ConnectionImpl(
					source, NodeImpl.CONNECTION_DEFAULT_TYPE, 
					target, NodeImpl.CONNECTION_DEFAULT_TYPE);
				result.setMetaData("bendpoints", connection.getBendpoints());
				result.setMetaData("UniqueId", connection.getId());
				
				if (System.getProperty("jbpm.enable.multi.con") != null){
					NodeImpl nodeImpl = (NodeImpl) source;
					Constraint constraint = buildConstraint(connection, nodeImpl);
					if (constraint != null) {
						nodeImpl.addConstraint(new ConnectionRef(target.getId(), NodeImpl.CONNECTION_DEFAULT_TYPE),
								constraint);
					}
					
				} else if (source instanceof Split) {
					Split split = (Split) source;
					Constraint constraint = buildConstraint(connection, split);
					split.addConstraint(
						new ConnectionRef(target.getId(), NodeImpl.CONNECTION_DEFAULT_TYPE),
						constraint);
				}
			}
		}
	}
	
    public static void linkBoundaryEvents(NodeContainer nodeContainer) {
        for (Node node: nodeContainer.getNodes()) {
            if (node instanceof EventNode) {
                final String attachedTo = (String) node.getMetaData().get("AttachedTo");
                if (attachedTo != null) {
                	String type = ((EventTypeFilter)
                        ((EventNode) node).getEventFilters().get(0)).getType();
                    Node attachedNode = null;
                    try {
                        // remove starting _
                        String attachedToString = attachedTo.substring(1);
                        // remove ids of parent nodes
                        attachedToString = attachedToString.substring(attachedToString.lastIndexOf("-") + 1);
                        attachedNode = nodeContainer.getNode(new Integer(attachedToString));
                    } catch (NumberFormatException e) {
                        // try looking for a node with same "UniqueId" (in metadata)
                        for (Node subnode: nodeContainer.getNodes()) {
                            if (attachedTo.equals(subnode.getMetaData().get("UniqueId"))) {
                                attachedNode = subnode;
                                break;
                            }
                        }
                        if (attachedNode == null) {
                            throw new IllegalArgumentException("Could not find node to attach to: " + attachedTo);
                        }
                    }
                    // 
                    if (!(attachedNode instanceof StateBasedNode) && !type.startsWith("Compensate-")) {
                        throw new IllegalArgumentException("Boundary events are supported only on StateBasedNode, found node: " + 
                        		attachedNode.getClass().getName());
                    }
                    
                    if (type.startsWith("Escalation-")) {
                        boolean cancelActivity = (Boolean) node.getMetaData().get("CancelActivity");
                        String escalationCode = (String) node.getMetaData().get("EscalationEvent");
                        
                        ContextContainer compositeNode = (ContextContainer) attachedNode;
                        ExceptionScope exceptionScope = (ExceptionScope) 
                            compositeNode.getDefaultContext(ExceptionScope.EXCEPTION_SCOPE);
                        if (exceptionScope == null) {
                            exceptionScope = new ExceptionScope();
                            compositeNode.addContext(exceptionScope);
                            compositeNode.setDefaultContext(exceptionScope);
                        }
                        
                        ActionExceptionHandler exceptionHandler = new ActionExceptionHandler();
                        DroolsConsequenceAction action = null;
                        
                        if (attachedNode instanceof CompositeContextNode) {
                            action = new DroolsConsequenceAction("java",
                                (cancelActivity ? "((org.jbpm.workflow.instance.NodeInstance) kcontext.getNodeInstance()).cancel();" : "") +
                                "kcontext.getProcessInstance().signalEvent(\"Escalation-" + attachedTo + "-" + escalationCode + "\", null);");
                        } else {
                            long attachedToNodeId = attachedNode.getId();
                            
                            
                            action = new DroolsConsequenceAction("java", 
                                    (cancelActivity ? "org.kie.runtime.process.WorkflowProcessInstance pi = (org.kie.runtime.process.WorkflowProcessInstance) kcontext.getProcessInstance();"+
                                    "long nodeInstanceId = -1;"+
                                    "for (org.kie.runtime.process.NodeInstance nodeInstance : pi.getNodeInstances()) {"+
                                     "   if (" +attachedToNodeId +" == nodeInstance.getNodeId()) {"+
                                     "       nodeInstanceId = nodeInstance.getId();"+
                                     "       break;"+
                                     "   }"+
                                    "}"+
                                    "    ((org.jbpm.workflow.instance.NodeInstance)((org.jbpm.workflow.instance.NodeInstanceContainer) context.getProcessInstance()).getNodeInstance(nodeInstanceId)).cancel();"+
                                    "kcontext.getProcessInstance().signalEvent(\"Escalation-" + attachedTo + "-" + escalationCode + "\", null);" 
                                    : "kcontext.getProcessInstance().signalEvent(\"Escalation-" + attachedTo + "-" + escalationCode + "\", null);"));
                            
                        }
                        
                        exceptionHandler.setAction(action);
                        exceptionScope.setExceptionHandler(escalationCode, exceptionHandler);
                       
                    } else if (type.startsWith("Error-")) {
                        ContextContainer compositeNode = (ContextContainer) attachedNode;
                        ExceptionScope exceptionScope = (ExceptionScope) 
                            compositeNode.getDefaultContext(ExceptionScope.EXCEPTION_SCOPE);
                        if (exceptionScope == null) {
                            exceptionScope = new ExceptionScope();
                            compositeNode.addContext(exceptionScope);
                            compositeNode.setDefaultContext(exceptionScope);
                        }
                        String errorCode = (String) node.getMetaData().get("ErrorEvent");
                        ActionExceptionHandler exceptionHandler = new ActionExceptionHandler();

                        DroolsConsequenceAction action = null;
                        
                        if (attachedNode instanceof CompositeContextNode) {
                            action = new DroolsConsequenceAction("java",
                                    "((org.jbpm.workflow.instance.NodeInstance) kcontext.getNodeInstance()).cancel();" +
                                    "kcontext.getProcessInstance().signalEvent(\"Error-" + attachedTo + "-" + errorCode + "\", null);");
                        } else {
                            long attachedToNodeId = attachedNode.getId();
                            
                            
                            action = new DroolsConsequenceAction("java", 
                                    "org.kie.runtime.process.WorkflowProcessInstance pi = (org.kie.runtime.process.WorkflowProcessInstance) kcontext.getProcessInstance();"+
                                    "long nodeInstanceId = -1;"+
                                    "for (org.kie.runtime.process.NodeInstance nodeInstance : pi.getNodeInstances()) {"+
                                    
                                     "   if (" +attachedToNodeId +" == nodeInstance.getNodeId()) {"+
                                     "       nodeInstanceId = nodeInstance.getId();"+
                                     "       break;"+
                                     "   }"+
                                    "}" +
                                    "if (nodeInstanceId > -1) {((org.jbpm.workflow.instance.NodeInstance)((org.jbpm.workflow.instance.NodeInstanceContainer) kcontext.getProcessInstance()).getNodeInstance(nodeInstanceId)).cancel();}"+
                                    "kcontext.getProcessInstance().signalEvent(\"Error-" + attachedTo + "-" + errorCode + "\", null);");
                            
                        }
                        
                        exceptionHandler.setAction(action);
                        exceptionScope.setExceptionHandler(errorCode, exceptionHandler);
                    } else if (type.startsWith("Timer-")) {
                        boolean cancelActivity = (Boolean) node.getMetaData().get("CancelActivity");
                        StateBasedNode compositeNode = (StateBasedNode) attachedNode;
                        String timeDuration = (String) node.getMetaData().get("TimeDuration");
                        String timeCycle = (String) node.getMetaData().get("TimeCycle");
                        String timeDate = (String) node.getMetaData().get("TimeDate");
                        Timer timer = new Timer();
                        if (timeDuration != null) {
                        	timer.setDelay(timeDuration);
                        	timer.setTimeType(Timer.TIME_DURATION);
                            compositeNode.addTimer(timer, new DroolsConsequenceAction("java",
                                (cancelActivity ? "((org.jbpm.workflow.instance.NodeInstance) kcontext.getNodeInstance()).cancel();" : "") +
                                "kcontext.getProcessInstance().signalEvent(\"Timer-" + attachedTo + "-" + timeDuration + "\", null);"));
                        } else if (timeCycle != null) {
                        	int index = timeCycle.indexOf("###");
                        	if (index != -1) {
                        		String period = timeCycle.substring(index + 3);
                        		timeCycle = timeCycle.substring(0, index);
                                timer.setPeriod(period);
                        	}
                        	timer.setDelay(timeCycle);
                        	timer.setTimeType(Timer.TIME_CYCLE);
                            compositeNode.addTimer(timer, new DroolsConsequenceAction("java",
                                (cancelActivity ? "((org.jbpm.workflow.instance.NodeInstance) kcontext.getNodeInstance()).cancel();" : "") +
                                "kcontext.getProcessInstance().signalEvent(\"Timer-" + attachedTo + "-" + timeCycle + (timer.getPeriod() == null ? "" : "###" + timer.getPeriod()) + "\", null);"));
                        } else if (timeDate != null) {
                            timer.setDate(timeDate);
                            timer.setTimeType(Timer.TIME_DATE);
                            compositeNode.addTimer(timer, new DroolsConsequenceAction("java",
                                (cancelActivity ? "((org.jbpm.workflow.instance.NodeInstance) kcontext.getNodeInstance()).cancel();" : "") +
                                "kcontext.getProcessInstance().signalEvent(\"Timer-" + attachedTo + "-" + timeDate + "\", null);"));
                        }
                    } else if (type.startsWith("Compensate-")) {
                    	String activityRef = (String) node.getMetaData().get("ActivityRef");
                    	if (activityRef == null) {
                    	    activityRef = attachedTo;
                    	}
            	        String eventType = "Compensate-" + activityRef;
            	        ((EventTypeFilter) ((EventNode) node).getEventFilters().get(0)).setType(eventType);
                    } else if (node.getMetaData().get("SignalName") != null || type.startsWith("Message-")) {
                        boolean cancelActivity = (Boolean) node.getMetaData().get("CancelActivity");
                        final long attachedToNodeId = attachedNode.getId();
                        if (cancelActivity) {
                            List<DroolsAction> actions = ((EventNode)node).getActions(EndNode.EVENT_NODE_EXIT);
                            if (actions == null) {
                                actions = new ArrayList<DroolsAction>();
                            }
                            DroolsConsequenceAction action =  new DroolsConsequenceAction("java", null);
                            action.setMetaData("Action", new CancelNodeInstanceAction(attachedToNodeId));
                            actions.add(action);
                            ((EventNode)node).setActions(EndNode.EVENT_NODE_EXIT, actions);
                        }
                        // cancel boundary event when node is completed by removing filter
                        final long id = node.getId();
                        StateBasedNode stateBasedNode = (StateBasedNode) attachedNode;
                        
                        List<DroolsAction> actionsAttachedTo = stateBasedNode.getActions(StateBasedNode.EVENT_NODE_EXIT);
                        if (actionsAttachedTo == null) {
                            actionsAttachedTo = new ArrayList<DroolsAction>();
                        }
                        DroolsConsequenceAction actionAttachedTo =  new DroolsConsequenceAction("java", "" +
                        		"org.kie.definition.process.Node node = context.getNodeInstance().getNode().getNodeContainer().getNode(" +id+ ");" +
                        		"if (node instanceof org.jbpm.workflow.core.node.EventNode) {" +
                        		" ((org.jbpm.workflow.core.node.EventNode)node).getEventFilters().clear();" +
                        		"((org.jbpm.workflow.core.node.EventNode)node).addEventFilter(new org.jbpm.process.core.event.EventFilter () " +
                        		"{public boolean acceptsEvent(String type, Object event) { return false;}});" +
                        		"}");
                     

                        actionsAttachedTo.add(actionAttachedTo);
                        stateBasedNode.setActions(StateBasedNode.EVENT_NODE_EXIT, actionsAttachedTo);
                        
                    } else if (type.startsWith("Condition-")) {
                        String processId = ((RuleFlowProcess) nodeContainer).getId();
                        String eventType = "RuleFlowStateEvent-" + processId + "-" + ((EventNode) node).getUniqueId() + "-" + attachedTo;
                        ((EventTypeFilter) ((EventNode) node).getEventFilters().get(0)).setType(eventType);
                        final long attachedToNodeId = attachedNode.getId();
                        boolean cancelActivity = (Boolean) node.getMetaData().get("CancelActivity");
                        if (cancelActivity) {
                            List<DroolsAction> actions = ((EventNode)node).getActions(EndNode.EVENT_NODE_EXIT);
                            if (actions == null) {
                                actions = new ArrayList<DroolsAction>();
                            }
                            DroolsConsequenceAction action =  new DroolsConsequenceAction("java", null);
                            action.setMetaData("Action", new CancelNodeInstanceAction(attachedToNodeId));
                            actions.add(action);
                            ((EventNode)node).setActions(EndNode.EVENT_NODE_EXIT, actions);
                        }
                        
                        // cancel boundary event when node is completed by removing filter
                        final long id = node.getId();
                        StateBasedNode stateBasedNode = (StateBasedNode) attachedNode;
                        
                        List<DroolsAction> actionsAttachedTo = stateBasedNode.getActions(StateBasedNode.EVENT_NODE_EXIT);
                        if (actionsAttachedTo == null) {
                            actionsAttachedTo = new ArrayList<DroolsAction>();
                        }
                        DroolsConsequenceAction actionAttachedTo =  new DroolsConsequenceAction("java", "" +
                                "org.kie.definition.process.Node node = context.getNodeInstance().getNode().getNodeContainer().getNode(" +id+ ");" +
                                "if (node instanceof org.jbpm.workflow.core.node.EventNode) {" +
                                " ((org.jbpm.workflow.core.node.EventNode)node).getEventFilters().clear();" +
                                "((org.jbpm.workflow.core.node.EventNode)node).addEventFilter(new org.jbpm.process.core.event.EventFilter () " +
                                "{public boolean acceptsEvent(String type, Object event) { return false;}});" +
                                "}");
                     

                        actionsAttachedTo.add(actionAttachedTo);
                        stateBasedNode.setActions(StateBasedNode.EVENT_NODE_EXIT, actionsAttachedTo);
                        stateBasedNode.addBoundaryEvents(eventType);
                    }
                }
            }
        }
    }
    
	private void assignLanes(RuleFlowProcess process, List<Lane> lanes) {
	    List<String> laneNames = new ArrayList<String>();
	    Map<String, String> laneMapping = new HashMap<String, String>();
	    if (lanes != null) {
	        for (Lane lane: lanes) {
	            String name = lane.getName();
	            if (name != null) {
	                Swimlane swimlane = new Swimlane();
	                swimlane.setName(name);
	                process.getSwimlaneContext().addSwimlane(swimlane);
	                laneNames.add(name);
	                for (String flowElementRef: lane.getFlowElements()) {
	                    laneMapping.put(flowElementRef, name);
	                }
	            }
	        }
	    }
	    assignLanes(process, laneMapping);
	}
	
    private void postProcessNodes(NodeContainer container) {
        for (Node node: container.getNodes()) {
            if (node instanceof StateNode) {
                StateNode stateNode = (StateNode) node;
                String condition = (String) stateNode.getMetaData("Condition");
                Constraint constraint = new ConstraintImpl();
                constraint.setConstraint(condition);
                constraint.setType("rule");
                for (org.kie.definition.process.Connection connection: stateNode.getDefaultOutgoingConnections()) {
                    stateNode.setConstraint(connection, constraint);
                }
            } else if (node instanceof NodeContainer) {
                postProcessNodes((NodeContainer) node);
            }
        }
    }
    
	private void assignLanes(NodeContainer nodeContainer, Map<String, String> laneMapping) {
	    for (Node node: nodeContainer.getNodes()) {
	        String lane = null;
	        String uniqueId = (String) node.getMetaData().get("UniqueId");
	        if (uniqueId != null) {
	            lane = laneMapping.get(uniqueId);
	        } else {
	            lane = laneMapping.get(XmlBPMNProcessDumper.getUniqueNodeId(node));
	        }
	        if (lane != null) {
	            ((NodeImpl) node).setMetaData("Lane", lane);
	            if (node instanceof HumanTaskNode) {
	                ((HumanTaskNode) node).setSwimlane(lane);
	            }
	        }
	        if (node instanceof NodeContainer) {
	            assignLanes((NodeContainer) node, laneMapping);
	        }
	    }
	}
	
	private static Constraint buildConstraint(SequenceFlow connection, NodeImpl node) {
		if (connection.getExpression() == null) {
			return null;
		}
		
		Constraint constraint = new ConstraintImpl();
		String defaultConnection = (String) node.getMetaData("Default");
		if (defaultConnection != null && defaultConnection.equals(connection.getId())) {
			constraint.setDefault(true);
		}
		if (connection.getName() != null) {
			constraint.setName(connection.getName());
		} else {
			constraint.setName("");
		}
		if (connection.getType() != null) {
			constraint.setType(connection.getType());
		} else {
			constraint.setType("code");
		}
		if (connection.getLanguage() != null) {
			constraint.setDialect(connection.getLanguage());
		}
		if (connection.getExpression() != null) {
			constraint.setConstraint(connection.getExpression());
		}
		constraint.setPriority(connection.getPriority());
		
		return constraint;
	}

}