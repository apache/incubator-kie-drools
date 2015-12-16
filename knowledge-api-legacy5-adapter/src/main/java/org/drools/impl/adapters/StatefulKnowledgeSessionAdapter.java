/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.impl.adapters;

import org.drools.KnowledgeBase;
import org.drools.command.Command;
import org.drools.definition.rule.Rule;
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
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.rule.Agenda;
import org.drools.runtime.rule.AgendaFilter;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.LiveQuery;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.ViewChangedEventListener;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.time.SessionClock;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.drools.impl.adapters.FactHandleAdapter.adaptFactHandles;
import static org.drools.impl.adapters.ProcessAdapter.adaptProcesses;
import static org.drools.impl.adapters.ProcessInstanceAdapter.adaptProcessInstances;

public class StatefulKnowledgeSessionAdapter extends KnowledgeRuntimeAdapter implements org.drools.runtime.StatefulKnowledgeSession {

    public StatefulKnowledgeSessionAdapter(StatefulKnowledgeSession delegate) {
        super(delegate);
    }

    public int getId() {
        return ((StatefulKnowledgeSession)delegate).getId();
    }

    public void dispose() {
        ((StatefulKnowledgeSession)delegate).dispose();
    }

    public <T> T execute(Command<T> command) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    public int fireAllRules() {
        return ((StatefulKnowledgeSession)delegate).fireAllRules();
    }

    public int fireAllRules(int max) {
        return ((StatefulKnowledgeSession)delegate).fireAllRules(max);
    }

    public int fireAllRules(AgendaFilter agendaFilter) {
        return ((StatefulKnowledgeSession)delegate).fireAllRules(new AgendaFilterAdapter(agendaFilter));
    }

    public int fireAllRules(AgendaFilter agendaFilter, int max) {
        return ((StatefulKnowledgeSession)delegate).fireAllRules(new AgendaFilterAdapter(agendaFilter), max);
    }

    public void fireUntilHalt() {
        ((StatefulKnowledgeSession)delegate).fireUntilHalt();
    }

    public void fireUntilHalt(AgendaFilter agendaFilter) {
        ((StatefulKnowledgeSession)delegate).fireUntilHalt(new AgendaFilterAdapter(agendaFilter));
    }

    public static List<org.drools.runtime.StatefulKnowledgeSession> adaptStatefulKnowledgeSession(Collection<org.kie.internal.runtime.StatefulKnowledgeSession> sessions) {
        List<org.drools.runtime.StatefulKnowledgeSession> result = new ArrayList<org.drools.runtime.StatefulKnowledgeSession>();
        for (org.kie.internal.runtime.StatefulKnowledgeSession session : sessions) {
            result.add(new StatefulKnowledgeSessionAdapter(session));
        }
        return result;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StatefulKnowledgeSessionAdapter && delegate.equals(((StatefulKnowledgeSessionAdapter)obj).delegate);
    }
}
