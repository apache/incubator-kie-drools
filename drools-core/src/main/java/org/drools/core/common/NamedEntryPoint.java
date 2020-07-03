/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.common;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.drools.core.RuleBaseConfiguration.AssertBehaviour;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.base.TraitDisabledHelper;
import org.drools.core.base.TraitHelper;
import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl.ObjectStoreWrapper;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.Activation;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.bitmask.AllSetBitMask;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Arrays.asList;
import static org.drools.core.reteoo.PropertySpecificUtil.allSetBitMask;
import static org.drools.core.reteoo.PropertySpecificUtil.calculatePositiveMask;

public class NamedEntryPoint
        implements
        InternalWorkingMemoryEntryPoint,
        WorkingMemoryEntryPoint,
        PropertyChangeListener  {

    protected static final transient Logger log = LoggerFactory.getLogger(NamedEntryPoint.class);

    protected static final Class<?>[] ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES = new Class[]{PropertyChangeListener.class};

    /** The arguments used when adding/removing a property change listener. */
    protected final Object[] addRemovePropertyChangeListenerArgs = new Object[]{this};

    private TruthMaintenanceSystem tms;

    protected ObjectStore objectStore;

    protected transient InternalKnowledgeBase kBase;

    protected EntryPointId     entryPoint;
    protected EntryPointNode entryPointNode;

    protected StatefulKnowledgeSessionImpl wm;

    protected FactHandleFactory         handleFactory;
    protected PropagationContextFactory pctxFactory;

    protected ReentrantLock lock;

    protected Set<InternalFactHandle> dynamicFacts = null;

    protected NamedEntryPoint() {
        lock = null;
        wm = null;
    }

    public NamedEntryPoint(EntryPointId entryPoint,
                           EntryPointNode entryPointNode,
                           StatefulKnowledgeSessionImpl wm) {
        this(entryPoint,
             entryPointNode,
             wm,
             new ReentrantLock());
    }

    public NamedEntryPoint(EntryPointId entryPoint,
                           EntryPointNode entryPointNode,
                           StatefulKnowledgeSessionImpl wm,
                           ReentrantLock lock) {
        this.entryPoint = entryPoint;
        this.entryPointNode = entryPointNode;
        this.wm = wm;
        this.kBase = this.wm.getKnowledgeBase();
        this.lock = lock;
        this.handleFactory = this.wm.getFactHandleFactory();
        this.pctxFactory = kBase.getConfiguration().getComponentFactory().getPropagationContextFactory();
        this.objectStore = new ClassAwareObjectStore(this.kBase.getConfiguration(), this.lock);
    }

    protected NamedEntryPoint( EntryPointId entryPoint,
                               StatefulKnowledgeSessionImpl wm,
                               FactHandleFactory handleFactory,
                               ReentrantLock lock,
                               ObjectStore objectStore ) {
        this.entryPoint = entryPoint;
        this.wm = wm;
        this.handleFactory = handleFactory;
        this.lock = lock;
        this.objectStore = objectStore;
    }

     public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public void reset() {
        this.objectStore.clear();
        if (tms != null) {
            tms.clear();
        }
    }

    public ObjectStore getObjectStore() {
        return this.objectStore;
    }

    public EntryPointNode getEntryPointNode() {
        return this.entryPointNode;
    }

    public FactHandleFactory getHandleFactory() {
        return handleFactory;
    }

    /**
     * @see org.drools.core.WorkingMemory
     */
    public FactHandle insert(final Object object) {
        return insert(object, /* Not-Dynamic */
                      false,
                      null,
                      null);
    }

    public FactHandle insert(final Object object,
                             final boolean dynamic) {
        return insert(object, dynamic, null, null);
    }

    public FactHandle insert(final Object object,
                             final boolean dynamic,
                             final RuleImpl rule,
                             final TerminalNode terminalNode) {
        if ( object == null ) {
            // you cannot assert a null object
            return null;
        }

        try {
            this.wm.startOperation();

            ObjectTypeConf typeConf = getObjectTypeConfigurationRegistry().getObjectTypeConf( this.entryPoint, object );

            final PropagationContext propagationContext = this.pctxFactory.createPropagationContext(this.wm.getNextPropagationIdCounter(),
                                                                                                    PropagationContext.Type.INSERTION,
                                                                                                    rule,
                                                                                                    terminalNode,
                                                                                                    null,
                                                                                                    entryPoint);
            if ( this.wm.isSequential() ) {
                InternalFactHandle handle = createHandle( object, typeConf );
                propagationContext.setFactHandle(handle);
                insert( handle,
                        object,
                        rule,
                        typeConf,
                        propagationContext );
                return handle;
            }

            InternalFactHandle handle;
            try {
                this.lock.lock();

                // check if the object already exists in the WM
                handle = this.objectStore.getHandleForObject( object );

                if ( !typeConf.isTMSEnabled() ) {
                    // TMS not enabled for this object type
                    if ( handle != null ) {
                        return handle;
                    }
                    handle = createHandle( object,
                                           typeConf );
                } else {
                    TruthMaintenanceSystem truthMaintenanceSystem = getTruthMaintenanceSystem();

                    EqualityKey key;
                    if ( handle != null && handle.getEqualityKey().getStatus() == EqualityKey.STATED ) {
                        // it's already stated, so just return the handle
                        return handle;
                    } else {
                        key = truthMaintenanceSystem.get( object );
                    }

                    if ( handle != null && key != null && key.getStatus() == EqualityKey.JUSTIFIED && handle != null) {
                        // The justified set needs to be staged, before we can continue with the stated insert
                        BeliefSet bs = handle.getEqualityKey().getBeliefSet();
                        bs.getBeliefSystem().stage( propagationContext, bs ); // staging will set it's status to stated
                    }

                    handle = createHandle( object,
                                           typeConf ); // we know the handle is null
                    if ( key == null ) {
                        key = new EqualityKey( handle, EqualityKey.STATED  );
                        truthMaintenanceSystem.put( key );
                    } else {
                        key.addFactHandle( handle );
                    }
                    handle.setEqualityKey( key );
                }

                propagationContext.setFactHandle(handle);

                // if the dynamic parameter is true or if the user declared the fact type with the meta tag:
                // @propertyChangeSupport
                if ( dynamic || typeConf.isDynamic() ) {
                    addPropertyChangeListener( handle, dynamic );
                }

                insert( handle,
                        object,
                        rule,
                        typeConf,
                        propagationContext );

            } finally {
                this.lock.unlock();
            }
            return handle;
        } finally {
            this.wm.endOperation();
        }

    }

    public void insert(InternalFactHandle handle,
                       Object object,
                       RuleImpl rule,
                       TerminalNode terminalNode,
                       ObjectTypeConf typeConf) {
        PropagationContext pctx = pctxFactory.createPropagationContext(this.wm.getNextPropagationIdCounter(), PropagationContext.Type.INSERTION,
                                                                       rule, terminalNode, handle, entryPoint);
        insert( handle, object, rule, typeConf, pctx );
    }

    public void insert(final InternalFactHandle handle,
                        final Object object,
                        final RuleImpl rule,
                        ObjectTypeConf typeConf,
                        PropagationContext pctx) {
        this.kBase.executeQueuedActions();

        this.objectStore.addHandle( handle,
                                    object );
        this.entryPointNode.assertObject( handle,
                                          pctx,
                                          typeConf,
                                          this.wm );

        this.wm.getRuleRuntimeEventSupport().fireObjectInserted(pctx,
                                                                handle,
                                                                object,
                                                                this.wm);
    }

    public FactHandle insertAsync(Object object) {
        ObjectTypeConf typeConf = getObjectTypeConfigurationRegistry().getObjectTypeConf( this.entryPoint, object );

        PropagationContext pctx = this.pctxFactory.createPropagationContext(this.wm.getNextPropagationIdCounter(),
                                                                            PropagationContext.Type.INSERTION,
                                                                            null, null, null, entryPoint);
        InternalFactHandle handle = createHandle( object, typeConf );
        pctx.setFactHandle(handle);

        this.entryPointNode.assertObject( handle, pctx, typeConf, this.wm );
        this.wm.getRuleRuntimeEventSupport().fireObjectInserted(pctx, handle, object, this.wm);
        return handle;
    }

    public void update(final FactHandle factHandle,
                       final Object object) {
        update( (InternalFactHandle) factHandle,
                object,
                allSetBitMask(),
                Object.class,
                null );
    }

    public void update(FactHandle handle,
                       Object object,
                       String... modifiedProperties) {
        Class modifiedClass = object.getClass();

        TypeDeclaration typeDeclaration = kBase.getOrCreateExactTypeDeclaration( modifiedClass );
        BitMask mask = typeDeclaration.isPropertyReactive() ?
                       calculatePositiveMask( modifiedClass, asList(modifiedProperties), typeDeclaration.getAccessibleProperties() ) :
                       AllSetBitMask.get();

        update( (InternalFactHandle) handle, object, mask, modifiedClass, null);
    }

    public void update(final FactHandle factHandle,
                       final Object object,
                       final BitMask mask,
                       final Class<?> modifiedClass,
                       final Activation activation) {
        update( (InternalFactHandle) factHandle, object, mask, modifiedClass, activation );
    }

    public InternalFactHandle update(InternalFactHandle handle,
                                     final Object object,
                                     final BitMask mask,
                                     final Class<?> modifiedClass,
                                     final Activation activation) {
        this.lock.lock();
        try {
            this.wm.startOperation();
            try {
                this.kBase.executeQueuedActions();

                // the handle might have been disconnected, so reconnect if it has
                if (handle.isDisconnected()) {
                    handle = this.objectStore.reconnect(handle);
                }

                final Object originalObject = handle.getObject();

                if (!handle.getEntryPointId().equals( entryPoint )) {
                    throw new IllegalArgumentException("Invalid Entry Point. You updated the FactHandle on entry point '" + handle.getEntryPointId() + "' instead of '" + getEntryPointId() + "'");
                }

                final ObjectTypeConf typeConf = getObjectTypeConfigurationRegistry().getObjectTypeConf(this.entryPoint, object);

                if (originalObject != object || !AssertBehaviour.IDENTITY.equals(this.kBase.getConfiguration().getAssertBehaviour())) {
                    this.objectStore.updateHandle(handle, object);
                }

                this.handleFactory.increaseFactHandleRecency(handle);

                final PropagationContext propagationContext = pctxFactory.createPropagationContext(this.wm.getNextPropagationIdCounter(), PropagationContext.Type.MODIFICATION,
                                                                                                   activation == null ? null : activation.getRule(),
                                                                                                   activation == null ? null : activation.getTuple().getTupleSink(),
                                                                                                   handle, entryPoint, mask, modifiedClass, null);

                if (typeConf.isTMSEnabled()) {
                    EqualityKey newKey = tms.get(object);
                    EqualityKey oldKey = handle.getEqualityKey();

                    if ((oldKey.getStatus() == EqualityKey.JUSTIFIED || oldKey.getBeliefSet() != null) && newKey != oldKey) {
                        // Mixed stated and justified, we cannot have updates untill we figure out how to use this.
                        throw new IllegalStateException("Currently we cannot modify something that has mixed stated and justified equal objects. " +
                                                                "Rule " + (activation == null ? "" : activation.getRule().getName()) + " attempted an illegal operation");
                    }

                    if (newKey == null) {
                        oldKey.removeFactHandle(handle);
                        newKey = new EqualityKey(handle,
                                                 EqualityKey.STATED); // updates are always stated
                        handle.setEqualityKey(newKey);
                        getTruthMaintenanceSystem().put(newKey);
                    } else if (newKey != oldKey) {
                        oldKey.removeFactHandle(handle);
                        handle.setEqualityKey(newKey);
                        newKey.addFactHandle(handle);
                    }

                    // If the old equality key is now empty, and no justified entries, remove it
                    if (oldKey.isEmpty() && oldKey.getLogicalFactHandle() == null) {
                        getTruthMaintenanceSystem().remove(oldKey);
                    }
                }

                beforeUpdate(handle, object, activation, originalObject, propagationContext);

                update(handle, object, originalObject, typeConf, propagationContext);
            } finally {
                this.wm.endOperation();
            }
        } finally {
            this.lock.unlock();
        }
        return handle;
    }

    protected void beforeUpdate(InternalFactHandle handle, Object object, Activation activation, Object originalObject, PropagationContext propagationContext) {
    }

    public void update(InternalFactHandle handle, Object object, Object originalObject, ObjectTypeConf typeConf, PropagationContext propagationContext) {
        this.entryPointNode.modifyObject( handle,
                                          propagationContext,
                                          typeConf,
                                          this.wm );

        this.wm.getRuleRuntimeEventSupport().fireObjectUpdated(propagationContext,
                                                               handle,
                                                               originalObject,
                                                               object,
                                                               this.wm);
    }

    public void retract(final FactHandle handle) {
        delete( handle );
    }

    public void delete(final FactHandle handle) {
        delete( handle, null, null );
    }

    public void delete(final FactHandle handle, FactHandle.State fhState) {
        delete( handle, null, null, fhState );
    }

    public void delete(FactHandle factHandle,
                       RuleImpl rule,
                       TerminalNode terminalNode) {
        delete(factHandle, rule, terminalNode, FactHandle.State.ALL);
    }

    public void delete(FactHandle factHandle,
                       RuleImpl rule,
                       TerminalNode terminalNode,
                       FactHandle.State fhState) {
        if ( factHandle == null ) {
            throw new IllegalArgumentException( "FactHandle cannot be null " );
        }

        this.lock.lock();
        try {
            this.wm.startOperation();
            try {
                this.kBase.executeQueuedActions();

                InternalFactHandle handle = (InternalFactHandle) factHandle;

                if (handle.getId() == -1) {
                    // can't retract an already retracted handle
                    return;
                }

                // the handle might have been disconnected, so reconnect if it has
                if (handle.isDisconnected()) {
                    handle = this.objectStore.reconnect(handle);
                }

                if (!handle.getEntryPointId().equals( entryPoint )) {
                    throw new IllegalArgumentException("Invalid Entry Point. You updated the FactHandle on entry point '" + handle.getEntryPointId() + "' instead of '" + getEntryPointId() + "'");
                }

                EqualityKey key = handle.getEqualityKey();
                if (fhState.isStated()) {
                    deleteStated(rule, terminalNode, handle, key);
                }
                if (fhState.isLogical()) {
                    deleteLogical(key);
                }
            } finally {
                this.wm.endOperation();
            }
        } finally {
            this.lock.unlock();
        }
    }

    private void deleteStated( RuleImpl rule, TerminalNode terminalNode, InternalFactHandle handle, EqualityKey key ) {
        if ( key != null && key.getStatus() == EqualityKey.JUSTIFIED ) {
            return;
        }

        beforeDestroy(rule, terminalNode, handle);

        final Object object = handle.getObject();

        final ObjectTypeConf typeConf = getObjectTypeConfigurationRegistry().getObjectTypeConf( this.entryPoint, object );

        if( typeConf.isDynamic() ) {
            removePropertyChangeListener( handle, true );
        }

        PropagationContext propagationContext = delete( handle, object, typeConf, rule, null, terminalNode );

        deleteFromTMS( handle, key, typeConf, propagationContext );

        this.handleFactory.destroyFactHandle( handle );
    }

    protected void beforeDestroy(RuleImpl rule, TerminalNode terminalNode, InternalFactHandle handle) {

    }

    private void deleteFromTMS( InternalFactHandle handle, EqualityKey key, ObjectTypeConf typeConf, PropagationContext propagationContext ) {
        if ( typeConf.isTMSEnabled() && key != null ) { // key can be null if we're expiring an event that has been already deleted
            TruthMaintenanceSystem truthMaintenanceSystem = getTruthMaintenanceSystem();

            // Update the equality key, which maintains a list of stated FactHandles
            key.removeFactHandle( handle );
            handle.setEqualityKey( null );

            // If the equality key is now empty, then remove it, as it's no longer state either
            if ( key.isEmpty() && key.getLogicalFactHandle() == null ) {
                truthMaintenanceSystem.remove( key );
            } else if ( key.getLogicalFactHandle() != null ) {
                // The justified set can be unstaged, now that the last stated has been deleted
                final InternalFactHandle justifiedHandle = key.getLogicalFactHandle();
                BeliefSet bs = justifiedHandle.getEqualityKey().getBeliefSet();
                bs.getBeliefSystem().unstage( propagationContext, bs );
            }
        }
    }

    private void deleteLogical(EqualityKey key) {
        if ( key != null && key.getStatus() == EqualityKey.JUSTIFIED ) {
            getTruthMaintenanceSystem().delete( key.getLogicalFactHandle() );
        }
    }

    public PropagationContext delete(InternalFactHandle handle, Object object, ObjectTypeConf typeConf, RuleImpl rule, Activation activation) {
        return delete( handle, object, typeConf, rule, activation, activation == null ? null : activation.getTuple().getTupleSink() );
    }

    public PropagationContext delete(InternalFactHandle handle, Object object, ObjectTypeConf typeConf, RuleImpl rule, Activation activation, TerminalNode terminalNode) {
        final PropagationContext propagationContext = pctxFactory.createPropagationContext( this.wm.getNextPropagationIdCounter(), PropagationContext.Type.DELETION,
                                                                                            rule, terminalNode,
                                                                                            handle, this.entryPoint );

        this.entryPointNode.retractObject( handle,
                                           propagationContext,
                                           typeConf,
                                           this.wm );

        afterRetract(handle, rule, terminalNode);

        this.objectStore.removeHandle( handle );


        this.wm.getRuleRuntimeEventSupport().fireObjectRetracted(propagationContext,
                                                                 handle,
                                                                 object,
                                                                 this.wm);
        return propagationContext;
    }

    protected void afterRetract(InternalFactHandle handle, RuleImpl rule, TerminalNode terminalNode) {

    }

    public void removeFromObjectStore(InternalFactHandle handle) {
        this.objectStore.removeHandle( handle );
        ObjectTypeConf typeConf = getObjectTypeConfigurationRegistry().getObjectTypeConf( this.entryPoint, handle.getObject() );
        deleteFromTMS( handle, handle.getEqualityKey(), typeConf, null );
    }

    protected void addPropertyChangeListener(final InternalFactHandle handle, final boolean dynamicFlag ) {
        Object object = handle.getObject();
        try {
            final Method method = object.getClass().getMethod( "addPropertyChangeListener",
                                                               NamedEntryPoint.ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES );

            method.invoke( object,
                           this.addRemovePropertyChangeListenerArgs );

            if( dynamicFlag ) {
                if( dynamicFacts == null ) {
                    dynamicFacts = new HashSet<>();
                }
                dynamicFacts.add( handle );
            }
        } catch ( final NoSuchMethodException e ) {
            log.error( "Warning: Method addPropertyChangeListener not found" + " on the class " + object.getClass() + " so Drools will be unable to process JavaBean" + " PropertyChangeEvents on the asserted Object" );
        } catch ( final IllegalArgumentException e ) {
            log.error( "Warning: The addPropertyChangeListener method" + " on the class " + object.getClass() + " does not take" + " a simple PropertyChangeListener argument" + " so Drools will be unable to process JavaBean"
                       + " PropertyChangeEvents on the asserted Object" );
        } catch ( final IllegalAccessException e ) {
            log.error( "Warning: The addPropertyChangeListener method" + " on the class " + object.getClass() + " is not public" + " so Drools will be unable to process JavaBean" + " PropertyChangeEvents on the asserted Object" );
        } catch ( final InvocationTargetException e ) {
            log.error( "Warning: The addPropertyChangeListener method" + " on the class " + object.getClass() + " threw an InvocationTargetException" + " so Drools will be unable to process JavaBean"
                       + " PropertyChangeEvents on the asserted Object: " + e.getMessage() );
        } catch ( final SecurityException e ) {
            log.error( "Warning: The SecurityManager controlling the class " + object.getClass() + " did not allow the lookup of a" + " addPropertyChangeListener method" + " so Drools will be unable to process JavaBean"
                       + " PropertyChangeEvents on the asserted Object: " + e.getMessage() );
        }
    }

    protected void removePropertyChangeListener(final FactHandle handle, final boolean removeFromSet ) {
        Object object = ((InternalFactHandle) handle).getObject();
        try {
            if ( dynamicFacts != null && removeFromSet ) {
                dynamicFacts.remove( object );
            }

            if ( object != null ) {
                final Method mehod = object.getClass().getMethod( "removePropertyChangeListener",
                                                                  NamedEntryPoint.ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES );

                mehod.invoke( object,
                              this.addRemovePropertyChangeListenerArgs );
            }
        } catch ( final NoSuchMethodException e ) {
            // The removePropertyChangeListener method on the class
            // was not found so Drools will be unable to
            // stop processing JavaBean PropertyChangeEvents
            // on the retracted Object
        } catch ( final IllegalArgumentException e ) {
            throw new RuntimeException( "Warning: The removePropertyChangeListener method on the class " + object.getClass() + " does not take a simple PropertyChangeListener argument so Drools will be unable to stop processing JavaBean"
                                        + " PropertyChangeEvents on the retracted Object" );
        } catch ( final IllegalAccessException e ) {
            throw new RuntimeException( "Warning: The removePropertyChangeListener method on the class " + object.getClass() + " is not public so Drools will be unable to stop processing JavaBean PropertyChangeEvents on the retracted Object" );
        } catch ( final InvocationTargetException e ) {
            throw new RuntimeException( "Warning: The removePropertyChangeL istener method on the class " + object.getClass() + " threw an InvocationTargetException so Drools will be unable to stop processing JavaBean"
                                        + " PropertyChangeEvents on the retracted Object: " + e.getMessage() );
        } catch ( final SecurityException e ) {
            throw new RuntimeException( "Warning: The SecurityManager controlling the class " + object.getClass() + " did not allow the lookup of a removePropertyChangeListener method so Drools will be unable to stop processing JavaBean"
                                        + " PropertyChangeEvents on the retracted Object: " + e.getMessage() );
        }
    }

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name) {
        return this.wm.getWorkingMemoryEntryPoint( name );
    }

    public ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry() {
        return entryPointNode.getTypeConfReg();
    }

    public InternalKnowledgeBase getKnowledgeBase() {
        return kBase;
    }

    public FactHandle getFactHandle(Object object) {
        return this.objectStore.getHandleForObject( object );
    }

    public EntryPointId getEntryPoint() {
        return this.entryPoint;
    }

    public InternalWorkingMemory getInternalWorkingMemory() {
        return this.wm;
    }

    public FactHandle getFactHandleByIdentity(final Object object) {
        return this.objectStore.getHandleForObjectIdentity( object );
    }

    public Object getObject(FactHandle factHandle) {
        return this.objectStore.getObjectForHandle((InternalFactHandle)factHandle);
    }

    @SuppressWarnings("unchecked")
    public <T extends FactHandle> Collection<T> getFactHandles() {
        return new ObjectStoreWrapper( this.objectStore,
                                       null,
                                       ObjectStoreWrapper.FACT_HANDLE );
    }

    @SuppressWarnings("unchecked")
    public <T extends FactHandle> Collection<T> getFactHandles(org.kie.api.runtime.ObjectFilter filter) {
        return new ObjectStoreWrapper( this.objectStore,
                                       filter,
                                       ObjectStoreWrapper.FACT_HANDLE );
    }

    @SuppressWarnings("unchecked")
    public Collection<? extends Object> getObjects() {
        return new ObjectStoreWrapper( this.objectStore,
                                       null,
                                       ObjectStoreWrapper.OBJECT );
    }

    @SuppressWarnings("unchecked")
    public Collection<? extends Object> getObjects(org.kie.api.runtime.ObjectFilter filter) {
        return new ObjectStoreWrapper( this.objectStore,
                                       filter,
                                       ObjectStoreWrapper.OBJECT );
    }

    public String getEntryPointId() {
        return this.entryPoint.getEntryPointId();
    }

    public long getFactCount() {
        return this.objectStore.size();
    }

    private InternalFactHandle createHandle(final Object object,
                                            ObjectTypeConf typeConf) {
        return this.handleFactory.newFactHandle( object,
                                                 typeConf,
                                                 this.wm,
                                                 this );
    }

    public void propertyChange(final PropertyChangeEvent event) {
        final Object object = event.getSource();
        FactHandle handle = getFactHandle( object );
        if ( handle == null ) {
            throw new RuntimeException( "Update error: handle not found for object: " + object + ". Is it in the working memory?" );
        }
        update( handle, object, event.getPropertyName() );
    }

    public void dispose() {
        if( dynamicFacts != null ) {
            // first we check for facts that were inserted into the working memory
            // using the old API and setting a per instance dynamic flag and remove the
            // session from the listeners list in the bean
            for( InternalFactHandle handle : dynamicFacts ) {
                removePropertyChangeListener( handle, false );
            }
            dynamicFacts = null;
        }
        for( ObjectTypeConf conf : getObjectTypeConfigurationRegistry().values() ) {
            // then, we check if any of the object types were configured using the
            // @propertyChangeSupport annotation, and clean them up
            if( conf.isDynamic() ) {
                // it is enough to iterate the facts on the concrete object type nodes
                // only, as the facts will always be in their concrete object type nodes
                // even if they were also asserted into higher level OTNs as well
                ObjectTypeNode otn = conf.getConcreteObjectTypeNode();
                if (otn != null) {
                    Iterator<InternalFactHandle> it = this.getInternalWorkingMemory().getNodeMemory(otn).iterator();
                    while (it.hasNext()) {
                        removePropertyChangeListener(it.next(), false);
                    }
                }
            }
        }
    }

    public void enQueueWorkingMemoryAction(WorkingMemoryAction action) {
        wm.queueWorkingMemoryAction( action );
    }

    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        if (tms == null) {
            tms = new TruthMaintenanceSystem(wm, this);
        }
        return tms;
    }

    @Override
    public TraitHelper getTraitHelper() {
        return new TraitDisabledHelper();
    }

    public PropagationContextFactory getPctxFactory() {
        return pctxFactory;
    }

    @Override
    public String toString() {
        return entryPoint.toString();
    }

    private Object ruleUnit;

    @Override
    public Object getRuleUnit() {
        return ruleUnit;
    }

    @Override
    public void setRuleUnit(Object ruleUnit) {
        this.ruleUnit = ruleUnit;
    }
}
