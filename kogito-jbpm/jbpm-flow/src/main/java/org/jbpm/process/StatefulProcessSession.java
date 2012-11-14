package org.jbpm.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.kie.KnowledgeBase;
import org.drools.RuntimeDroolsException;
import org.drools.SessionConfiguration;
import org.drools.base.MapGlobalResolver;
import org.kie.command.Command;
import org.drools.common.EndOperationListener;
import org.drools.common.InternalKnowledgeRuntime;
import org.drools.common.WorkingMemoryAction;
import org.kie.event.process.ProcessEventListener;
import org.kie.event.rule.AgendaEventListener;
import org.kie.event.rule.WorkingMemoryEventListener;
import org.kie.runtime.Calendars;
import org.kie.runtime.Channel;
import org.kie.runtime.Environment;
import org.kie.runtime.ExitPoint;
import org.kie.runtime.Globals;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.ObjectFilter;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;
import org.kie.runtime.rule.Agenda;
import org.kie.runtime.rule.AgendaFilter;
import org.kie.runtime.rule.FactHandle;
import org.kie.runtime.rule.LiveQuery;
import org.kie.runtime.rule.QueryResults;
import org.kie.runtime.rule.ViewChangedEventListener;
import org.kie.runtime.rule.WorkingMemoryEntryPoint;
import org.kie.time.SessionClock;
import org.drools.time.TimerService;
import org.drools.time.TimerServiceFactory;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessRuntimeImpl;

public class StatefulProcessSession implements StatefulKnowledgeSession, InternalKnowledgeRuntime {

	private KnowledgeBase kbase;
	private InternalProcessRuntime processRuntime;
	private WorkItemManager workItemManager;
	private KnowledgeSessionConfiguration sessionConfiguration;
	private Environment environment;
	private TimerService timerService;
	protected Queue<WorkingMemoryAction> actionQueue;
	private int id;
	private MapGlobalResolver globals = new MapGlobalResolver();
	
	public StatefulProcessSession(KnowledgeBase kbase, KnowledgeSessionConfiguration sessionConfiguration, Environment environment) {
		this.kbase = kbase;
		this.sessionConfiguration = sessionConfiguration;
		this.environment = environment;
		timerService = TimerServiceFactory.getTimerService((SessionConfiguration) sessionConfiguration);
		processRuntime = new ProcessRuntimeImpl(this);
		actionQueue = new LinkedList<WorkingMemoryAction>();
	}
	
	public void abortProcessInstance(long processInstanceId) {
		processRuntime.abortProcessInstance(processInstanceId);
	}

	public ProcessInstance getProcessInstance(long processInstanceId) {
		return processRuntime.getProcessInstance(processInstanceId);
	}

	public Collection<ProcessInstance> getProcessInstances() {
		return processRuntime.getProcessInstances();
	}

	public void signalEvent(String type, Object event) {
		processRuntime.signalEvent(type, event);
	}

	public void signalEvent(String type, Object event, long processInstanceId) {
		processRuntime.signalEvent(type, event, processInstanceId);
	}

	public ProcessInstance startProcess(String processId) {
		return processRuntime.startProcess(processId);
	}

	public ProcessInstance startProcess(String processId, Map<String, Object> parameters) {
		return processRuntime.startProcess(processId, parameters);
	}

	public ProcessInstance createProcessInstance(String processId, Map<String, Object> parameters) {
		return processRuntime.createProcessInstance(processId, parameters);
	}

	public ProcessInstance startProcessInstance(long processInstanceId) {
		return processRuntime.startProcessInstance(processInstanceId);
	}

	public void addEventListener(ProcessEventListener listener) {
		processRuntime.addEventListener(listener);
	}

	public Collection<ProcessEventListener> getProcessEventListeners() {
		return processRuntime.getProcessEventListeners();
	}

	public void removeEventListener(ProcessEventListener listener) {
		processRuntime.removeEventListener(listener);
	}

	public KnowledgeBase getKnowledgeBase() {
		return kbase;
	}

	public WorkItemManager getWorkItemManager() {
        if ( workItemManager == null ) {
            workItemManager = ((SessionConfiguration) sessionConfiguration).getWorkItemManagerFactory().createWorkItemManager(this);
            Map<String, WorkItemHandler> workItemHandlers = ((SessionConfiguration) sessionConfiguration).getWorkItemHandlers();
            if (workItemHandlers != null) {
                for (Map.Entry<String, WorkItemHandler> entry: workItemHandlers.entrySet()) {
                    workItemManager.registerWorkItemHandler(entry.getKey(), entry.getValue());
                }
            }
        }
        return workItemManager;
	}

	public Environment getEnvironment() {
		return environment;
	}
	
	public InternalProcessRuntime getProcessRuntime() {
		return processRuntime;
	}
	
	public KnowledgeSessionConfiguration getSessionConfiguration() {
		return sessionConfiguration;
	}

	public TimerService getTimerService() {
		return timerService;
	}

	public void startOperation() {
	}

	public void endOperation() {
	}

	public void executeQueuedActions() {
        try {
            startOperation();
            if (!this.actionQueue.isEmpty()) {
                WorkingMemoryAction action = null;
                while ((action = actionQueue.poll()) != null) {
                    try {
                        action.execute(this);
                    } catch (Exception e) {
                        throw new RuntimeDroolsException( "Unexpected exception executing action " + action.toString(), e );
                    }
                }
            }
        } finally {
            endOperation();
        }
	}

	public Queue<WorkingMemoryAction> getActionQueue() {
		return actionQueue;
	}

	public void queueWorkingMemoryAction(WorkingMemoryAction action) {
		actionQueue.add(action);
	}
	
	public void dispose() {
		if (timerService != null) {
			timerService.shutdown();
		}
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public void setEndOperationListener(EndOperationListener listener) {
		
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

	public int fireAllRules(AgendaFilter agendaFilter, int i) {
		throw new UnsupportedOperationException();
	}

	public void fireUntilHalt() {
		throw new UnsupportedOperationException();
	}

	public void fireUntilHalt(AgendaFilter agendaFilter) {
		throw new UnsupportedOperationException();
	}

	public <T> T execute(Command<T> command) {
		throw new UnsupportedOperationException();
	}

	public Calendars getCalendars() {
		throw new UnsupportedOperationException();
	}

	public Map<String, Channel> getChannels() {
		throw new UnsupportedOperationException();
	}

	public Object getGlobal(String identifier) {
		return globals.get(identifier);
	}

	public Globals getGlobals() {
		return globals;
	}

	public SessionClock getSessionClock() {
        return (SessionClock) this.timerService;
	}

	public void registerChannel(String name, Channel channel) {
		throw new UnsupportedOperationException();
	}

	public void registerExitPoint(String name, ExitPoint exitPoint) {
		throw new UnsupportedOperationException();
	}

	public void setGlobal(String identifier, Object object) {
		throw new UnsupportedOperationException();
	}

	public void unregisterChannel(String name) {
		throw new UnsupportedOperationException();
	}

	public void unregisterExitPoint(String name) {
		throw new UnsupportedOperationException();
	}

	public Agenda getAgenda() {
		throw new UnsupportedOperationException();
	}

	public QueryResults getQueryResults(String query, Object... arguments) {
		throw new UnsupportedOperationException();
	}

	public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name) {
		throw new UnsupportedOperationException();
	}

	public Collection<? extends WorkingMemoryEntryPoint> getWorkingMemoryEntryPoints() {
		throw new UnsupportedOperationException();
	}

	public void halt() {
		throw new UnsupportedOperationException();
	}

	public LiveQuery openLiveQuery(String query, Object[] arguments, ViewChangedEventListener listener) {
		throw new UnsupportedOperationException();
	}

	public String getEntryPointId() {
		throw new UnsupportedOperationException();
	}

	public long getFactCount() {
		throw new UnsupportedOperationException();
	}

	public FactHandle getFactHandle(Object object) {
		throw new UnsupportedOperationException();
	}

	public <T extends FactHandle> Collection<T> getFactHandles() {
		throw new UnsupportedOperationException();
	}

	public <T extends FactHandle> Collection<T> getFactHandles(ObjectFilter filter) {
		throw new UnsupportedOperationException();
	}

	public Object getObject(FactHandle factHandle) {
		throw new UnsupportedOperationException();
	}

	public Collection<Object> getObjects() {
		throw new UnsupportedOperationException();
	}

	public Collection<Object> getObjects(ObjectFilter filter) {
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
		// Do nothing
	}

	public void addEventListener(AgendaEventListener listener) {
		// Do nothing
	}

	public Collection<AgendaEventListener> getAgendaEventListeners() {
		return new ArrayList<AgendaEventListener>();
	}

	public Collection<WorkingMemoryEventListener> getWorkingMemoryEventListeners() {
		return new ArrayList<WorkingMemoryEventListener>();
	}

	public void removeEventListener(WorkingMemoryEventListener listener) {
		// Do nothing
	}

	public void removeEventListener(AgendaEventListener listener) {
		// Do nothing
	}

	public long getLastIdleTimestamp() {
		throw new UnsupportedOperationException();
	}

}
