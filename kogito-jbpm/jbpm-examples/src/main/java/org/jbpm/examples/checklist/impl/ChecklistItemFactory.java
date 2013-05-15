package org.jbpm.examples.checklist.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.process.core.Work;
import org.jbpm.examples.checklist.ChecklistItem;
import org.jbpm.examples.checklist.ChecklistItem.Status;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;
import org.kie.api.definition.process.WorkflowProcess;
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
	
	public static List<ChecklistItem> getPendingChecklistItems(WorkflowProcess process) {
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
				result.add(
					createPendingChecklistItem(
						(String) workItem.getParameter("TaskName"), 
						actors,
						(String) workItem.getParameter("Comment"),
						priority, processId));
			} else if (node instanceof NodeContainer) {
				getPendingChecklistItems((NodeContainer) node, result, processId);
			}
		}
	}
	
	private static ChecklistItem createPendingChecklistItem(String name, String actors, String orderingNb, long priority, String processId) {
		return new DefaultChecklistItem(
			name == null ? "" : name, 
			Status.Pending,
			null,
			actors == null ? "" : actors,
			priority,
			processId,
			null,
			orderingNb);
	}
	
}
