/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.examples.checklist.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.process.core.Work;
import org.jbpm.examples.checklist.ChecklistItem;
import org.jbpm.examples.checklist.ChecklistItem.Status;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.StartNode;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;

public final class ChecklistItemFactory {
	
	public static ChecklistItem createChecklistItem(Task task) {
		return new DefaultChecklistItem(
			getText(task.getNames()), 
			getStatus(task.getTaskData().getStatus()),
			task.getId(),
			"HumanTaskNode",
			getActors(task),
			task.getPriority(),
			task.getTaskData().getProcessId(),
			task.getTaskData().getProcessInstanceId(),
			getText(task.getDescriptions()));
	}
	
	private static String getText(List<I18NText> texts) {
		if (texts == null) {
			return null;
		}
		for (I18NText text: texts) {
			if ("en-UK".equals(text.getLanguage())) {
				return text.getText();
			}
		}
		return null;
	}
	
	private static Status getStatus(org.kie.api.task.model.Status status) {
		switch (status) {
			case Completed: return Status.Completed;
			case Ready: 
			case Created: return Status.Created;
			case Error:
			case Exited:
			case Failed:
			case Obsolete: return Status.Aborted;
			case Suspended:
			case InProgress: return Status.InProgress;
			case Reserved: return Status.Reserved;
			default: return null;
		}
	}
	
	private static String getActors(Task task) {
		User actualOwner = task.getTaskData().getActualOwner();
		if (actualOwner != null) {
			return actualOwner.getId();
		} else {
			String result = "";
			for (OrganizationalEntity o: task.getPeopleAssignments().getPotentialOwners()) {
				if (result.length() != 0) {
					result += ",";
				}
				result += o.getId();
			}
			return result;
		}
	}
	
	public static Collection<ChecklistItem> getPendingChecklistItems(WorkflowProcess process) {
		List<ChecklistItem> result = new ArrayList<ChecklistItem>();
		getPendingChecklistItems(process, result, process.getId());
		return result;
	}
	
	private static void getPendingChecklistItems(NodeContainer container, List<ChecklistItem> result, String processId) {
		for (Node node: container.getNodes()) {
			if (node instanceof HumanTaskNode) {
				Work workItem = ((HumanTaskNode) node).getWork();
				int priority = 0;
				String priorityString = (String) workItem.getParameter("Priority");
				if (priorityString != null) {
					try {
						priority = new Integer(priorityString);
					} catch (NumberFormatException e) {
						// Do nothing
					}
				}
				String actorId = (String) workItem.getParameter("ActorId");
				if (actorId != null && actorId.trim().length() == 0) {
					actorId = null;
				}
				String groupId = (String) workItem.getParameter("GroupId");
				if (groupId != null && groupId.trim().length() == 0) {
					groupId = null;
				}
				String actors = null;
				if (actorId == null) {
					if (groupId == null) {
						actors = "";
					} else { 
						actors = groupId;
					}
				} else {
					if (groupId == null) {
						actors = actorId;
					} else {
						actors = actorId + "," + groupId;
					}
				}
				Status status = Status.Pending;
				if (((HumanTaskNode) node).getDefaultIncomingConnections().size() == 0) {
					status = Status.Optional;
				}
				result.add(
					createChecklistItem(
						(String) workItem.getParameter("TaskName"),
						"HumanTaskNode",
						actors,
						(String) workItem.getParameter("Comment"),
						priority, processId, status));
			} else if (node instanceof NodeContainer) {
				getPendingChecklistItems((NodeContainer) node, result, processId);
			} else {
				String docs = (String) node.getMetaData().get("Documentation");
				if (docs != null) {
					int position = docs.indexOf("OrderingNb=");
					if (position >= 0) {
						int end = docs.indexOf(";", position + 1);
						String orderingNumber = docs.substring(position + 11, end);
						Status status = Status.Pending;
						if (((NodeImpl) node).getDefaultIncomingConnections().size() == 0 && !(node instanceof StartNode)) {
							status = Status.Optional;
						}
						result.add(
							createChecklistItem(
								node.getName(), 
								node.getClass().getSimpleName(),
								"",
								orderingNumber,
								0, 
								processId, 
								status));
					}
				}
			}
		}
	}
	
	public static Collection<ChecklistItem> getLoggedChecklistItems(WorkflowProcess process, List<NodeInstanceLog> nodeInstances) {
		Map<String, ChecklistItem> result = new HashMap<String, ChecklistItem>();
		Map<String, String> relevantNodes = new HashMap<String, String>();
		getRelevantNodes(process, relevantNodes);
		for (NodeInstanceLog log: nodeInstances) {
			String orderingNb = relevantNodes.get(log.getNodeId());
			if (orderingNb != null) {
				if (log.getType() == NodeInstanceLog.TYPE_EXIT) {
					result.put(orderingNb, createChecklistItem(log.getNodeName(), log.getNodeType(), "", orderingNb, 0, log.getProcessId(), Status.Completed));
				} else {
					if (result.get(orderingNb) == null) {
						result.put(orderingNb, createChecklistItem(log.getNodeName(), log.getNodeType(), "", orderingNb, 0, log.getProcessId(), Status.InProgress));
					}
				}
			}
		}
		return result.values();
	}
	
	private static void getRelevantNodes(NodeContainer container, Map<String, String> result) {
		for (Node node: container.getNodes()) {
			if (node instanceof NodeContainer) {
				getRelevantNodes((NodeContainer) node, result);
			}
			String docs = (String) node.getMetaData().get("Documentation");
			if (docs != null) {
				int position = docs.indexOf("OrderingNb=");
				if (position >= 0) {
					int end = docs.indexOf(";", position + 1);
					String orderingNumber = docs.substring(position + 11, end);
					String nodeId = (String)node.getMetaData().get("UniqueId");
					if (nodeId == null) {
						nodeId = ((NodeImpl) node).getUniqueId();
					}
					result.put(nodeId, orderingNumber); 
				}
			}
		}
	}
	
	private static ChecklistItem createChecklistItem(String name, String type, String actors, String orderingNb, long priority, String processId, Status status) {
		return new DefaultChecklistItem(
			name == null ? "" : name, 
			status,
			null,
			type,
			actors == null ? "" : actors,
			priority,
			processId,
			null,
			orderingNb);
	}
	
}
