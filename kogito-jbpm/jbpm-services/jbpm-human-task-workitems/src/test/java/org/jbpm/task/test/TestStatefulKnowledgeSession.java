package org.jbpm.task.test;

import java.util.Collection;
import java.util.Map;

import org.kie.KnowledgeBase;
import org.kie.command.Command;
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
import org.kie.runtime.process.WorkItemManager;
import org.kie.runtime.rule.Agenda;
import org.kie.runtime.rule.AgendaFilter;
import org.kie.runtime.rule.FactHandle;
import org.kie.runtime.rule.LiveQuery;
import org.kie.runtime.rule.QueryResults;
import org.kie.runtime.rule.ViewChangedEventListener;
import org.kie.runtime.rule.WorkingMemoryEntryPoint;
import org.kie.time.SessionClock;

public class TestStatefulKnowledgeSession implements StatefulKnowledgeSession {
	public static int testSessionId = 5;
	private Environment env;
	public Calendars getCalendars() {
		return null;
	}

	public Map<String, Channel> getChannels() {
		return null;
	}

        public void setEnvironment(Environment env){
            this.env = env;
        }
	public Environment getEnvironment() {
		return this.env;
	}

	public Object getGlobal(String arg0) {
		return null;
	}

	public Globals getGlobals() {
		return null;
	}

	public KnowledgeBase getKnowledgeBase() {
		return null;
	}

	public <T extends SessionClock> T getSessionClock() {
		return null;
	}

	public KnowledgeSessionConfiguration getSessionConfiguration() {
		return null;
	}

	public void registerChannel(String arg0, Channel arg1) {
	}

	public void registerExitPoint(String arg0, ExitPoint arg1) {
	}

	public void setGlobal(String arg0, Object arg1) {
	}

	public void unregisterChannel(String arg0) {
	}

	public void unregisterExitPoint(String arg0) {
	}

	public void addEventListener(AgendaEventListener arg0) {
	}

	public void addEventListener(WorkingMemoryEventListener arg0) {
	}

	public Collection<AgendaEventListener> getAgendaEventListeners() {
		return null;
	}

	public Collection<WorkingMemoryEventListener> getWorkingMemoryEventListeners() {
		return null;
	}

	public void removeEventListener(AgendaEventListener arg0) {
	}

	public void removeEventListener(WorkingMemoryEventListener arg0) {
	}

	public void abortProcessInstance(long arg0) {
	}

	public ProcessInstance createProcessInstance(String arg0,
			Map<String, Object> arg1) {
		return null;
	}

	public ProcessInstance getProcessInstance(long arg0) {
		return null;
	}

	public Collection<ProcessInstance> getProcessInstances() {
		return null;
	}
	
	private WorkItemManager workItemManager;
	
	public void setWorkItemManager(WorkItemManager workItemManager) {
		this.workItemManager = workItemManager;
	}

	public WorkItemManager getWorkItemManager() {
		return workItemManager;
	}

	public void signalEvent(String arg0, Object arg1, long arg2) {
	}

	public void signalEvent(String arg0, Object arg1) {
	}

	public ProcessInstance startProcess(String arg0, Map<String, Object> arg1) {
		return null;
	}

	public ProcessInstance startProcess(String arg0) {
		return null;
	}

	public ProcessInstance startProcessInstance(long arg0) {
		return null;
	}

	public Agenda getAgenda() {
		return null;
	}

	public QueryResults getQueryResults(String arg0, Object... arg1) {
		return null;
	}

	public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String arg0) {
		return null;
	}

	public Collection<? extends WorkingMemoryEntryPoint> getWorkingMemoryEntryPoints() {
		return null;
	}

	public void halt() {
	}

	public LiveQuery openLiveQuery(String arg0, Object[] arg1,
			ViewChangedEventListener arg2) {
		return null;
	}

	public int fireAllRules() {
		return 0;
	}

	public int fireAllRules(AgendaFilter arg0, int arg1) {
		return 0;
	}

	public int fireAllRules(AgendaFilter arg0) {
		return 0;
	}

	public int fireAllRules(int arg0) {
		return 0;
	}

	public void fireUntilHalt() {
	}

	public void fireUntilHalt(AgendaFilter arg0) {
	}

	public <T> T execute(Command<T> arg0) {
		return null;
	}

	public void addEventListener(ProcessEventListener arg0) {
	}

	public Collection<ProcessEventListener> getProcessEventListeners() {
		return null;
	}

	public void removeEventListener(ProcessEventListener arg0) {
	}

	public String getEntryPointId() {
		return null;
	}

	public long getFactCount() {
		return 0;
	}

	public FactHandle getFactHandle(Object arg0) {
		return null;
	}

	public <T extends FactHandle> Collection<T> getFactHandles() {
		return null;
	}

	public <T extends FactHandle> Collection<T> getFactHandles(ObjectFilter arg0) {
		return null;
	}

	public Object getObject(FactHandle arg0) {
		return null;
	}

	public Collection<Object> getObjects() {
		return null;
	}

	public Collection<Object> getObjects(ObjectFilter arg0) {
		return null;
	}

	public FactHandle insert(Object arg0) {
		return null;
	}

	public void retract(FactHandle arg0) {
	}

	public void update(FactHandle arg0, Object arg1) {
	}

	public void dispose() {
	}

	public int getId() {
		return testSessionId; 
	}
}
