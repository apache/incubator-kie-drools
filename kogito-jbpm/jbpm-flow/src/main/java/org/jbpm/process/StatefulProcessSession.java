package org.jbpm.process;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.drools.KnowledgeBase;
import org.drools.RuntimeDroolsException;
import org.drools.SessionConfiguration;
import org.drools.base.MapGlobalResolver;
import org.drools.command.Command;
import org.drools.common.EndOperationListener;
import org.drools.common.InternalKnowledgeRuntime;
import org.drools.common.WorkingMemoryAction;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.runtime.Calendars;
import org.drools.runtime.Channel;
import org.drools.runtime.Environment;
import org.drools.runtime.ExitPoint;
import org.drools.runtime.Globals;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.rule.Agenda;
import org.drools.runtime.rule.AgendaFilter;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.LiveQuery;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.ViewChangedEventListener;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.time.SessionClock;
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

	public <T extends SessionClock> T getSessionClock() {
		throw new UnsupportedOperationException();
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

	public long getLastIdleTimestamp() {
		throw new UnsupportedOperationException();
	}

}
