package org.drools.base;

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

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.QueryResults;
import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemoryActions;
import org.drools.rule.Declaration;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaGroup;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;

public class DefaultKnowledgeHelper
    implements
    KnowledgeHelper {

    private static final long serialVersionUID = 3692991484157215686L;

    private Rule                               rule;
    private Activation                         activation;
    private Tuple                              tuple;
    private final InternalWorkingMemoryActions workingMemory;

    public DefaultKnowledgeHelper(final WorkingMemory workingMemory) {
        this.workingMemory = (InternalWorkingMemoryActions) workingMemory;
    }

    public DefaultKnowledgeHelper(final Activation agendaItem,
                                  final WorkingMemory workingMemory) {
        this.rule = agendaItem.getRule();
        this.activation = agendaItem;
        this.tuple = agendaItem.getTuple();
        this.workingMemory = (InternalWorkingMemoryActions) workingMemory;
    }

    public void setActivation(final Activation agendaItem) {
        this.rule = agendaItem.getRule();
        this.activation = agendaItem;
        this.tuple = agendaItem.getTuple();
    }

    public void assertObject(final Object object) throws FactException {
        assertObject( object,
                      false );
    }

    public void assertObject(final Object object,
                             final boolean dynamic) throws FactException {
        this.workingMemory.assertObject( object,
                                         dynamic,
                                         false,
                                         this.rule,
                                         this.activation );
    }

    public void assertLogicalObject(final Object object) throws FactException {
        assertLogicalObject( object,
                             false );
    }

    public void assertLogicalObject(final Object object,
                                    final boolean dynamic) throws FactException {
        this.workingMemory.assertObject( object,
                                         dynamic,
                                         true,
                                         this.rule,
                                         this.activation );
    }

    public void modifyObject(final FactHandle handle,
                             final Object newObject) throws FactException {
        this.workingMemory.modifyObject( handle,
                                         newObject,
                                         this.rule,
                                         this.activation );
    }

    public void retractObject(final FactHandle handle) throws FactException {
        this.workingMemory.retractObject( handle,
                                          true,
                                          true,
                                          this.rule,
                                          this.activation );
    }

    public Rule getRule() {
        return this.rule;
    }

    public List getObjects() {
        return this.workingMemory.getObjects();
    }

    public List getObjects(final Class objectClass) {
        return this.workingMemory.getObjects( objectClass );
    }

    public void clearAgenda() {
        this.workingMemory.clearAgenda();
    }

    public void clearAgendaGroup(final String group) {
        this.workingMemory.clearAgendaGroup( group );
    }

    public Object get(final Declaration declaration) {
        return declaration.getValue( this.tuple.get( declaration ).getObject() );
    }

    public Tuple getTuple() {
        return this.tuple;
    }

    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }

    public Activation getActivation() {
        return this.activation;
    }

    public QueryResults getQueryResults(final String query) {
        return this.workingMemory.getQueryResults( query );
    }

    public AgendaGroup getFocus() {
        return this.workingMemory.getFocus();
    }

    public void setFocus(final String focus) {
        this.workingMemory.setFocus( focus );
    }

    public void setFocus(final AgendaGroup focus) {
        this.workingMemory.setFocus( focus );
    }
}
