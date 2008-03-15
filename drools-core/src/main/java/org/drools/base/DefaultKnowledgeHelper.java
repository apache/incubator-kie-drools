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

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemoryActions;
import org.drools.rule.Declaration;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class DefaultKnowledgeHelper
    implements
    KnowledgeHelper {

    private static final long                  serialVersionUID = 400L;

    private Rule                               rule;
    private GroupElement                       subrule;
    private Activation                         activation;
    private Tuple                              tuple;
    private InternalWorkingMemoryActions workingMemory;

    public DefaultKnowledgeHelper() {
    }

    public DefaultKnowledgeHelper(final WorkingMemory workingMemory) {
        this.workingMemory = (InternalWorkingMemoryActions) workingMemory;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        rule    = (Rule)in.readObject();
        subrule    = (GroupElement)in.readObject();
        activation    = (Activation)in.readObject();
        tuple    = (Tuple)in.readObject();
        workingMemory    = (InternalWorkingMemoryActions)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(rule);
        out.writeObject(subrule);
        out.writeObject(activation);
        out.writeObject(tuple);
        out.writeObject(workingMemory);
    }
    
    public void setActivation(final Activation agendaItem) {
        this.rule = agendaItem.getRule();
        this.subrule = agendaItem.getSubRule();
        this.activation = agendaItem;
        this.tuple = agendaItem.getTuple();
    }

    public void reset() {
        this.rule = null;
        this.subrule = null;
        this.activation = null;
        this.tuple = null;
    }

    public void insert(final Object object) throws FactException {
        insert( object,
                false );
    }

    public void insert(final Object object,
                       final boolean dynamic) throws FactException {
        this.workingMemory.insert( object,
                                   dynamic,
                                   false,
                                   this.rule,
                                   this.activation );
    }

    public void insertLogical(final Object object) throws FactException {
        insertLogical( object,
                       false );
    }

    public void insertLogical(final Object object,
                              final boolean dynamic) throws FactException {
        this.workingMemory.insert( object,
                                   dynamic,
                                   true,
                                   this.rule,
                                   this.activation );
    }

    public void update(final FactHandle handle,
                       final Object newObject) throws FactException {
        // only update if this fact exists in the wm
        this.workingMemory.update( handle,
                                   newObject,
                                   this.rule,
                                   this.activation );
    }

    public void update(final Object object) throws FactException {
        FactHandle handle = this.workingMemory.getFactHandleByIdentity( object );
        if ( handle == null ) {
            throw new FactException( "Update error: handle not found for object: " + object + ". Is it in the working memory?" );
        }
        // only update if this fact exists in the wm
        this.workingMemory.update( handle,
                                   object,
                                   this.rule,
                                   this.activation );
    }

    public void retract(final FactHandle handle) throws FactException {
        this.workingMemory.retract( handle,
                                    true,
                                    true,
                                    this.rule,
                                    this.activation );
    }

    public void retract(final Object object) throws FactException {
        FactHandle handle = this.workingMemory.getFactHandleByIdentity( object );
        if ( handle == null ) {
            throw new FactException( "Retract error: handle not found for object: " + object + ". Is it in the working memory?" );
        }
        this.workingMemory.retract( handle,
                                    true,
                                    true,
                                    this.rule,
                                    this.activation );
    }

    public void modifyRetract(final Object object) {
        FactHandle handle = this.workingMemory.getFactHandleByIdentity( object );
        this.workingMemory.modifyRetract( handle, rule, activation );
    }

    public void modifyRetract(final FactHandle factHandle) {
        this.workingMemory.modifyRetract( factHandle, rule, activation );
    }

    public void modifyInsert(final Object object) {
        FactHandle handle = this.workingMemory.getFactHandleByIdentity( object );
        this.workingMemory.modifyInsert( handle, object, rule, activation );
    }

    public void modifyInsert(final FactHandle factHandle,
                             final Object object) {
        this.workingMemory.modifyInsert( factHandle, object, rule, activation );
    }

    public Rule getRule() {
        return this.rule;
    }

    //    public List getObjects() {
    //        return null; //this.workingMemory.getObjects();
    //    }
    //
    //    public List getObjects(final Class objectClass) {
    //        return null; //this.workingMemory.getObjects( objectClass );
    //    }
    //
    //    public void clearAgenda() {
    //        this.workingMemory.clearAgenda();
    //    }
    //
    //    public void clearAgendaGroup(final String group) {
    //        this.workingMemory.clearAgendaGroup( group );
    //    }

    public Tuple getTuple() {
        return this.tuple;
    }

    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }

    public Activation getActivation() {
        return this.activation;
    }

    //    public QueryResults getQueryResults(final String query) {
    //        return this.workingMemory.getQueryResults( query );
    //    }
    //
    //    public AgendaGroup getFocus() {
    //        return this.workingMemory.getFocus();
    //    }
    //
    public void setFocus(final String focus) {
        this.workingMemory.setFocus( focus );
    }

    //
    //    public void setFocus(final AgendaGroup focus) {
    //        this.workingMemory.setFocus( focus );
    //    }

    public Object get(final Declaration declaration) {
        return declaration.getValue( workingMemory, this.tuple.get( declaration ).getObject() );
    }

    public Declaration getDeclaration(final String identifier) {
        return (Declaration) this.subrule.getOuterDeclarations().get( identifier );
    }

    public void halt() {
        this.workingMemory.halt();
    }
}
