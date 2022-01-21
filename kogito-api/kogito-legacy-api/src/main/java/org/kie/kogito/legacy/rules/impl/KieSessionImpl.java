/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.legacy.rules.impl;

import java.util.Collection;
import java.util.Map;

import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.Calendars;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.rule.Agenda;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.api.time.SessionClock;
import org.kie.internal.event.rule.RuleEventListener;
import org.kie.internal.event.rule.RuleEventManager;

public class KieSessionImpl implements KieSession, RuleEventManager {

    private final KieSession delegate;

    public KieSessionImpl(org.kie.api.runtime.KieSession delegate) {
        this.delegate = delegate;
    }

    @Override
    public long getIdentifier() {
        return delegate.getIdentifier();
    }

    @Override
    public void dispose() {
        delegate.dispose();
    }

    @Override
    public void destroy() {
        delegate.destroy();
    }

    @Override
    public void submit(AtomicAction atomicAction) {
        delegate.submit(atomicAction);
    }

    @Override
    public <T> T getKieRuntime(Class<T> aClass) {
        return delegate.getKieRuntime(aClass);
    }

    @Override
    public int fireAllRules() {
        return delegate.fireAllRules();
    }

    @Override
    public int fireAllRules(int i) {
        return delegate.fireAllRules(i);
    }

    @Override
    public int fireAllRules(AgendaFilter agendaFilter) {
        return delegate.fireAllRules(agendaFilter);
    }

    @Override
    public int fireAllRules(AgendaFilter agendaFilter, int i) {
        return delegate.fireAllRules(agendaFilter, i);
    }

    @Override
    public void fireUntilHalt() {
        delegate.fireUntilHalt();
    }

    @Override
    public void fireUntilHalt(AgendaFilter agendaFilter) {
        delegate.fireUntilHalt(agendaFilter);
    }

    @Override
    public <T> T execute(Command<T> command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends SessionClock> T getSessionClock() {
        return delegate.getSessionClock();
    }

    @Override
    public void setGlobal(String s, Object o) {
        delegate.setGlobal(s, o);
    }

    @Override
    public Object getGlobal(String s) {
        return delegate.getGlobal(s);
    }

    @Override
    public Globals getGlobals() {
        return delegate.getGlobals();
    }

    @Override
    public Calendars getCalendars() {
        return delegate.getCalendars();
    }

    @Override
    public Environment getEnvironment() {
        return delegate.getEnvironment();
    }

    @Override
    public KieBase getKieBase() {
        return delegate.getKieBase();
    }

    @Override
    public void registerChannel(String s, Channel channel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unregisterChannel(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Channel> getChannels() {
        throw new UnsupportedOperationException();
    }

    @Override
    public KieSessionConfiguration getSessionConfiguration() {
        return delegate.getSessionConfiguration();
    }

    @Override
    public void halt() {
        delegate.halt();
    }

    @Override
    public Agenda getAgenda() {
        return delegate.getAgenda();
    }

    @Override
    public EntryPoint getEntryPoint(String s) {
        return delegate.getEntryPoint(s);
    }

    @Override
    public Collection<? extends EntryPoint> getEntryPoints() {
        return delegate.getEntryPoints();
    }

    @Override
    public QueryResults getQueryResults(String s, Object... objects) {
        return delegate.getQueryResults(s, objects);
    }

    @Override
    public String getEntryPointId() {
        return delegate.getEntryPointId();
    }

    @Override
    public FactHandle insert(Object o) {
        return delegate.insert(o);
    }

    @Override
    public void retract(FactHandle factHandle) {
        delegate.retract(factHandle);
    }

    @Override
    public void delete(FactHandle factHandle) {
        delegate.delete(factHandle);
    }

    @Override
    public void delete(FactHandle factHandle, FactHandle.State state) {
        delegate.delete(factHandle, state);
    }

    @Override
    public void update(FactHandle factHandle, Object o) {
        delegate.update(factHandle, o);
    }

    @Override
    public void update(FactHandle factHandle, Object o, String... strings) {
        delegate.update(factHandle, o, strings);
    }

    @Override
    public FactHandle getFactHandle(Object o) {
        return delegate.getFactHandle(o);
    }

    @Override
    public Object getObject(FactHandle factHandle) {
        return delegate.getObject(factHandle);
    }

    @Override
    public Collection<? extends Object> getObjects() {
        return delegate.getObjects();
    }

    @Override
    public Collection<? extends Object> getObjects(ObjectFilter objectFilter) {
        return delegate.getObjects(objectFilter);
    }

    @Override
    public <T extends FactHandle> Collection<T> getFactHandles() {
        return delegate.getFactHandles();
    }

    @Override
    public <T extends FactHandle> Collection<T> getFactHandles(ObjectFilter objectFilter) {
        return delegate.getFactHandles(objectFilter);
    }

    @Override
    public long getFactCount() {
        return delegate.getFactCount();
    }

    @Override
    public WorkItemManager getWorkItemManager() {
        return delegate.getWorkItemManager();
    }

    @Override
    public void addEventListener(RuleRuntimeEventListener ruleRuntimeEventListener) {
        delegate.addEventListener(ruleRuntimeEventListener);
    }

    @Override
    public void removeEventListener(RuleRuntimeEventListener ruleRuntimeEventListener) {
        delegate.removeEventListener(ruleRuntimeEventListener);
    }

    @Override
    public Collection<RuleRuntimeEventListener> getRuleRuntimeEventListeners() {
        return delegate.getRuleRuntimeEventListeners();
    }

    @Override
    public void addEventListener(AgendaEventListener agendaEventListener) {
        delegate.addEventListener(agendaEventListener);
    }

    @Override
    public void removeEventListener(AgendaEventListener agendaEventListener) {
        delegate.removeEventListener(agendaEventListener);
    }

    @Override
    public Collection<AgendaEventListener> getAgendaEventListeners() {
        return delegate.getAgendaEventListeners();
    }

    @Override
    @Deprecated
    public int getId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addEventListener(ProcessEventListener processEventListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeEventListener(ProcessEventListener processEventListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<ProcessEventListener> getProcessEventListeners() {
        throw new UnsupportedOperationException();
    }

    @Override
    public KieRuntimeLogger getLogger() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProcessInstance startProcess(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProcessInstance startProcess(String s, Map<String, Object> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProcessInstance startProcess(String s, AgendaFilter agendaFilter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProcessInstance startProcess(String s, Map<String, Object> map, AgendaFilter agendaFilter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProcessInstance startProcessFromNodeIds(String s, Map<String, Object> map, String... strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProcessInstance createProcessInstance(String s, Map<String, Object> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProcessInstance startProcessInstance(String l) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void signalEvent(String s, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void signalEvent(String s, Object o, String l) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<ProcessInstance> getProcessInstances() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProcessInstance getProcessInstance(String l) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProcessInstance getProcessInstance(String l, boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void abortProcessInstance(String l) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LiveQuery openLiveQuery(String s, Object[] objects, ViewChangedEventListener viewChangedEventListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addEventListener(RuleEventListener ruleEventListener) {
        ((RuleEventManager) delegate).addEventListener(ruleEventListener);
    }

    @Override
    public void removeEventListener(RuleEventListener ruleEventListener) {
        ((RuleEventManager) delegate).removeEventListener(ruleEventListener);
    }
}
