/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.instance;

import java.util.Collection;
import java.util.Map;

import org.drools.core.common.EndOperationListener;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.impl.EnvironmentImpl;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.drools.core.time.TimerService;
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
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.api.time.SessionClock;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.internal.process.event.KogitoProcessEventSupport;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;

/**
 * A severely limited implementation of the WorkingMemory interface.
 * It only exists for legacy reasons.
 */
class DummyKnowledgeRuntime implements InternalKnowledgeRuntime, KogitoProcessRuntime {

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
        return null;
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
    public KogitoProcessEventSupport getProcessEventSupport() {
        return ((org.jbpm.process.instance.InternalProcessRuntime) processRuntime).getProcessEventSupport();
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    public JobsService getJobsService() {
        return null;
    }

    @Override
    public KieRuntime getKieRuntime() {
        throw new UnsupportedOperationException();
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
    public KogitoProcessInstance startProcess( String processId) {
        return null;
    }

    @Override
    public KogitoProcessInstance startProcess(String processId, Map<String, Object> parameters) {
        return null;
    }

    @Override
    public KogitoProcessInstance startProcess( String processId, AgendaFilter agendaFilter ) {
        return null;
    }

    @Override
    public KogitoProcessInstance startProcess(String processId, Map<String, Object> parameters, AgendaFilter agendaFilter) {
        return null;
    }

    @Override
    public ProcessInstance startProcessFromNodeIds( String s, Map<String, Object> map, String... strings ) {
        throw new UnsupportedOperationException( "org.jbpm.process.instance.DummyKnowledgeRuntime.startProcessFromNodeIds -> TODO" );

    }

    @Override
    public KogitoProcessInstance createProcessInstance(String processId, Map<String, Object> parameters) {
        return null;
    }

    @Override
    public ProcessInstance startProcessInstance( long l ) {
        throw new UnsupportedOperationException( "org.jbpm.process.instance.DummyKnowledgeRuntime.startProcessInstance -> TODO" );

    }

    public KogitoProcessInstance startProcessInstance(String processInstanceId) {
        return null;
    }

    public KogitoProcessInstance startProcessInstance(String processInstanceId, String trigger) {
        return null;
    }

    @Override
    public void signalEvent(String type, Object event) {

    }

    @Override
    public void signalEvent( String s, Object o, long l ) {
        throw new UnsupportedOperationException( "org.jbpm.process.instance.DummyKnowledgeRuntime.signalEvent -> TODO" );

    }

    public void signalEvent(String type, Object event, String processInstanceId) {

    }

    @Override
    public Collection<ProcessInstance> getProcessInstances() {
        return null;
    }

    @Override
    public Collection<KogitoProcessInstance> getKogitoProcessInstances() {
        return null;
    }

    @Override
    public ProcessInstance getProcessInstance( long l ) {
        throw new UnsupportedOperationException( "org.jbpm.process.instance.DummyKnowledgeRuntime.getProcessInstance -> TODO" );

    }

    @Override
    public ProcessInstance getProcessInstance( long l, boolean b ) {
        throw new UnsupportedOperationException( "org.jbpm.process.instance.DummyKnowledgeRuntime.getProcessInstance -> TODO" );

    }

    @Override
    public void abortProcessInstance( long l ) {
        throw new UnsupportedOperationException( "org.jbpm.process.instance.DummyKnowledgeRuntime.abortProcessInstance -> TODO" );

    }

    public KogitoProcessInstance getProcessInstance(String processInstanceId) {
        return null;
    }

    public KogitoProcessInstance getProcessInstance(String processInstanceId, boolean readonly) {
        return null;
    }

    public void abortProcessInstance(String processInstanceId) {

    }

    @Override
    public KogitoWorkItemManager getWorkItemManager() {
        return ( KogitoWorkItemManager ) this.processRuntime.getWorkItemManager();
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