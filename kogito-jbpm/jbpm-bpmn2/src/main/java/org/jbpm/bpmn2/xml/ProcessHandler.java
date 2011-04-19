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

import org.drools.definition.process.Node;
import org.drools.definition.process.NodeContainer;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.jbpm.bpmn2.core.DataStore;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.bpmn2.core.Error;
import org.jbpm.bpmn2.core.Escalation;
import org.jbpm.bpmn2.core.Interface;
import org.jbpm.bpmn2.core.ItemDefinition;
import org.jbpm.bpmn2.core.Lane;
import org.jbpm.bpmn2.core.Message;
import org.jbpm.bpmn2.core.SequenceFlow;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.process.core.context.exception.ActionExceptionHandler;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.core.context.swimlane.Swimlane;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Connection;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.impl.ConstraintImpl;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StateNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ProcessHandler extends BaseAbstractHandler implements Handler {
	
	public static final String CONNECTIONS = "BPMN.Connections";

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
		return process;
	}

	@SuppressWarnings("unchecked")
	public Object end(final String uri, final String localName,
			          final ExtensibleXmlParser parser) throws SAXException {
		parser.endElementBuilder();
		RuleFlowProcess process = (RuleFlowProcess) parser.getCurrent();
		List<SequenceFlow> connections = (List<SequenceFlow>)
			process.getMetaData(CONNECTIONS);
		linkConnections(process, connections);
		linkBoundaryEvents(process);
        List<Lane> lanes = (List<Lane>)
            process.getMetaData(LaneHandler.LANES);
        assignLanes(process, lanes);
        postProcessNodes(process);
		return process;
	}

	public Class<?> generateNodeFor() {
		return org.drools.definition.process.Process.class;
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
				if (source instanceof Split) {
					Split split = (Split) source;
					Constraint constraint = new ConstraintImpl();
					String defaultConnection = (String) split.getMetaData("Default");
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
                String attachedTo = (String) node.getMetaData().get("AttachedTo");
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
                    if (type.startsWith("Escalation-")) {
                        boolean cancelActivity = (Boolean) node.getMetaData().get("CancelActivity");
                        CompositeContextNode compositeNode = (CompositeContextNode) attachedNode;
                        ExceptionScope exceptionScope = (ExceptionScope) 
                            compositeNode.getDefaultContext(ExceptionScope.EXCEPTION_SCOPE);
                        if (exceptionScope == null) {
                            exceptionScope = new ExceptionScope();
                            compositeNode.addContext(exceptionScope);
                            compositeNode.setDefaultContext(exceptionScope);
                        }
                        String escalationCode = (String) node.getMetaData().get("EscalationEvent");
                        ActionExceptionHandler exceptionHandler = new ActionExceptionHandler();
                        exceptionHandler.setAction(new DroolsConsequenceAction("java",
                            (cancelActivity ? "((org.jbpm.workflow.instance.NodeInstance) kcontext.getNodeInstance()).cancel();" : "") +
                            "kcontext.getProcessInstance().signalEvent(\"Escalation-" + attachedTo + "-" + escalationCode + "\", null);"));
                        exceptionScope.setExceptionHandler(escalationCode, exceptionHandler);
                    } else if (type.startsWith("Error-")) {
                        CompositeContextNode compositeNode = (CompositeContextNode) attachedNode;
                        ExceptionScope exceptionScope = (ExceptionScope) 
                            compositeNode.getDefaultContext(ExceptionScope.EXCEPTION_SCOPE);
                        if (exceptionScope == null) {
                            exceptionScope = new ExceptionScope();
                            compositeNode.addContext(exceptionScope);
                            compositeNode.setDefaultContext(exceptionScope);
                        }
                        String errorCode = (String) node.getMetaData().get("ErrorEvent");
                        ActionExceptionHandler exceptionHandler = new ActionExceptionHandler();
                        exceptionHandler.setAction(new DroolsConsequenceAction("java",
                            "((org.jbpm.workflow.instance.NodeInstance) kcontext.getNodeInstance()).cancel();" +
                            "kcontext.getProcessInstance().signalEvent(\"Error-" + attachedTo + "-" + errorCode + "\", null);"));
                        exceptionScope.setExceptionHandler(errorCode, exceptionHandler);
                    } else if (type.startsWith("Timer-")) {
                        boolean cancelActivity = (Boolean) node.getMetaData().get("CancelActivity");
                        CompositeContextNode compositeNode = (CompositeContextNode) attachedNode;
                        String timeDuration = (String) node.getMetaData().get("TimeDuration");
                        String timeCycle = (String) node.getMetaData().get("TimeCycle");
                        Timer timer = new Timer();
                        if (timeDuration != null) {
                        	timer.setDelay(timeDuration);
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
                            compositeNode.addTimer(timer, new DroolsConsequenceAction("java",
                                (cancelActivity ? "((org.jbpm.workflow.instance.NodeInstance) kcontext.getNodeInstance()).cancel();" : "") +
                                "kcontext.getProcessInstance().signalEvent(\"Timer-" + attachedTo + "-" + timeCycle + (timer.getPeriod() == null ? "" : "###" + timer.getPeriod()) + "\", null);"));
                        }
                    } else if (type.startsWith("Compensate-")) {
                    	String uniqueId = (String) node.getMetaData().get("UniqueId");
            	        String eventType = "Compensate-" + uniqueId;
            	        ((EventTypeFilter) ((EventNode) node).getEventFilters().get(0)).setType(eventType);
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
                for (org.drools.definition.process.Connection connection: stateNode.getDefaultOutgoingConnections()) {
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

}