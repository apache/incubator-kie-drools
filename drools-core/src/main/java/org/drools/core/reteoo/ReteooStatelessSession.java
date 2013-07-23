/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.reteoo;

import org.drools.core.SessionConfiguration;
import org.drools.core.StatelessSession;
import org.drools.core.StatelessSessionResult;
import org.drools.core.base.MapGlobalResolver;
import org.drools.core.common.AbstractWorkingMemory.WorkingMemoryReteAssertAction;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalRuleBase;
import org.drools.core.common.InternalStatelessSession;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.WorkingMemoryFactory;
import org.drools.core.event.AgendaEventListener;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleBaseEventListener;
import org.drools.core.event.RuleEventListenerSupport;
import org.drools.core.event.WorkingMemoryEventListener;
import org.drools.core.event.WorkingMemoryEventSupport;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.AgendaFilter;
import org.drools.core.spi.GlobalExporter;
import org.drools.core.spi.GlobalResolver;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ReteooStatelessSession
        implements
        StatelessSession,
        InternalStatelessSession,
        Externalizable {


    private WorkingMemoryEventSupport workingMemoryEventSupport = new WorkingMemoryEventSupport();
    private AgendaEventSupport        agendaEventSupport        = new AgendaEventSupport();
    private RuleEventListenerSupport  ruleEventListenerSupport  = new RuleEventListenerSupport();
    private GlobalResolver            globalResolver            = new MapGlobalResolver();
    private GlobalExporter       globalExporter;
    private InternalRuleBase     ruleBase;
    private AgendaFilter         agendaFilter;
    private SessionConfiguration sessionConf;
    private WorkingMemoryFactory wmFactory;

    public ReteooStatelessSession() {
    }

    public ReteooStatelessSession(final InternalRuleBase ruleBase) {
        this.ruleBase = ruleBase;
        this.wmFactory = ruleBase.getConfiguration().getComponentFactory().getWorkingMemoryFactory();
        this.sessionConf = SessionConfiguration.getDefaultInstance(); // create one of these and re-use
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        ruleBase = (InternalRuleBase) in.readObject();
        agendaFilter = (AgendaFilter) in.readObject();
        globalResolver = (GlobalResolver) in.readObject();
        globalExporter = (GlobalExporter) in.readObject();
        this.sessionConf = SessionConfiguration.getDefaultInstance(); // create one of these and re-use
        this.wmFactory = ruleBase.getConfiguration().getComponentFactory().getWorkingMemoryFactory();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(ruleBase);
        out.writeObject(agendaFilter);
        out.writeObject(globalResolver);
        out.writeObject(globalExporter);
    }

    public InternalRuleBase getRuleBase() {
        return this.ruleBase;
    }

    public InternalWorkingMemory newWorkingMemory() {
        this.ruleBase.readLock();
        try {
            InternalWorkingMemory wm = wmFactory.createWorkingMemory(this.ruleBase.nextWorkingMemoryCounter(),
                                                                     this.ruleBase,
                                                                     ruleBase.newFactHandleFactory(),
                                                                     null,
                                                                     0,
                                                                     this.sessionConf,
                                                                     EnvironmentFactory.newEnvironment(),
                                                                     this.workingMemoryEventSupport,
                                                                     this.agendaEventSupport,
                                                                     this.ruleEventListenerSupport,
                                                                     null);

            wm.setGlobalResolver(this.globalResolver);

            final InternalFactHandle handle = wm.getFactHandleFactory().newFactHandle(InitialFactImpl.getInstance(),
                                                                                      wm.getObjectTypeConfigurationRegistry().getObjectTypeConf(EntryPointId.DEFAULT,
                                                                                                                                                InitialFactImpl.getInstance()),
                                                                                      wm,
                                                                                      wm);

            wm.queueWorkingMemoryAction(new WorkingMemoryReteAssertAction(handle,
                                                                          false,
                                                                          true,
                                                                          null,
                                                                          null));
            return wm;
        } finally {
            this.ruleBase.readUnlock();
        }
    }

    public void addEventListener(final WorkingMemoryEventListener listener) {
        this.workingMemoryEventSupport.addEventListener(listener);
    }

    public void removeEventListener(final WorkingMemoryEventListener listener) {
        this.workingMemoryEventSupport.removeEventListener(listener);
    }

    public List getWorkingMemoryEventListeners() {
        return this.workingMemoryEventSupport.getEventListeners();
    }

    public void addEventListener(final AgendaEventListener listener) {
        this.agendaEventSupport.addEventListener(listener);
    }

    public void removeEventListener(final AgendaEventListener listener) {
        this.agendaEventSupport.removeEventListener(listener);
    }

    public List getAgendaEventListeners() {
        return this.agendaEventSupport.getEventListeners();
    }

    public void addEventListener(RuleBaseEventListener listener) {
        this.ruleBase.addEventListener(listener);
    }

    public List getRuleBaseEventListeners() {
        return this.ruleBase.getRuleBaseEventListeners();
    }

    public void removeEventListener(RuleBaseEventListener listener) {
        this.ruleBase.removeEventListener(listener);
    }

    public void setAgendaFilter(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public void setGlobal(String identifier,
                          Object value) {
        this.globalResolver.setGlobal(identifier,
                                      value);
    }

    public void setGlobalResolver(GlobalResolver globalResolver) {
        this.globalResolver = globalResolver;
    }

    public void setGlobalExporter(GlobalExporter globalExporter) {
        this.globalExporter = globalExporter;
    }

    public void execute(Object object) {
        InternalWorkingMemory wm = newWorkingMemory();

        wm.insert(object);
        wm.fireAllRules(this.agendaFilter);
    }

    public void execute(Object[] array) {
        InternalWorkingMemory wm = newWorkingMemory();

        for (int i = 0, length = array.length; i < length; i++) {
            wm.insert(array[i]);
        }
        wm.fireAllRules(this.agendaFilter);
    }

    public void execute(Collection collection) {
        InternalWorkingMemory wm = newWorkingMemory();

        for (Iterator it = collection.iterator(); it.hasNext(); ) {
            wm.insert(it.next());
        }
        wm.fireAllRules(this.agendaFilter);
    }

    public StatelessSessionResult executeWithResults(Object object) {
        InternalWorkingMemory wm = newWorkingMemory();

        wm.insert(object);
        wm.fireAllRules(this.agendaFilter);

        GlobalResolver globalResolver = null;
        if (this.globalExporter != null) {
            globalResolver = this.globalExporter.export(wm);
        }
        return new ReteStatelessSessionResult(wm,
                                              globalResolver);
    }

    public StatelessSessionResult executeWithResults(Object[] array) {
        InternalWorkingMemory wm = newWorkingMemory();

        for (int i = 0, length = array.length; i < length; i++) {
            wm.insert(array[i]);
        }
        wm.fireAllRules(this.agendaFilter);

        GlobalResolver globalResolver = null;
        if (this.globalExporter != null) {
            globalResolver = this.globalExporter.export(wm);
        }
        return new ReteStatelessSessionResult(wm,
                                              globalResolver);
    }

    public StatelessSessionResult executeWithResults(Collection collection) {
        InternalWorkingMemory wm = newWorkingMemory();

        for (Iterator it = collection.iterator(); it.hasNext(); ) {
            wm.insert(it.next());
        }
        wm.fireAllRules(this.agendaFilter);

        GlobalResolver globalResolver = null;
        if (this.globalExporter != null) {
            globalResolver = this.globalExporter.export(wm);
        }
        return new ReteStatelessSessionResult(wm,
                                              globalResolver);
    }
}
