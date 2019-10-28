package org.jbpm.process.instance;

import java.util.Collection;
import java.util.Map;

import org.drools.core.common.EndOperationListener;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.impl.EnvironmentImpl;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.jbpm.workflow.instance.impl.CodegenNodeInstanceFactoryRegistry;
import org.kie.api.KieBase;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.Calendars;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.api.time.SessionClock;
import org.kie.kogito.jobs.JobsService;
import org.kie.services.time.TimerService;

/**
 * A severely limited implementation of the WorkingMemory interface.
 * It only exists for legacy reasons.
 */
class DummyKnowledgeRuntime implements InternalKnowledgeRuntime {

    private final EnvironmentImpl environment;
    private InternalProcessRuntime processRuntime;

    DummyKnowledgeRuntime(InternalProcessRuntime processRuntime) {
        this.processRuntime = processRuntime;
        this.environment = new EnvironmentImpl();
        // register codegen-based node instances factories
        environment.set("NodeInstanceFactoryRegistry", new CodegenNodeInstanceFactoryRegistry());
    }

    @Override
    public InternalAgenda getAgenda() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setIdentifier(long id) {

    }

    @Override
    public void setEndOperationListener(EndOperationListener listener) {

    }

    @Override
    public long getLastIdleTimestamp() {
        return 0;
    }

    @Override
    public void queueWorkingMemoryAction(WorkingMemoryAction action) {

    }

    @Override
    public InternalProcessRuntime getProcessRuntime() {
        return this.processRuntime;
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public JobsService getJobsService() {
        return null;
    }

    @Override
    public void startOperation() {

    }

    @Override
    public void endOperation() {

    }

    @Override
    public void executeQueuedActions() {

    }

    @Override
    public <T extends SessionClock> T getSessionClock() {
        return null;
    }

    @Override
    public void setGlobal(String identifier, Object value) {

    }

    @Override
    public Object getGlobal(String identifier) {
        return null;
    }

    @Override
    public Globals getGlobals() {
        return null;
    }

    @Override
    public Calendars getCalendars() {
        return null;
    }

    @Override
    public KieBase getKieBase() {
        return null;
    }

    @Override
    public void registerChannel(String name, Channel channel) {

    }

    @Override
    public void unregisterChannel(String name) {

    }

    @Override
    public Map<String, Channel> getChannels() {
        return null;
    }

    @Override
    public KieSessionConfiguration getSessionConfiguration() {
        return null;
    }

    @Override
    public KieRuntimeLogger getLogger() {
        return null;
    }

    @Override
    public void addEventListener(ProcessEventListener listener) {

    }

    @Override
    public void removeEventListener(ProcessEventListener listener) {

    }

    @Override
    public Collection<ProcessEventListener> getProcessEventListeners() {
        return null;
    }

    @Override
    public void addEventListener(RuleRuntimeEventListener listener) {

    }

    @Override
    public void removeEventListener(RuleRuntimeEventListener listener) {

    }

    @Override
    public Collection<RuleRuntimeEventListener> getRuleRuntimeEventListeners() {
        return null;
    }

    @Override
    public void addEventListener(AgendaEventListener listener) {

    }

    @Override
    public void removeEventListener(AgendaEventListener listener) {

    }

    @Override
    public Collection<AgendaEventListener> getAgendaEventListeners() {
        return null;
    }

    @Override
    public ProcessInstance startProcess(String processId) {
        return null;
    }

    @Override
    public ProcessInstance startProcess(String processId, Map<String, Object> parameters) {
        return null;
    }

    @Override
    public ProcessInstance createProcessInstance(String processId, Map<String, Object> parameters) {
        return null;
    }

    @Override
    public ProcessInstance startProcessInstance(String processInstanceId) {
        return null;
    }
    
    @Override
    public ProcessInstance startProcessInstance(String processInstanceId, String trigger) {
        return null;
    }

    @Override
    public void signalEvent(String type, Object event) {

    }

    @Override
    public void signalEvent(String type, Object event, String processInstanceId) {

    }

    @Override
    public Collection<ProcessInstance> getProcessInstances() {
        return null;
    }

    @Override
    public ProcessInstance getProcessInstance(String processInstanceId) {
        return null;
    }

    @Override
    public ProcessInstance getProcessInstance(String processInstanceId, boolean readonly) {
        return null;
    }

    @Override
    public void abortProcessInstance(String processInstanceId) {

    }

    @Override
    public WorkItemManager getWorkItemManager() {
        return this.processRuntime.getWorkItemManager();
    }

    @Override
    public void halt() {

    }

    @Override
    public EntryPoint getEntryPoint(String name) {
        return null;
    }

    @Override
    public Collection<? extends EntryPoint> getEntryPoints() {
        return null;
    }

    @Override
    public QueryResults getQueryResults(String query, Object... arguments) {
        return null;
    }

    @Override
    public LiveQuery openLiveQuery(String query, Object[] arguments, ViewChangedEventListener listener) {
        return null;
    }

    @Override
    public String getEntryPointId() {
        return null;
    }

    @Override
    public FactHandle insert(Object object) {
        return null;
    }

    @Override
    public void retract(FactHandle handle) {

    }

    @Override
    public void delete(FactHandle handle) {

    }

    @Override
    public void delete(FactHandle handle, FactHandle.State fhState) {

    }

    @Override
    public void update(FactHandle handle, Object object) {

    }

    @Override
    public void update(FactHandle handle, Object object, String... modifiedProperties) {

    }

    @Override
    public FactHandle getFactHandle(Object object) {
        return null;
    }

    @Override
    public Object getObject(FactHandle factHandle) {
        return null;
    }

    @Override
    public Collection<? extends Object> getObjects() {
        return null;
    }

    @Override
    public Collection<? extends Object> getObjects(ObjectFilter filter) {
        return null;
    }

    @Override
    public <T extends FactHandle> Collection<T> getFactHandles() {
        return null;
    }

    @Override
    public <T extends FactHandle> Collection<T> getFactHandles(ObjectFilter filter) {
        return null;
    }

    @Override
    public long getFactCount() {
        return 0;
    }

    @Override
    public TimerService getTimerService() {
        return null;
    }
}