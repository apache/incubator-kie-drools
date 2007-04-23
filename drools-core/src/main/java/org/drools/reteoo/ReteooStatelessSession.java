package org.drools.reteoo;

import java.util.Iterator;
import java.util.List;

import org.drools.ObjectFilter;
import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.WorkingMemory;
import org.drools.common.InternalRuleBase;
import org.drools.concurrent.AssertObject;
import org.drools.concurrent.AssertObjects;
import org.drools.concurrent.ExecutorService;
import org.drools.concurrent.FireAllRules;
import org.drools.concurrent.Future;
import org.drools.event.AgendaEventListener;
import org.drools.spi.AgendaFilter;
import org.drools.spi.GlobalResolver;

public class ReteooStatelessSession implements StatelessSession {
    private WorkingMemory workingMemory;
    private final ExecutorService executor;
    private AgendaFilter agendaFilter;    

    public ReteooStatelessSession(final WorkingMemory workingMemory,
                                final ExecutorService executorService) {
        this.workingMemory = workingMemory;
        this.executor = executorService;
    }        

    public void addEventListener(AgendaEventListener listener) {
        this.workingMemory.addEventListener( listener );
    }

    public List getWorkingMemoryEventListeners() {
        return this.workingMemory.getWorkingMemoryEventListeners();
    }

    public void removeEventListener(AgendaEventListener listener) {
        this.removeEventListener( listener );   
    }

    public void setAgendaFilter(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }
    
    public void setGlobal(String identifier,
                          Object value) {
        this.workingMemory.setGlobal( identifier, value );
    }

    public void setGlobalResolver(GlobalResolver globalResolver) {
       this.workingMemory.setGlobalResolver( globalResolver );        
    }

    public void execute(Object object) {
        this.workingMemory.assertObject( object );
        this.workingMemory.fireAllRules( this.agendaFilter );
    }

    public void execute(Object[] list) {
        for ( int i = 0, length = list.length; i < length; i++ ) {
            this.workingMemory.assertObject( list[i] );
        }
        this.workingMemory.fireAllRules( this.agendaFilter );
    }

    public void execute(List list) {
        for( Iterator it = list.iterator(); it.hasNext(); ) {
            this.workingMemory.assertObject( it.next() );
        }
        this.workingMemory.fireAllRules( this.agendaFilter );
    }
    
    public void asyncExecute(final Object object) {
        final AssertObject assertObjects = new AssertObject( object );
        this.executor.submit( assertObjects );
        this.executor.submit( new FireAllRules( this.agendaFilter ) );
    }       
    
    public void asyncExecute(final Object[] list) {
        final AssertObjects assertObjects = new AssertObjects( list );
        this.executor.submit( assertObjects );
        this.executor.submit( new FireAllRules( this.agendaFilter ) );
    }
    
    public void asyncExecute(final List list) {
        final AssertObjects assertObjects = new AssertObjects( list );
        this.executor.submit( assertObjects );
        this.executor.submit( new FireAllRules( this.agendaFilter ) );
    }         
    
    public StatelessSessionResult executeWithResults(Object object) {
        this.workingMemory.assertObject( object );
        this.workingMemory.fireAllRules( this.agendaFilter );
        return new ReteStatelessSessionResult( this.workingMemory );
    }

    public StatelessSessionResult executeWithResults(Object[] list) {
        for ( int i = 0, length = list.length; i < length; i++ ) {
            this.workingMemory.assertObject( list[i] );
        }
        this.workingMemory.fireAllRules( this.agendaFilter );
        return new ReteStatelessSessionResult( this.workingMemory );
    }

    public StatelessSessionResult executeWithResults(List list) {
        for( Iterator it = list.iterator(); it.hasNext(); ) {
            this.workingMemory.assertObject( it.next() );
        }
        this.workingMemory.fireAllRules( this.agendaFilter );
        return new ReteStatelessSessionResult( this.workingMemory );
    }    
}
