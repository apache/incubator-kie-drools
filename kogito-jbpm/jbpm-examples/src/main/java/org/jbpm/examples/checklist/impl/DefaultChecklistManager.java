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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.examples.checklist.ChecklistContext;
import org.jbpm.examples.checklist.ChecklistContextConstraint;
import org.jbpm.examples.checklist.ChecklistItem;
import org.jbpm.examples.checklist.ChecklistManager;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalI18NText;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.InternalTaskData;

public class DefaultChecklistManager implements ChecklistManager {

	private RuntimeManager manager;
	private List<ChecklistContext> contexts = new ArrayList<ChecklistContext>();
	private RuntimeEnvironment environment;
	
	public DefaultChecklistManager(RuntimeEnvironment environment) {
		this.environment = environment;
	}
	
	public List<ChecklistContext> getContexts() {
		return contexts;
	}

	public long createContext(String name, String userId) {
		RuntimeEngine runtime = getRuntime();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startUser", userId);
		ProcessInstance processInstance = runtime.getKieSession().startProcess(
			name == null ? "org.jbpm.examples.checklist.AdHocProcess" : name, params);
		manager.disposeRuntimeEngine(runtime);
		ChecklistContext context = new DefaultChecklistContext();
		contexts.add(context);
		return processInstance.getId();
	}

	public List<ChecklistItem> getTasks(long processInstanceId, List<ChecklistContextConstraint> contexts) {
		List<ChecklistItem> items = getTasks(processInstanceId);
		List<ChecklistItem> results = new ArrayList<ChecklistItem>();
		for (ChecklistItem item: items) {
			if (contexts != null) {
				for (ChecklistContextConstraint context: contexts) {
					if (!context.acceptsTask(item)) {
						break;
					}
				}
			}
			results.add(item);
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public List<ChecklistItem> getTasks(long processInstanceId) {
		RuntimeEngine runtime = getRuntime();
		KieSession ksession = runtime.getKieSession();
		ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
		Map<String, ChecklistItem> orderingIds = new HashMap<String, ChecklistItem>();
		if (processInstance != null) {
			WorkflowProcess process = (WorkflowProcess)
				ksession.getKieBase().getProcess(processInstance.getProcessId());
			Collection<ChecklistItem> result = ChecklistItemFactory.getPendingChecklistItems(process);
			result.addAll(ChecklistItemFactory.getLoggedChecklistItems(process, (List<NodeInstanceLog>) runtime.getAuditService().findNodeInstances(processInstance.getId())));
			for (ChecklistItem item: result) {
				if (item.getOrderingNb() != null && item.getOrderingNb().trim().length() > 0) { 
					orderingIds.put(item.getOrderingNb(), item);
				}
			}
		}
		TaskService taskService = runtime.getTaskService();
		List<Long> taskIds = taskService.getTasksByProcessInstanceId(processInstanceId);
		List<ChecklistItem> result = new ArrayList<ChecklistItem>();
		for (Long taskId: taskIds) {
			Task task = taskService.getTaskById(taskId);
			if (task != null) {
				ChecklistItem item = ChecklistItemFactory.createChecklistItem(task);
				if (item.getOrderingNb() != null) {
					orderingIds.put(item.getOrderingNb(), item);
				} else {
					result.add(item);
				}
			}
		}
		for (ChecklistItem item: orderingIds.values()) {
			result.add(item);
		}
		Collections.sort(result, new Comparator<ChecklistItem>() {
			public int compare(ChecklistItem o1, ChecklistItem o2) {
				if (o1.getOrderingNb() != null && o2.getOrderingNb() != null) {
					return o1.getOrderingNb().compareTo(o2.getOrderingNb());
				} else if (o1.getTaskId() != null && o2.getTaskId() != null) {
					return o1.getTaskId().compareTo(o2.getTaskId());
				} else {
					throw new IllegalArgumentException();
				}
			}
		});
		manager.disposeRuntimeEngine(runtime);
		return result;
	}

	public ChecklistItem addTask(String userId, String[] actorIds, String[] groupIds, String name, String orderingId, long processInstanceId) {
		RuntimeEngine runtime = getRuntime();
		
		InternalTask task = (InternalTask) TaskModelProvider.getFactory().newTask();;
        setTaskName(task, name);
        setTaskDescription(task, orderingId);
        //task.setPriority(priority);
        InternalTaskData taskData = (InternalTaskData) TaskModelProvider.getFactory().newTaskData(); 
        taskData.setProcessInstanceId(processInstanceId);
        // taskData.setProcessSessionId(sessionId);
        taskData.setSkipable(false);
        taskData.setDeploymentId("default-singleton");
        User cuser = TaskModelProvider.getFactory().newUser();
    	((InternalOrganizationalEntity) cuser).setId(userId); 
        taskData.setCreatedBy(cuser);
        task.setTaskData(taskData);
        
        InternalPeopleAssignments peopleAssignments = (InternalPeopleAssignments) task.getPeopleAssignments();
        if (peopleAssignments == null) {
        	peopleAssignments = (InternalPeopleAssignments) TaskModelProvider.getFactory().newPeopleAssignments();
        	peopleAssignments.setPotentialOwners(new ArrayList<OrganizationalEntity>());
        	peopleAssignments.setBusinessAdministrators(new ArrayList<OrganizationalEntity>());
        	peopleAssignments.setExcludedOwners(new ArrayList<OrganizationalEntity>());
        	peopleAssignments.setRecipients(new ArrayList<OrganizationalEntity>());
        	peopleAssignments.setTaskStakeholders(new ArrayList<OrganizationalEntity>());
        	task.setPeopleAssignments(peopleAssignments);
        }

        List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();
        for (String actorId: actorIds) {
        	User user = TaskModelProvider.getFactory().newUser();
        	((InternalOrganizationalEntity) user).setId(actorId); 
        	potentialOwners.add(user);
        }
        for (String groupId: groupIds) {
        	Group group = TaskModelProvider.getFactory().newGroup();
        	((InternalOrganizationalEntity) group).setId(groupId);
        	potentialOwners.add(group);
        }
        setTaskPotentialOwners(task, potentialOwners);
        List<OrganizationalEntity> businessAdministrators = peopleAssignments.getBusinessAdministrators();
        User administrator = TaskModelProvider.getFactory().newUser();
    	((InternalOrganizationalEntity) administrator).setId("Administrator"); 
        businessAdministrators.add(administrator);
        
        TaskService taskService = runtime.getTaskService();
		long taskId = taskService.addTask(task, (Map<String, Object>) null);
		manager.disposeRuntimeEngine(runtime);
		return ChecklistItemFactory.createChecklistItem(taskService.getTaskById(taskId));
	}

	public void updateTaskName(long taskId, String name) {
		RuntimeEngine runtime = getRuntime();
		List<I18NText> names = new ArrayList<I18NText>();
		I18NText text = TaskModelProvider.getFactory().newI18NText();
        ((InternalI18NText) text).setLanguage("en-UK");
        ((InternalI18NText) text).setText(name);
		names.add(text);
		((InternalTaskService) runtime.getTaskService()).setTaskNames(taskId, names);
		manager.disposeRuntimeEngine(runtime);
	}
	
	private void setTaskName(InternalTask task, String name) {
		List<I18NText> names = new ArrayList<I18NText>();
		I18NText text = TaskModelProvider.getFactory().newI18NText();
        ((InternalI18NText) text).setLanguage("en-UK");
        ((InternalI18NText) text).setText(name);
        names.add(text);
        task.setNames(names);
		List<I18NText> subjects = new ArrayList<I18NText>();
		text = TaskModelProvider.getFactory().newI18NText();
        ((InternalI18NText) text).setLanguage("en-UK");
        ((InternalI18NText) text).setText(name);
		subjects.add(text);
        task.setSubjects(subjects);
	}

	public void updateTaskDescription(long taskId, String description) {
		RuntimeEngine runtime = getRuntime();
		List<I18NText> descriptions = new ArrayList<I18NText>();
		I18NText text = TaskModelProvider.getFactory().newI18NText();
        ((InternalI18NText) text).setLanguage("en-UK");
        ((InternalI18NText) text).setText(description);
		descriptions.add(text);
		((InternalTaskService) runtime.getTaskService()).setDescriptions(taskId, descriptions);
		manager.disposeRuntimeEngine(runtime);
	}
	
	private void setTaskDescription(InternalTask task, String description) {
		List<I18NText> descriptions = new ArrayList<I18NText>();
		I18NText text = TaskModelProvider.getFactory().newI18NText();
        ((InternalI18NText) text).setLanguage("en-UK");
        ((InternalI18NText) text).setText(description);
        descriptions.add(text);
        task.setDescriptions(descriptions);
	}

	public void updateTaskPriority(long taskId, int priority) {
		RuntimeEngine runtime = getRuntime();
		((InternalTaskService) runtime.getTaskService()).setPriority(taskId, priority);
		manager.disposeRuntimeEngine(runtime);
	}

	public void updateTaskPotentialOwners(long taskId, List<OrganizationalEntity> potentialOwners) {
//		RuntimeEngine runtime = getRuntime();
//		runtime.getTaskService().set(taskId, potentialOwners);
//		manager.disposeRuntimeEngine(runtime);
	}
	
	private void setTaskPotentialOwners(Task task, List<OrganizationalEntity> potentialOwners) {
        ((InternalPeopleAssignments) task.getPeopleAssignments()).setPotentialOwners(potentialOwners);
	}

	public void claimTask(String userId, long taskId) {
		RuntimeEngine runtime = getRuntime();
		runtime.getTaskService().claim(taskId, userId);
		manager.disposeRuntimeEngine(runtime);
	}

	public void releaseTask(String userId, long taskId) {
		RuntimeEngine runtime = getRuntime();
		runtime.getTaskService().release(taskId, userId);
		manager.disposeRuntimeEngine(runtime);
	}

	public void completeTask(String userId, long taskId) {
		RuntimeEngine runtime = getRuntime();
		runtime.getTaskService().start(taskId, userId);
		runtime.getTaskService().complete(taskId, userId, null);
		manager.disposeRuntimeEngine(runtime);
	}
	
	public void abortTask(String userId, long taskId) {
		RuntimeEngine runtime = getRuntime();
		runtime.getTaskService().start(taskId, userId);
		runtime.getTaskService().fail(taskId, userId, null);
		manager.disposeRuntimeEngine(runtime);
	}

	public void selectOptionalTask(String taskName, long processInstanceId) {
		RuntimeEngine runtime = getRuntime();
		runtime.getKieSession().signalEvent(taskName, null, processInstanceId);
		manager.disposeRuntimeEngine(runtime);
	}

	protected RuntimeEngine getRuntime() {
		if (manager == null) {
			if (environment == null) {
				environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().get();
			}
			manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
		}
        return manager.getRuntimeEngine(EmptyContext.get());
	}
	
}
