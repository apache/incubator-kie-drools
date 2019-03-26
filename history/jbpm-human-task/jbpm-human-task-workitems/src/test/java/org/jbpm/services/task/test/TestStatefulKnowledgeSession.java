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

package org.jbpm.services.task.test;

import org.drools.core.impl.AbstractRuntime;
import org.jbpm.process.core.impl.ProcessImpl;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.Calendars;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.Globals;
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
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TestStatefulKnowledgeSession extends AbstractRuntime implements StatefulKnowledgeSession {

    public static Long testSessionId = 5L;
    private Environment env;

    public Calendars getCalendars() {
        return null;
    }

    public Map<String, Channel> getChannels() {
        return null;
    }

    public void setEnvironment(Environment env) {
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

    public KieBase getKieBase() {
        return null;
    }

    public <T extends SessionClock> T getSessionClock() {
        return null;
    }

    public KieSessionConfiguration getSessionConfiguration() {
        return null;
    }

    public void registerChannel(String arg0, Channel arg1) {
    }

    public void setGlobal(String arg0, Object arg1) {
    }

    public void unregisterChannel(String arg0) {
    }

    public void addEventListener(AgendaEventListener arg0) {
    }

    public void addEventListener(RuleRuntimeEventListener arg0) {
    }

    public Collection<AgendaEventListener> getAgendaEventListeners() {
        return null;
    }

    public Collection<RuleRuntimeEventListener> getRuleRuntimeEventListeners() {
        return null;
    }

    public void removeEventListener(AgendaEventListener arg0) {
    }

    public void removeEventListener(RuleRuntimeEventListener arg0) {
    }

    public void abortProcessInstance(long arg0) {
    }

    public ProcessInstance createProcessInstance(String arg0,
            Map<String, Object> arg1) {
        return null;
    }

    public ProcessInstance getProcessInstance(long arg0) {
        RuleFlowProcessInstance pi = new RuleFlowProcessInstance();
        pi.setId(arg0);
        ProcessImpl processImpl = new ProcessImpl();
        processImpl.setId(""+arg0);
        pi.setProcess(processImpl);
        return pi;
    }

    public ProcessInstance getProcessInstance(long arg0, boolean readOnly) {
        RuleFlowProcessInstance pi = new RuleFlowProcessInstance();
        pi.setId(arg0);
        ProcessImpl processImpl = new ProcessImpl();
        processImpl.setId(""+arg0);
        pi.setProcess(processImpl);
        return pi;
    }

    public Collection<ProcessInstance> getProcessInstances() {
      List<ProcessInstance> pis = new ArrayList<ProcessInstance>();
      pis.add(new RuleFlowProcessInstance());
      return pis;
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

    public EntryPoint getEntryPoint(String arg0) {
        return null;
    }

    public Collection<? extends EntryPoint> getEntryPoints() {
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

    public void update(FactHandle arg0, Object arg1, String... arg2) {
    }

    public void submit(AtomicAction action) {
    }

    @Override
    public <T> T getKieRuntime(Class<T> cls) {
        return null;
    }

    public void dispose() {
    }

    public int getId() {
        return testSessionId.intValue();
    }
    
    public long getIdentifier() {
    	return testSessionId;
    }

    @Override
    public void delete(FactHandle fh) {
    }

    @Override
    public void delete(FactHandle fh, FactHandle.State fhState) {
    }

    @Override
    public void destroy() {
        dispose();        
    }
}
