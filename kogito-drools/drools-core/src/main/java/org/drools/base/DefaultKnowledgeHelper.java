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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemoryActions;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.runtime.ExitPoint;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemoryEntryPoint;
import org.drools.spi.Activation;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;

public class DefaultKnowledgeHelper
    implements
    KnowledgeHelper,
    Externalizable {

    private static final long            serialVersionUID = 400L;

    private Rule                         rule;
    private GroupElement                 subrule;
    private Activation                   activation;
    private Tuple                        tuple;
    private InternalWorkingMemoryActions workingMemory;

    private IdentityHashMap<Object,FactHandle>              identityMap;

    public DefaultKnowledgeHelper() {

    }

    public DefaultKnowledgeHelper(final WorkingMemory workingMemory) {
        this.workingMemory = (InternalWorkingMemoryActions) workingMemory;

       this.identityMap =  new IdentityHashMap<Object,FactHandle>();

    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        rule = (Rule) in.readObject();
        subrule = (GroupElement) in.readObject();
        activation = (Activation) in.readObject();
        tuple = (Tuple) in.readObject();
        workingMemory = (InternalWorkingMemoryActions) in.readObject();
        identityMap = (IdentityHashMap<Object,FactHandle> ) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( rule );
        out.writeObject( subrule );
        out.writeObject( activation );
        out.writeObject( tuple );
        out.writeObject( workingMemory );
         out.writeObject( identityMap );
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
        this.identityMap.clear();
    }

    public void insert(final Object object) throws FactException {
        insert( object,
                false );
    }

    public void insert(final Object object,
                       final boolean dynamic) throws FactException {
         FactHandle handle = this.workingMemory.insert( object,
                                   dynamic,
                                   false,
                                   this.rule,
                                   this.activation );
         this.getIdentityMap().put(object, handle);
    }

    public void insertLogical(final Object object) throws FactException {
        insertLogical( object,
                       false );
    }

    public void insertLogical(final Object object,
                              final boolean dynamic) throws FactException {
      FactHandle handle = this.workingMemory.insert( object,
                                   dynamic,
                                   true,
                                   this.rule,
                                   this.activation );
        this.getIdentityMap().put(object, handle);
    }

    public void update(final FactHandle handle,
                       final Object newObject) throws FactException {
        // only update if this fact exists in the wm

        ((InternalWorkingMemoryEntryPoint)((InternalFactHandle)handle).getEntryPoint())
                                .update( handle,
                                   newObject,
                                   this.rule,
                                   this.activation );
        this.getIdentityMap().put(newObject, handle);
    }

    public void update(final Object object) throws FactException {
        FactHandle handle = getFactHandle( object );
        if( handle == null ) {
            throw new FactException( "Update error: handle not found for object: " + object + ". Is it in the working memory?" );
        }
        update( handle, object );
    }

    public void retract(final FactHandle handle) throws FactException {
        ((InternalWorkingMemoryEntryPoint)((InternalFactHandle)handle).getEntryPoint())
                            .retract( handle,
                                    true,
                                    true,
                                    this.rule,
                                    this.activation );
         this.getIdentityMap().remove(((InternalFactHandle)handle).getObject());
    }

    public void retract(final Object object) throws FactException {
        FactHandle handle = getFactHandle( object );
        if ( handle == null ) {
            throw new FactException( "Retract error: handle not found for object: " + object + ". Is it in the working memory?" );
        }
        retract( handle );
    }

    public void modifyRetract(final Object object) {
        FactHandle handle = getFactHandle( object );
        if ( handle == null ) {
            throw new FactException( "Modify error: handle not found for object: " + object + ". Is it in the working memory?" );
        }
        modifyRetract( handle );
    }

    public void modifyRetract(final FactHandle factHandle) {
        ((InternalWorkingMemoryEntryPoint)((InternalFactHandle)factHandle).getEntryPoint())
                        .modifyRetract( factHandle,
                                          rule,
                                          activation );
    }

    public void modifyInsert(final Object object) {
        FactHandle handle = getFactHandle( object );
        if ( handle == null ) {
            throw new FactException( "Modify error: handle not found for object: " + object + ". Is it in the working memory?" );
        }
        modifyInsert( handle,
                      object );
    }

    public void modifyInsert(final FactHandle factHandle,
                             final Object object) {
        ((InternalWorkingMemoryEntryPoint)((InternalFactHandle)factHandle).getEntryPoint())
                            .modifyInsert( factHandle,
                                         object,
                                         rule,
                                         activation );
        this.getIdentityMap().put(object, factHandle);
    }

    public Rule getRule() {
        return this.rule;
    }

    public Tuple getTuple() {
        return this.tuple;
    }

    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }

    public KnowledgeRuntime getKnowledgeRuntime() {
        return new StatefulKnowledgeSessionImpl( (ReteooWorkingMemory) this.workingMemory );
    }

    public Activation getActivation() {
        return this.activation;
    }

    public void setFocus(final String focus) {
        this.workingMemory.setFocus( focus );
    }

    public Object get(final Declaration declaration) {
         InternalWorkingMemoryEntryPoint wmTmp = ((InternalWorkingMemoryEntryPoint)(this.tuple.get(declaration)).getEntryPoint());
         
        if(wmTmp != null){
        Object object = declaration.getValue( wmTmp.getInternalWorkingMemory() ,
                                     this.tuple.get( declaration ).getObject() );

                getIdentityMap().put(object, wmTmp.getFactHandleByIdentity(object));
                return object;
        }
        return null;
    }

    public Declaration getDeclaration(final String identifier) {
             return (Declaration) this.subrule.getOuterDeclarations().get( identifier );
    }

    public void halt() {
        this.workingMemory.halt();
    }

    public WorkingMemoryEntryPoint getEntryPoint(String id) {
        return this.workingMemory.getEntryPoints().get( id );
    }

    public ExitPoint getExitPoint(String id) {
        return this.workingMemory.getExitPoints().get( id );
    }

    public Map<String, WorkingMemoryEntryPoint> getEntryPoints() {
        return Collections.unmodifiableMap( this.workingMemory.getEntryPoints() );
    }

    public Map<String, ExitPoint> getExitPoints() {
        return Collections.unmodifiableMap( this.workingMemory.getExitPoints() );
    }

    /**
     * @return the identityMap
     */
    public IdentityHashMap<Object, FactHandle> getIdentityMap() {
        return identityMap;
}

    /**
     * @param identityMap the identityMap to set
     */
    public void setIdentityMap(IdentityHashMap<Object, FactHandle> identityMap) {
        this.identityMap = identityMap;
    }

    private FactHandle getFactHandle(final Object object) {
        FactHandle handle = identityMap.get(object);
        // entry point null means it is a generated fact, not a regular inserted fact
        // NOTE: it would probably be a good idea to create a specific attribute for that
        if ( handle == null || ((InternalFactHandle)handle).getEntryPoint() == null ) {
            for( WorkingMemoryEntryPoint ep : workingMemory.getEntryPoints().values() ) {
                handle = (FactHandle) ep.getFactHandle( object );
                if( handle != null ) {
                    identityMap.put( object, handle );
                    break;
                }
            }
        }
        return handle;
    }

}
