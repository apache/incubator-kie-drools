package org.drools;

/*
 * Copyright 2005 JBoss Inc
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

import java.util.List;
import java.util.Map;

import org.drools.event.AgendaEventListener;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.AsyncExceptionHandler;
import org.drools.spi.GlobalResolver;

/**
 * Each implemented method of the WorkingMemory interface is synchronised.
 * This class simply delegates each method call to the underlying unsynchronized
 * WorkingMemoryImpl.
 * 
 * <preformat>
 * WorkingMemory workingMemory = new SynchronizedWorkingMemory( ruleBase.newWorkingMemory( ) );
 * </preformat>
 *   
 * @author mproctor
 *
 */
public class SynchronizedWorkingMemory
    implements
    WorkingMemory {
    /**
     * 
     */
    private static final long serialVersionUID = 8304004224308319285L;
    final WorkingMemory       workingMemory;

    public SynchronizedWorkingMemory(final WorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    public synchronized void addEventListener(final WorkingMemoryEventListener listener) {
        this.workingMemory.addEventListener( listener );
    }

    public synchronized FactHandle assertObject(final Object object,
                                                final boolean dynamic) throws FactException {
        return this.workingMemory.assertObject( object,
                                                dynamic );
    }

    public synchronized FactHandle assertObject(final Object object) throws FactException {
        return this.workingMemory.assertObject( object );
    }

    public synchronized void clearAgenda() {
        this.workingMemory.clearAgenda();
    }

    public synchronized void clearAgendaGroup(final String group) {
        this.workingMemory.clearAgendaGroup( group );
    }

    public synchronized void fireAllRules() throws FactException {
        this.workingMemory.fireAllRules();
    }

    public synchronized void fireAllRules(final AgendaFilter agendaFilter) throws FactException {
        this.workingMemory.fireAllRules( agendaFilter );
    }

    public synchronized FactHandle getFactHandle(final Object object) throws NoSuchFactHandleException {
        return this.workingMemory.getFactHandle( object );
    }

    public synchronized List getFactHandles() {
        return this.workingMemory.getFactHandles();
    }

    public synchronized Object getObject(final FactHandle handle) throws NoSuchFactObjectException {
        return this.workingMemory.getObject( handle );
    }

    public synchronized List getObjects() {
        return this.workingMemory.getObjects();
    }

    public synchronized List getObjects(final Class objectClass) {
        return this.workingMemory.getObjects( objectClass );
    }

    public synchronized RuleBase getRuleBase() {
        return this.workingMemory.getRuleBase();
    }

    public synchronized void modifyObject(final FactHandle handle,
                                          final Object object) throws FactException {
        this.workingMemory.modifyObject( handle,
                                         object );
    }

    public synchronized void removeEventListener(final WorkingMemoryEventListener listener) {
        this.workingMemory.removeEventListener( listener );
    }

    public synchronized void retractObject(final FactHandle handle) throws FactException {
        this.workingMemory.retractObject( handle );
    }

    public synchronized void setAsyncExceptionHandler(final AsyncExceptionHandler handler) {
        this.workingMemory.setAsyncExceptionHandler( handler );
    }

    public void addEventListener(final AgendaEventListener listener) {
        this.workingMemory.addEventListener( listener );

    }

    public void dispose() {
        this.workingMemory.dispose();

    }

    public Agenda getAgenda() {
        return this.workingMemory.getAgenda();
    }

    public List getAgendaEventListeners() {
        return this.workingMemory.getAgendaEventListeners();
    }

    public AgendaGroup getFocus() {
        return this.workingMemory.getFocus();
    }

    public Object getGlobal(final String name) {
        return this.workingMemory.getGlobal( name );
    }

    public Map getGlobals() {
        return this.workingMemory.getGlobals();
    }

    public void setGlobalResolver(final GlobalResolver globalResolver) {
        this.workingMemory.setGlobalResolver( globalResolver );
    }

    public QueryResults getQueryResults(final String query) {
        return this.workingMemory.getQueryResults( query );
    }

    public List getWorkingMemoryEventListeners() {
        return this.workingMemory.getWorkingMemoryEventListeners();
    }

    public void removeEventListener(final AgendaEventListener listener) {
        this.workingMemory.removeEventListener( listener );
    }

    public void setFocus(final String focus) {
        this.workingMemory.setFocus( focus );
    }

    public void setFocus(final AgendaGroup focus) {
        this.workingMemory.setFocus( focus );
    }

    public void setGlobal(final String name,
                          final Object value) {
        this.workingMemory.setGlobal( name,
                                      value );
    }
}