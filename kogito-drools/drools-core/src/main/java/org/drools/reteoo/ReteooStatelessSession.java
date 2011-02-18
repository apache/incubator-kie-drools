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

package org.drools.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.drools.SessionConfiguration;
import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.base.MapGlobalResolver;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalStatelessSession;
import org.drools.common.InternalWorkingMemory;
import org.drools.concurrent.AssertObject;
import org.drools.concurrent.AssertObjects;
import org.drools.concurrent.CommandExecutor;
import org.drools.concurrent.ExecutorService;
import org.drools.concurrent.FireAllRules;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaEventSupport;
import org.drools.event.RuleBaseEventListener;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.impl.EnvironmentFactory;
import org.drools.reteoo.ReteooWorkingMemory.WorkingMemoryReteAssertAction;
import org.drools.rule.EntryPoint;
import org.drools.spi.AgendaFilter;
import org.drools.spi.ExecutorServiceFactory;
import org.drools.spi.GlobalExporter;
import org.drools.spi.GlobalResolver;

public class ReteooStatelessSession
    implements
    StatelessSession,
    InternalStatelessSession,
    Externalizable {
    //private WorkingMemory workingMemory;

    private InternalRuleBase            ruleBase;
    private AgendaFilter                agendaFilter;
    private GlobalResolver              globalResolver            = new MapGlobalResolver();

    private GlobalExporter              globalExporter;
    
    private SessionConfiguration        sessionConf; 

    /** The eventSupport */
    protected WorkingMemoryEventSupport workingMemoryEventSupport = new WorkingMemoryEventSupport();

    protected AgendaEventSupport        agendaEventSupport        = new AgendaEventSupport();

    public ReteooStatelessSession() {
    }

    public ReteooStatelessSession(final InternalRuleBase ruleBase) {
        this.ruleBase = ruleBase;
        this.sessionConf = SessionConfiguration.getDefaultInstance(); // create one of these and re-use
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        ruleBase = (InternalRuleBase) in.readObject();
        agendaFilter = (AgendaFilter) in.readObject();
        globalResolver = (GlobalResolver) in.readObject();
        globalExporter = (GlobalExporter) in.readObject();
        this.sessionConf = SessionConfiguration.getDefaultInstance(); // create one of these and re-use
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( ruleBase );
        out.writeObject( agendaFilter );
        out.writeObject( globalResolver );
        out.writeObject( globalExporter );
    }
    
    public InternalRuleBase getRuleBase() {
        return this.ruleBase;
    }

    public InternalWorkingMemory newWorkingMemory() {
        this.ruleBase.readLock();
        try {
            InternalWorkingMemory wm = new ReteooWorkingMemory( this.ruleBase.nextWorkingMemoryCounter(),
                                                                this.ruleBase,
                                                                this.sessionConf,
                                                                EnvironmentFactory.newEnvironment(),
                                                                this.workingMemoryEventSupport,
                                                                this.agendaEventSupport);

            wm.setGlobalResolver( this.globalResolver );

            final InternalFactHandle handle =  wm.getFactHandleFactory().newFactHandle( InitialFactImpl.getInstance(),
                                                                                       wm.getObjectTypeConfigurationRegistry().getObjectTypeConf( EntryPoint.DEFAULT,
                                                                                                                                                  InitialFactImpl.getInstance() ),
                                                                                       wm,
                                                                                       wm);

            wm.queueWorkingMemoryAction( new WorkingMemoryReteAssertAction( handle,
                                                                            false,
                                                                            true,
                                                                            null,
                                                                            null ) );
            return wm;
        } finally {
            this.ruleBase.readUnlock();
        }
    }
    
	public void addEventListener(final WorkingMemoryEventListener listener) {
        this.workingMemoryEventSupport.addEventListener( listener );
    }

    public void removeEventListener(final WorkingMemoryEventListener listener) {
        this.workingMemoryEventSupport.removeEventListener( listener );
    }

    public List getWorkingMemoryEventListeners() {
        return this.workingMemoryEventSupport.getEventListeners();
    }

    public void addEventListener(final AgendaEventListener listener) {
        this.agendaEventSupport.addEventListener( listener );
    }

    public void removeEventListener(final AgendaEventListener listener) {
        this.agendaEventSupport.removeEventListener( listener );
    }

    public List getAgendaEventListeners() {
        return this.agendaEventSupport.getEventListeners();
    }

    public void addEventListener(RuleBaseEventListener listener) {
        this.ruleBase.addEventListener( listener );
    }

    public List getRuleBaseEventListeners() {
        return this.ruleBase.getRuleBaseEventListeners();
    }

    public void removeEventListener(RuleBaseEventListener listener) {
        this.ruleBase.removeEventListener( listener );
    }

    public void setAgendaFilter(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public void setGlobal(String identifier,
                          Object value) {
        this.globalResolver.setGlobal( identifier,
                                       value );
    }

    public void setGlobalResolver(GlobalResolver globalResolver) {
        this.globalResolver = globalResolver;
    }

    public void setGlobalExporter(GlobalExporter globalExporter) {
        this.globalExporter = globalExporter;
    }

    public void execute(Object object) {
        InternalWorkingMemory wm = newWorkingMemory();

        wm.insert( object );
        wm.fireAllRules( this.agendaFilter );
    }

    public void execute(Object[] array) {
        InternalWorkingMemory wm = newWorkingMemory();

        for ( int i = 0, length = array.length; i < length; i++ ) {
            wm.insert( array[i] );
        }
        wm.fireAllRules( this.agendaFilter );
    }

    public void execute(Collection collection) {
        InternalWorkingMemory wm = newWorkingMemory();

        for ( Iterator it = collection.iterator(); it.hasNext(); ) {
            wm.insert( it.next() );
        }
        wm.fireAllRules( this.agendaFilter );
    }

    public void asyncExecute(final Object object) {
        InternalWorkingMemory wm = newWorkingMemory();

        final AssertObject assertObject = new AssertObject( object );
        ExecutorService executor = ExecutorServiceFactory.createExecutorService( this.ruleBase.getConfiguration().getExecutorService() );
        executor.setCommandExecutor( new CommandExecutor( wm ) );
        executor.submit( assertObject );
        executor.submit( new FireAllRules( this.agendaFilter ) );
    }

    public void asyncExecute(final Object[] array) {
        InternalWorkingMemory wm = newWorkingMemory();

        final AssertObjects assertObjects = new AssertObjects( array );
        ExecutorService executor = ExecutorServiceFactory.createExecutorService( this.ruleBase.getConfiguration().getExecutorService() );
        executor.setCommandExecutor( new CommandExecutor( wm ) );
        executor.submit( assertObjects );
        executor.submit( new FireAllRules( this.agendaFilter ) );
    }

    public void asyncExecute(final Collection collection) {
        InternalWorkingMemory wm = newWorkingMemory();

        final AssertObjects assertObjects = new AssertObjects( collection );
        ExecutorService executor = ExecutorServiceFactory.createExecutorService( this.ruleBase.getConfiguration().getExecutorService() );
        executor.setCommandExecutor( new CommandExecutor( wm ) );
        executor.submit( assertObjects );
        executor.submit( new FireAllRules( this.agendaFilter ) );
    }

    public StatelessSessionResult executeWithResults(Object object) {
        InternalWorkingMemory wm = newWorkingMemory();

        wm.insert( object );
        wm.fireAllRules( this.agendaFilter );

        GlobalResolver globalResolver = null;
        if ( this.globalExporter != null ) {
            globalResolver = this.globalExporter.export( wm );
        }
        return new ReteStatelessSessionResult( wm,
                                               globalResolver );
    }

    public StatelessSessionResult executeWithResults(Object[] array) {
        InternalWorkingMemory wm = newWorkingMemory();

        for ( int i = 0, length = array.length; i < length; i++ ) {
            wm.insert( array[i] );
        }
        wm.fireAllRules( this.agendaFilter );

        GlobalResolver globalResolver = null;
        if ( this.globalExporter != null ) {
            globalResolver = this.globalExporter.export( wm );
        }
        return new ReteStatelessSessionResult( wm,
                                               globalResolver );
    }

    public StatelessSessionResult executeWithResults(Collection collection) {
        InternalWorkingMemory wm = newWorkingMemory();

        for ( Iterator it = collection.iterator(); it.hasNext(); ) {
            wm.insert( it.next() );
        }
        wm.fireAllRules( this.agendaFilter );

        GlobalResolver globalResolver = null;
        if ( this.globalExporter != null ) {
            globalResolver = this.globalExporter.export( wm );
        }
        return new ReteStatelessSessionResult( wm,
                                               globalResolver );
    }
}
