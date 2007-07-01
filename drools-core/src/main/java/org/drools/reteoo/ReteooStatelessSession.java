package org.drools.reteoo;

import java.util.Collection;
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
        this.workingMemory.insert( object );
        this.workingMemory.fireAllRules( this.agendaFilter );
    }

    public void execute(Object[] array) {
        for ( int i = 0, length = array.length; i < length; i++ ) {
            this.workingMemory.insert( array[i] );
        }
        this.workingMemory.fireAllRules( this.agendaFilter );
    }

    public void execute(Collection collection) {
        for( Iterator it = collection.iterator(); it.hasNext(); ) {
            this.workingMemory.insert( it.next() );
        }
        this.workingMemory.fireAllRules( this.agendaFilter );
    }
    
    public void asyncExecute(final Object object) {
        final AssertObject assertObject = new AssertObject( object );
        this.executor.submit( assertObject );
        this.executor.submit( new FireAllRules( this.agendaFilter ) );
    }       
    
    public void asyncExecute(final Object[] array) {
        final AssertObjects assertObjects = new AssertObjects( array );
        this.executor.submit( assertObjects );
        this.executor.submit( new FireAllRules( this.agendaFilter ) );
    }
    
    public void asyncExecute(final Collection collection) {
        final AssertObjects assertObjects = new AssertObjects( collection );
        this.executor.submit( assertObjects );
        this.executor.submit( new FireAllRules( this.agendaFilter ) );
    }         
    
    public StatelessSessionResult executeWithResults(Object object) {
        this.workingMemory.insert( object );
        this.workingMemory.fireAllRules( this.agendaFilter );
        return new ReteStatelessSessionResult( this.workingMemory );
    }

    public StatelessSessionResult executeWithResults(Object[] array) {
        for ( int i = 0, length = array.length; i < length; i++ ) {
            this.workingMemory.insert( array[i] );
        }
        this.workingMemory.fireAllRules( this.agendaFilter );
        return new ReteStatelessSessionResult( this.workingMemory );
    }

    public StatelessSessionResult executeWithResults(Collection collection) {
        for( Iterator it = collection.iterator(); it.hasNext(); ) {
            this.workingMemory.insert( it.next() );
        }
        this.workingMemory.fireAllRules( this.agendaFilter );
        return new ReteStatelessSessionResult( this.workingMemory );
    }    
}
