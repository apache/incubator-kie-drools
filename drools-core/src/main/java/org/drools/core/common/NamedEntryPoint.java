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

import org.drools.core.RuleBaseConfiguration.AssertBehaviour;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.base.TraitHelper;
import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.traits.TraitProxy;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl.ObjectStoreWrapper;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.Activation;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import static org.drools.core.reteoo.PropertySpecificUtil.allSetButTraitBitMask;

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

    private ObjectTypeConfigurationRegistry typeConfReg;

    private final StatefulKnowledgeSessionImpl wm;

    private FactHandleFactory         handleFactory;
    private PropagationContextFactory pctxFactory;

    protected final ReentrantLock lock;

    protected Set<InternalFactHandle> dynamicFacts = null;

    protected TraitHelper traitHelper;

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
        this.typeConfReg = new ObjectTypeConfigurationRegistry(this.kBase);
        this.handleFactory = this.wm.getFactHandleFactory();
        this.pctxFactory = kBase.getConfiguration().getComponentFactory().getPropagationContextFactory();
        this.objectStore = new ClassAwareObjectStore(this.kBase.getConfiguration(), this.lock);
        this.traitHelper = new TraitHelper( wm, this );
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public void reset() {
        this.objectStore.clear();
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
        return insert(object,
                      dynamic,
                      null,
                      null);
    }

    public FactHandle insert(final Object object,
                             final boolean dynamic,
                             final RuleImpl rule,
                             final Activation activation) {
        if ( object == null ) {
            // you cannot assert a null object
            return null;
        }

        try {
            this.wm.startOperation();

            ObjectTypeConf typeConf = this.typeConfReg.getObjectTypeConf( this.entryPoint,
                                                                          object );

            InternalFactHandle handle = null;
            final PropagationContext propagationContext = this.pctxFactory.createPropagationContext(this.wm.getNextPropagationIdCounter(), PropagationContext.INSERTION, rule,
                                                                                                    (activation == null) ? null : activation.getTuple(), handle, entryPoint);

            if ( this.wm.isSequential() ) {
                handle = createHandle( object,
                                       typeConf );
                propagationContext.setFactHandle(handle);
                insert( handle,
                        object,
                        rule,
                        activation,
                        typeConf,
                        propagationContext );
                return handle;
            }

            
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
                    TruthMaintenanceSystem tms = getTruthMaintenanceSystem();

                    EqualityKey key;
                    if ( handle != null && handle.getEqualityKey().getStatus() == EqualityKey.STATED ) {
                        // it's already stated, so just return the handle
                        return handle;
                    } else {
                        key = tms.get( object );
                    }

                    if ( key != null && key.getStatus() == EqualityKey.JUSTIFIED ) {
                        // The justified set needs to be staged, before we can continue with the stated insert
                        BeliefSet bs = handle.getEqualityKey().getBeliefSet();
                        bs.getBeliefSystem().stage( propagationContext, bs ); // staging will set it's status to stated
                    }

                    handle = createHandle( object,
                                           typeConf ); // we know the handle is null
                    if ( key == null ) {
                        key = new EqualityKey( handle, EqualityKey.STATED  );
                        tms.put( key );
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
                        activation,
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

    public void insert(final InternalFactHandle handle,
                       final Object object,
                       final RuleImpl rule,
                       final Activation activation,
                       ObjectTypeConf typeConf,
                       PropagationContext pctx) {
        this.kBase.executeQueuedActions();

        this.wm.executeQueuedActionsForRete();

        if ( activation != null ) {
            // release resources so that they can be GC'ed
            activation.getPropagationContext().releaseResources();
        }
        PropagationContext propagationContext = pctx;
        if ( pctx == null ) {
            propagationContext = pctxFactory.createPropagationContext(this.wm.getNextPropagationIdCounter(), PropagationContext.INSERTION,
                                                                      rule, (activation == null) ? null : activation.getTuple(), handle, entryPoint);
        }

        this.objectStore.addHandle( handle,
                                    object );
        this.entryPointNode.assertObject( handle,
                                          propagationContext,
                                          typeConf,
                                          this.wm );

        propagationContext.evaluateActionQueue( this.wm );

        this.wm.getRuleRuntimeEventSupport().fireObjectInserted(propagationContext,
                                                                handle,
                                                                object,
                                                                this.wm);

        this.wm.executeQueuedActionsForRete();

        if ( rule == null ) {
            // This is not needed for internal WM actions as the firing rule will unstage
            wm.getAgenda().unstageActivations();
        }
    }

    public void update(final FactHandle factHandle,
                       final Object object) {
        InternalFactHandle handle = (InternalFactHandle) factHandle;
        update( handle,
                object,
                allSetButTraitBitMask(),
                Object.class,
                null );
    }

    public void update(final FactHandle factHandle,
                       final Object object,
                       final BitMask mask,
                       final Class<?> modifiedClass,
                       final Activation activation) {
        InternalFactHandle handle = (InternalFactHandle) factHandle;
        update( handle,
                object,
                mask,
                modifiedClass,
                activation );
    }

    public InternalFactHandle update(InternalFactHandle handle,
                                     final Object object,
                                     final BitMask mask,
                                     final Class<?> modifiedClass,
                                     final Activation activation) {
        try {
            this.lock.lock();
            this.wm.startOperation();
            this.kBase.executeQueuedActions();


            // the handle might have been disconnected, so reconnect if it has
            if ( handle.isDisconnected() ) {
                handle = this.objectStore.reconnect( handle );
            }

            final Object originalObject = handle.getObject();
            
            if ( handle.getEntryPoint() != this ) {
                throw new IllegalArgumentException( "Invalid Entry Point. You updated the FactHandle on entry point '" + handle.getEntryPoint().getEntryPointId() + "' instead of '" + getEntryPointId() + "'" );
            }
            
            final ObjectTypeConf typeConf = this.typeConfReg.getObjectTypeConf( this.entryPoint,
                                                                                object );


            if ( handle.getId() == -1 || object == null || (handle.isEvent() && ((EventFactHandle) handle).isExpired()) ) {
                // the handle is invalid, most likely already retracted, so return and we cannot assert a null object
                return handle;
            }

            if ( activation != null ) {
                // release resources so that they can be GC'ed
                activation.getPropagationContext().releaseResources();
            }

            if ( originalObject != object || !AssertBehaviour.IDENTITY.equals( this.kBase.getConfiguration().getAssertBehaviour() ) ) {
                this.objectStore.updateHandle(handle, object);
            }

            this.handleFactory.increaseFactHandleRecency( handle );
            RuleImpl rule = activation == null ? null : activation.getRule();

            final PropagationContext propagationContext = pctxFactory.createPropagationContext(this.wm.getNextPropagationIdCounter(), PropagationContext.MODIFICATION,
                                                                                               rule, (activation == null) ? null : activation.getTuple(),
                                                                                               handle, entryPoint, mask, modifiedClass, null);
            
            if ( typeConf.isTMSEnabled() ) {
                EqualityKey newKey = tms.get( object );
                EqualityKey oldKey = handle.getEqualityKey();

                if ( ( oldKey.getStatus() == EqualityKey.JUSTIFIED || oldKey.getBeliefSet() != null ) && newKey != oldKey ) {
                    // Mixed stated and justified, we cannot have updates untill we figure out how to use this.
                    throw new IllegalStateException("Currently we cannot modify something that has mixed stated and justified equal objects. " +
                                                    "Rule " + activation.getRule().getName() + " attempted an illegal operation" );
                }

                if ( newKey == null ) {
                    oldKey.removeFactHandle( handle );
                    newKey = new EqualityKey( handle,
                                              EqualityKey.STATED ); // updates are always stated
                    handle.setEqualityKey( newKey );
                    getTruthMaintenanceSystem().put( newKey );


                } else if ( newKey != oldKey ) {
                    oldKey.removeFactHandle( handle );
                    handle.setEqualityKey( newKey );
                    newKey.addFactHandle( handle );
                }

                // If the old equality key is now empty, and no justified entries, remove it
                if ( oldKey.isEmpty() && oldKey.getLogicalFactHandle() == null  ) {
                    getTruthMaintenanceSystem().remove( oldKey );
                }
            }

            if ( handle.isTraitable() && object != originalObject
                 && object instanceof TraitableBean && originalObject instanceof TraitableBean ) {
                this.traitHelper.replaceCore( handle, object, originalObject, propagationContext.getModificationMask(), object.getClass(), activation );
            }

            update( handle, object, originalObject, typeConf, rule, propagationContext );

        } finally {
            this.wm.endOperation();
            this.lock.unlock();
        }
        return handle;
    }

    public void update(InternalFactHandle handle, Object object, Object originalObject, ObjectTypeConf typeConf, RuleImpl rule, PropagationContext propagationContext) {
        this.entryPointNode.modifyObject( handle,
                                          propagationContext,
                                          typeConf,
                                          this.wm );

        propagationContext.evaluateActionQueue( this.wm );

        this.wm.getRuleRuntimeEventSupport().fireObjectUpdated(propagationContext,
                                                               handle,
                                                               originalObject,
                                                               object,
                                                               this.wm);

        this.wm.executeQueuedActionsForRete();

        if ( rule == null ) {
            // This is not needed for internal WM actions as the firing rule will unstage
            wm.getAgenda().unstageActivations();
        }
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
                       Activation activation) {
        delete(factHandle, rule, activation, FactHandle.State.ALL);
    }

    public void delete(FactHandle factHandle,
                       RuleImpl rule,
                       Activation activation,
                       FactHandle.State fhState) {
        if ( factHandle == null ) {
            throw new IllegalArgumentException( "FactHandle cannot be null " );
        }

        try {
            this.lock.lock();
            this.wm.startOperation();
            this.kBase.executeQueuedActions();

            InternalFactHandle handle = (InternalFactHandle) factHandle;

            if ( handle.getId() == -1 ) {
                // can't retract an already retracted handle
                return;
            }

            // the handle might have been disconnected, so reconnect if it has
            if ( handle.isDisconnected() ) {
                handle = this.objectStore.reconnect( handle );
            }

            if ( handle.getEntryPoint() != this ) {
                throw new IllegalArgumentException( "Invalid Entry Point. You updated the FactHandle on entry point '" + handle.getEntryPoint().getEntryPointId() + "' instead of '" + getEntryPointId() + "'" );
            }

            EqualityKey key = handle.getEqualityKey();
            if (fhState.isStated()) {
                deleteStated( rule, activation, handle, key );
            }
            if (fhState.isLogical()) {
                deleteLogical( key );
            }
        } finally {
            this.wm.endOperation();
            this.lock.unlock();
        }
    }

    private void deleteStated( RuleImpl rule, Activation activation, InternalFactHandle handle, EqualityKey key ) {
        if ( key != null && key.getStatus() == EqualityKey.JUSTIFIED ) {
            return;
        }

        if ( handle.isTraitable() ) {
            traitHelper.deleteWMAssertedTraitProxies( handle, rule, activation );
        }

        final Object object = handle.getObject();

        final ObjectTypeConf typeConf = this.typeConfReg.getObjectTypeConf( this.entryPoint, object );

        if( typeConf.isSupportsPropertyChangeListeners() ) {
            removePropertyChangeListener( handle, true );
        }

        if ( activation != null ) {
            // release resources so that they can be GC'ed
            activation.getPropagationContext().releaseResources();
        }

        PropagationContext propagationContext = delete( handle, object, typeConf, rule, activation );

        deleteFromTMS( handle, key, typeConf, propagationContext );

        this.handleFactory.destroyFactHandle( handle );
    }

    private void deleteFromTMS( InternalFactHandle handle, EqualityKey key, ObjectTypeConf typeConf, PropagationContext propagationContext ) {
        if ( typeConf.isTMSEnabled() && key != null ) { // key can be null if we're expiring an event that has been already deleted
            TruthMaintenanceSystem tms = getTruthMaintenanceSystem();

            // Update the equality key, which maintains a list of stated FactHandles
            key.removeFactHandle( handle );
            handle.setEqualityKey( null );

            // If the equality key is now empty, then remove it, as it's no longer state either
            if ( key.isEmpty() && key.getLogicalFactHandle() == null ) {
                tms.remove( key );
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
        final PropagationContext propagationContext = pctxFactory.createPropagationContext( this.wm.getNextPropagationIdCounter(), PropagationContext.DELETION,
                                                                                            rule, ( activation == null ) ? null : activation.getTuple(),
                                                                                            handle, this.entryPoint );

        this.entryPointNode.retractObject( handle,
                                           propagationContext,
                                           typeConf,
                                           this.wm );

        if ( handle.isTraiting() && handle.getObject() instanceof TraitProxy ) {
            (( (TraitProxy) handle.getObject() ).getObject()).removeTrait( ( (TraitProxy) handle.getObject() )._getTypeCode() );
        } else if ( handle.isTraitable() ) {
            traitHelper.deleteWMAssertedTraitProxies( handle, rule, activation );
        }

        this.objectStore.removeHandle( handle );


        propagationContext.evaluateActionQueue( this.wm );

        this.wm.getRuleRuntimeEventSupport().fireObjectRetracted(propagationContext,
                                                                 handle,
                                                                 object,
                                                                 this.wm);

        this.wm.executeQueuedActionsForRete();


        if ( rule == null ) {
            // This is not needed for internal WM actions as the firing rule will unstage
            wm.getAgenda().unstageActivations();
        }

        return propagationContext;
    }

    public void removeFromObjectStore(InternalFactHandle handle) {
        this.objectStore.removeHandle( handle );
        ObjectTypeConf typeConf = this.typeConfReg.getObjectTypeConf( this.entryPoint, handle.getObject() );
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
                    dynamicFacts = new HashSet<InternalFactHandle>();
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
        Object object = null;
        try {
            object = ((InternalFactHandle) handle).getObject();
            
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
        return this.typeConfReg;
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
        update( handle, object );
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
        for( ObjectTypeConf conf : this.typeConfReg.values() ) {
            // then, we check if any of the object types were configured using the 
            // @propertyChangeSupport annotation, and clean them up
            if( conf.isDynamic() && conf.isSupportsPropertyChangeListeners() ) {
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

    public PropagationContextFactory getPctxFactory() {
        return pctxFactory;
    }

    public TraitHelper getTraitHelper() {
        return traitHelper;
    }

    @Override
    public String toString() {
        return entryPoint.toString();
    }
}
