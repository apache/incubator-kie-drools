package org.drools.process.command.impl;

import java.util.Collection;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.process.command.AbortWorkItemCommand;
import org.drools.process.command.CommandService;
import org.drools.process.command.CompleteWorkItemCommand;
import org.drools.process.command.GetProcessInstanceCommand;
import org.drools.process.command.SignalEventCommand;
import org.drools.process.command.StartProcessCommand;
import org.drools.runtime.ExitPoint;
import org.drools.runtime.GlobalResolver;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.rule.Agenda;
import org.drools.runtime.rule.AgendaFilter;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.time.SessionClock;

public class CommandBasedStatefulKnowledgeSession implements StatefulKnowledgeSession {

	private CommandService commandService;
	private transient WorkItemManager workItemManager;
	
	public CommandBasedStatefulKnowledgeSession(CommandService commandService) {
		this.commandService = commandService;
	}
	
	public ProcessInstance getProcessInstance(long id) {
		GetProcessInstanceCommand command = new GetProcessInstanceCommand();
		command.setProcessInstanceId(id);
		return commandService.execute(command);
	}

	public Collection<ProcessInstance> getProcessInstances() {
		throw new UnsupportedOperationException();
	}

	public WorkItemManager getWorkItemManager() {
		if (workItemManager == null) {
			workItemManager = new WorkItemManager() {
				public void completeWorkItem(long id, Map<String, Object> results) {
					CompleteWorkItemCommand command = new CompleteWorkItemCommand();
					command.setWorkItemId(id);
					command.setResults(results);
					commandService.execute(command);
				}
				public void abortWorkItem(long id) {
					AbortWorkItemCommand command = new AbortWorkItemCommand();
					command.setWorkItemId(id);
					commandService.execute(command);
				}
				public void registerWorkItemHandler(String workItemName, WorkItemHandler handler) {
					throw new UnsupportedOperationException();
				}
			};
		}
		return workItemManager;
	}

	public void signalEvent(String type, Object event) {
		SignalEventCommand command = new SignalEventCommand();
		command.setEventType(type);
		command.setEvent(event);
		commandService.execute(command);
	}

	public ProcessInstance startProcess(String processId) {
		return startProcess(processId, null);
	}

	public ProcessInstance startProcess(String processId,
			Map<String, Object> parameters) {
		StartProcessCommand command = new StartProcessCommand();
		command.setProcessId(processId);
		command.setParameters(parameters);
		return commandService.execute(command);
	}

	public void dispose() {
		commandService.dispose();
	}

	public int fireAllRules() {
		throw new UnsupportedOperationException();
	}

	public int fireAllRules(int max) {
		throw new UnsupportedOperationException();
	}

	public int fireAllRules(AgendaFilter agendaFilter) {
		throw new UnsupportedOperationException();
	}

	public void fireUntilHalt() {
		throw new UnsupportedOperationException();
	}

	public void fireUntilHalt(AgendaFilter agendaFilter) {
		throw new UnsupportedOperationException();
	}

	public KnowledgeBase getKnowledgeBase() {
		throw new UnsupportedOperationException();
	}

	public void registerExitPoint(String name, ExitPoint exitPoint) {
		throw new UnsupportedOperationException();
	}

	public void setGlobal(String identifier, Object object) {
		throw new UnsupportedOperationException();
	}

	public void setGlobalResolver(GlobalResolver globalResolver) {
		throw new UnsupportedOperationException();
	}

	public void unregisterExitPoint(String name) {
		throw new UnsupportedOperationException();
	}

	public Agenda getAgenda() {
		throw new UnsupportedOperationException();
	}

	public FactHandle getFactHandle(Object object) {
		throw new UnsupportedOperationException();
	}

	public Collection<? extends FactHandle> getFactHandles() {
		throw new UnsupportedOperationException();
	}

	public Collection<? extends FactHandle> getFactHandles(ObjectFilter filter) {
		throw new UnsupportedOperationException();
	}

	public Collection<?> getObjects() {
		throw new UnsupportedOperationException();
	}

	public Collection<?> getObjects(ObjectFilter filter) {
		throw new UnsupportedOperationException();
	}

	public SessionClock getSessionClock() {
		throw new UnsupportedOperationException();
	}

	public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name) {
		throw new UnsupportedOperationException();
	}

	public void halt() {
		throw new UnsupportedOperationException();
	}

	public FactHandle insert(Object object) {
		throw new UnsupportedOperationException();
	}

	public void retract(FactHandle handle) {
		throw new UnsupportedOperationException();
	}

	public void update(FactHandle handle, Object object) {
		throw new UnsupportedOperationException();
	}

	public void addEventListener(WorkingMemoryEventListener listener) {
		throw new UnsupportedOperationException();
	}

	public void addEventListener(AgendaEventListener listener) {
		throw new UnsupportedOperationException();
	}

	public Collection<AgendaEventListener> getAgendaEventListeners() {
		throw new UnsupportedOperationException();
	}

	public Collection<WorkingMemoryEventListener> getWorkingMemoryEventListeners() {
		throw new UnsupportedOperationException();
	}

	public void removeEventListener(WorkingMemoryEventListener listener) {
		throw new UnsupportedOperationException();
	}

	public void removeEventListener(AgendaEventListener listener) {
		throw new UnsupportedOperationException();
	}

	public void addEventListener(ProcessEventListener listener) {
		throw new UnsupportedOperationException();
	}

	public Collection<ProcessEventListener> getProcessEventListeners() {
		throw new UnsupportedOperationException();
	}

	public void removeEventListener(ProcessEventListener listener) {
		throw new UnsupportedOperationException();
	}

	public Object getGlobal(String identifier) {
		throw new UnsupportedOperationException();
	}

	public Object getObject(FactHandle factHandle) {
		throw new UnsupportedOperationException();
	}

}