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

package org.drools.core.common;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.RuleBaseConfiguration.AssertBehaviour;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.base.ClassObjectType;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.traits.TraitProxy;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.util.Iterator;
import org.drools.core.util.ObjectHashSet;
import org.drools.core.util.ObjectHashSet.ObjectEntry;
import org.drools.core.impl.StatefulKnowledgeSessionImpl.ObjectStoreWrapper;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ObjectTypeNode.ObjectTypeNodeMemory;
import org.drools.core.reteoo.Rete;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.Activation;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        this.objectStore = new SingleThreadedObjectStore(this.kBase.getConfiguration(),
                                                         this.lock);
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


    /**
     * @see org.drools.core.WorkingMemory
     */
    public FactHandle insert(final Object object) {
        return insert(object, /* Not-Dynamic */
                      null,
                      false,
                      false,
                      null,
                      null);
    }

    public FactHandle insert(final Object object,
                             final boolean dynamic) {
        return insert(object,
                      null,
                      dynamic,
                      false,
                      null,
                      null);
    }

    public FactHandle insert(final Object object,
                             final Object tmsValue,
                             final boolean dynamic,
                             boolean logical,
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
            if ( logical && !typeConf.isTMSEnabled()) {
                enableTMS(object, typeConf);
            }

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
                this.kBase.readLock();
                // check if the object already exists in the WM
                handle = this.objectStore.getHandleForObject( object );

                if ( typeConf.isTMSEnabled() ) {
                    TruthMaintenanceSystem tms = getTruthMaintenanceSystem();
                    
                    if ( handle != null ) {
                        propagationContext.setFactHandle(handle);
                        insertWhenHandleExists( object, tmsValue, logical, rule, activation, typeConf, handle, tms, propagationContext );
                        return handle;
                    }

                    // get the key for other "equal" objects, returns null if none exist
                    EqualityKey key = tms.get( object );
                    
                    if ( logical ) {  
                        if ( key != null && key.getStatus() == EqualityKey.STATED ) {
                            // You cannot logically insert a previously stated equality equal object
                            return key.getFactHandle();
                        }
                        

                        
                        if ( key == null ) {
                            handle = createHandle( object,
                                                   typeConf ); // we know the handle is null
                            
                            key = new EqualityKey( handle ); 
                            handle.setEqualityKey( key );
                            tms.put( key );                           
                            key.setStatus( EqualityKey.JUSTIFIED ); // new Key, so we know it's JUSTIFIED                 
                        } else {
                            handle = key.getFactHandle();
                        }
                        
                       // Any logical propagations are handled via the TMS.addLogicalDependency
                       tms.addLogicalDependency( handle,
                                                 object,
                                                 tmsValue,
                                                 activation,
                                                 activation.getPropagationContext(),
                                                 rule,
                                                 typeConf );
                        
                        return key.getFactHandle(); 
                                                    
                    } else { // !logical                     
                        if ( key == null ) {
                            handle = createHandle( object,
                                                   typeConf ); // we know the handle is null                            
                            key = new EqualityKey( handle );                        
                            handle.setEqualityKey( key );                            
                            tms.put( key );                  
                        } else if ( key.getStatus() == EqualityKey.JUSTIFIED ) {
                                // Its previous justified, so switch to stated
                                key.setStatus( EqualityKey.STATED ); // must be done before the justifiedHandle retract  
                                
                                // remove logical dependencies
                                final InternalFactHandle justifiedHandle = key.getFactHandle();
                                propagationContext.setFactHandle( justifiedHandle ); // necessary to stop recursive retractions
                                TruthMaintenanceSystemHelper.clearLogicalDependencies( justifiedHandle, propagationContext );
                                
                                // now update existing handle to new value
                                return update( justifiedHandle, true, object, Long.MAX_VALUE, Object.class, activation );
                        } else   {  // STATED 
                            handle = createHandle( object,
                                                   typeConf ); // we know the handle is null                                                    
                            handle.setEqualityKey( key );                                                    
                            key.addFactHandle( handle );
                        }
                        key.setStatus( EqualityKey.STATED ); // KEY is always stated
                    }                    
                } else {
                    // TMS not enabled for this object type 
                    if ( handle != null ) {
                        return handle;
                    }
                    handle = createHandle( object,
                                           typeConf );
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
                this.kBase.readUnlock();
                this.lock.unlock();
            }
            return handle;
        } finally {
            this.wm.endOperation();
        }

    }

    private void insertWhenHandleExists(final Object object,
                                              final Object tmsValue,
                                              boolean logical,
                                              final RuleImpl rule,
                                              final Activation activation,
                                              ObjectTypeConf typeConf,
                                              InternalFactHandle handle,
                                              TruthMaintenanceSystem tms,
                                              final PropagationContext propagationContext ) {
        // Object is already asserted, so check and possibly correct its
        // status and then return the handle
        EqualityKey key = handle.getEqualityKey();

        if ( key == null ) {
            // Edge case: another object X, equivalent (equals+hashcode) to "object" Y
            // has been previously stated. However, if X is a subclass of Y, TMS
            // may have not been enabled yet, and key would be null.
            ObjectTypeConf typeC = this.typeConfReg.getObjectTypeConf( this.entryPoint, handle.getObject() );
            enableTMS( handle.getObject(), typeC );
            key = handle.getEqualityKey();
        }

        if ( key.getStatus() == EqualityKey.STATED ) {
            // return null as you cannot justify a stated object.
            return;
        }

        if ( !logical ) {
            // this object was previously justified, so we have to override it to stated
            key.setStatus( EqualityKey.STATED );
            TruthMaintenanceSystemHelper.removeLogicalDependencies( handle, propagationContext );
        } else {                                        
            // this was object is already justified, so just add new logical dependency
            tms.addLogicalDependency( handle,
                                      object,
                                      tmsValue,
                                      activation,
                                      activation.getPropagationContext(),
                                      rule,
                                      typeConf );
        }
    }

    public void insert(final InternalFactHandle handle,
                       final Object object,
                       final RuleImpl rule,
                       final Activation activation,
                       ObjectTypeConf typeConf,
                       PropagationContext pctx) {
        this.kBase.executeQueuedActions();

        this.wm.executeQueuedActions();

        if ( activation != null ) {
            // release resources so that they can be GC'ed
            activation.getPropagationContext().releaseResources();
        }
        PropagationContext propagationContext = pctx;
        if ( pctx == null ) {
            propagationContext = pctxFactory.createPropagationContext(this.wm.getNextPropagationIdCounter(), PropagationContext.INSERTION,
                                                                      rule, (activation == null) ? null : activation.getTuple(), handle, entryPoint);
        }

        this.entryPointNode.assertObject( handle,
                                          propagationContext,
                                          typeConf,
                                          this.wm );
        
        propagationContext.evaluateActionQueue( this.wm );

        this.wm.getRuleRuntimeEventSupport().fireObjectInserted(propagationContext,
                                                                handle,
                                                                object,
                                                                this.wm);
        
        this.wm.executeQueuedActions();        
        
        if ( rule == null ) {
            // This is not needed for internal WM actions as the firing rule will unstage
            ((InternalAgenda)this.wm.getAgenda()).unstageActivations();
        }        
    }

    public void update(final FactHandle factHandle,
                       final Object object) {
        InternalFactHandle handle = (InternalFactHandle) factHandle;
        update( handle,
                false,
                object,
                Long.MAX_VALUE,
                Object.class,
                null );
    }
    
    public void update(final FactHandle factHandle,
                       final Object object,
                       final long mask,
                       final Class<?> modifiedClass,
                       final Activation activation) {
        InternalFactHandle handle = (InternalFactHandle) factHandle;
        update( handle,
                false,
                object,
                mask,
                modifiedClass,
                activation );
    }

    public InternalFactHandle update(InternalFactHandle handle,
                                     final boolean updateLogical,
                                     final Object object,
                                     final long mask,
                                     final Class<?> modifiedClass,
                                     final Activation activation) {
        try {
            this.lock.lock();
            this.kBase.readLock();
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

            // only needed if we maintain tms, but either way we must get it before we do the update
            int status = -1;
            if ( typeConf.isTMSEnabled() ) {
                status = handle.getEqualityKey().getStatus();
            }


            if ( handle.getId() == -1 || object == null || (handle.isEvent() && ((EventFactHandle) handle).isExpired()) ) {
                // the handle is invalid, most likely already retracted, so return and we cannot assert a null object
                return handle;
            }

            if ( activation != null ) {
                // release resources so that they can be GC'ed
                activation.getPropagationContext().releaseResources();
            }

            if ( originalObject != object || !AssertBehaviour.IDENTITY.equals( this.kBase.getConfiguration().getAssertBehaviour() ) ) {
                this.objectStore.removeHandle( handle );

                // set anyway, so that it updates the hashCodes
                handle.setObject( object );
                this.objectStore.addHandle( handle,
                                            object );
            }

            this.handleFactory.increaseFactHandleRecency( handle );
            RuleImpl rule = activation == null ? null : activation.getRule();

            final PropagationContext propagationContext = pctxFactory.createPropagationContext(this.wm.getNextPropagationIdCounter(), PropagationContext.MODIFICATION,
                                                                                               rule, (activation == null) ? null : activation.getTuple(),
                                                                                               handle, entryPoint, mask, modifiedClass, null);
            
            if ( typeConf.isTMSEnabled() ) {
                EqualityKey newKey = tms.get( object );
                EqualityKey oldKey = handle.getEqualityKey();
                if ( newKey == null ) {                    
                    if ( oldKey.getStatus() == EqualityKey.JUSTIFIED ) {
                        // new target key is JUSTFIED, updates are always STATED
                        TruthMaintenanceSystemHelper.removeLogicalDependencies( oldKey.getFactHandle(), propagationContext );
                    }
                    
                    oldKey.removeFactHandle( handle );
                    // If the equality key is now empty, then remove it
                    if ( oldKey.isEmpty() ) {
                        getTruthMaintenanceSystem().remove( oldKey );
                    }                    
                    
                    newKey = new EqualityKey( handle,
                                              EqualityKey.STATED ); // updates are always stated
                    handle.setEqualityKey( newKey );
                    getTruthMaintenanceSystem().put( newKey );
                } else if ( newKey != oldKey ) {
                    oldKey.removeFactHandle( handle );
                    // If the equality key is now empty, then remove it
                    if ( oldKey.isEmpty() ) {
                        getTruthMaintenanceSystem().remove( oldKey );
                    }  
                    
                    if ( newKey.getStatus() == EqualityKey.JUSTIFIED ) {
                        // new target key is JUSTITIED, updates are always STATED
                        TruthMaintenanceSystemHelper.removeLogicalDependencies( newKey.getFactHandle(), propagationContext );
                        newKey.setStatus( EqualityKey.STATED );
                    }
                    // the caller needs the new handle
                    handle = newKey.getFactHandle();
                } else if ( !updateLogical &&  oldKey.getStatus() == EqualityKey.JUSTIFIED  ) {
                    // new target key is JUSTIFIED, updates are always STATED
                    TruthMaintenanceSystemHelper.removeLogicalDependencies( oldKey.getFactHandle(), propagationContext );                     
                }
            }

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

           this.wm.executeQueuedActions();
           
           if ( rule == null ) {
               // This is not needed for internal WM actions as the firing rule will unstage
               ((InternalAgenda)this.wm.getAgenda()).unstageActivations();
           }           
        } finally {
            this.wm.endOperation();
            this.kBase.readUnlock();
            this.lock.unlock();
        }
        return handle;
    }

    public void retract(final FactHandle handle) {
        delete( (FactHandle) handle,
                 null,
                 null );
    }

    public void delete(final FactHandle handle) {
        delete( (FactHandle) handle,
                 null,
                 null );
    }

    public void delete(final FactHandle factHandle,
                       final RuleImpl rule,
                       final Activation activation) {
        if ( factHandle == null ) {
            throw new IllegalArgumentException( "FactHandle cannot be null " );
        }
        try {
            this.lock.lock();
            this.kBase.readLock();
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

            if ( handle.isTraitable() ) {
                TraitableBean traitableBean = (TraitableBean) handle.getObject();
                if( traitableBean.hasTraits() ){
                    PriorityQueue<TraitProxy> removedTypes =
                            new PriorityQueue<TraitProxy>( traitableBean._getTraitMap().values().size() );
                    removedTypes.addAll( traitableBean._getTraitMap().values() );

                    while ( ! removedTypes.isEmpty() ) {
                        TraitProxy proxy = removedTypes.poll();
                        if ( ! proxy.isVirtual() ) {
                            delete( getFactHandle( proxy ),
                                    rule,
                                    activation );
                        }
                    }
                }
            }

            if ( handle.getEntryPoint() != this ) {
                throw new IllegalArgumentException( "Invalid Entry Point. You updated the FactHandle on entry point '" + handle.getEntryPoint().getEntryPointId() + "' instead of '" + getEntryPointId() + "'" );
            }            

            final Object object = handle.getObject();
            
            final ObjectTypeConf typeConf = this.typeConfReg.getObjectTypeConf( this.entryPoint,
                                                                                object );

            if( typeConf.isSupportsPropertyChangeListeners() ) {
                removePropertyChangeListener( handle, true );
            }          
            
            if ( activation != null ) {
                // release resources so that they can be GC'ed
                activation.getPropagationContext().releaseResources();
            }
            final PropagationContext propagationContext = pctxFactory.createPropagationContext(this.wm.getNextPropagationIdCounter(), PropagationContext.DELETION,
                                                                                               rule, (activation == null) ? null : activation.getTuple(),
                                                                                               handle, this.entryPoint);

            this.entryPointNode.retractObject( handle,
                                               propagationContext,
                                               typeConf,
                                               this.wm );

            if ( typeConf.isTMSEnabled() ) {
                TruthMaintenanceSystem tms = getTruthMaintenanceSystem();

                // TMS.removeLogicalDependency also cleans up Handles from the EqualityKey
                // This can happen on the logical retraction of the last FH, where it's cleaned up in the TMS and also in the main network.
                // However when the user retracts the FH to a logical set of insertions, then we need to clean up the TMS here.
                                                   
                // Update the equality key, which maintains a list of stated FactHandles
                final EqualityKey key = handle.getEqualityKey();

                // Its justified so attempt to remove any logical dependencies for the handle
                if ( key.getStatus() == EqualityKey.JUSTIFIED ) {
                    TruthMaintenanceSystemHelper.removeLogicalDependencies( handle, propagationContext );
                } 
                key.removeFactHandle( handle );
                handle.setEqualityKey( null );
                
                // If the equality key is now empty, then remove it
                if ( key.isEmpty() ) {
                    tms.remove( key );
                }
            }

            if ( handle.isTraiting() && handle.getObject() instanceof TraitProxy ) {
                (( (TraitProxy) handle.getObject() ).getObject()).removeTrait( ( (TraitProxy) handle.getObject() ).getTypeCode() );
            }

            propagationContext.evaluateActionQueue( this.wm );
            

            this.wm.getRuleRuntimeEventSupport().fireObjectRetracted(propagationContext,
                                                                     handle,
                                                                     object,
                                                                     this.wm);

            this.wm.executeQueuedActions();
            
            this.objectStore.removeHandle( handle );
            this.handleFactory.destroyFactHandle( handle );            
            
            if ( rule == null ) {
                // This is not needed for internal WM actions as the firing rule will unstage
                ((InternalAgenda)this.wm.getAgenda()).unstageActivations();
            }            
        } finally {
            this.wm.endOperation();
            this.kBase.readUnlock();
            this.lock.unlock();
        }
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
        return this.objectStore.getObjectForHandle(factHandle);
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
        InternalFactHandle handle;
        handle = this.handleFactory.newFactHandle( object,
                                                   typeConf,
                                                   this.wm,
                                                   this );
        this.objectStore.addHandle( handle,
                                    object );
        return handle;
    }    
    
    /**
     * TMS will be automatically enabled when the first logical insert happens. 
     * 
     * We will take all the already asserted objects of the same type and initialize
     * the equality map.
     *  
     * @param object the logically inserted object.
     * @param conf the type's configuration.
     */
    private void enableTMS(Object object, ObjectTypeConf conf) {
        final Rete source = this.kBase.getRete();
        final ClassObjectType cot = new ClassObjectType( object.getClass() );
        final Map<ObjectType, ObjectTypeNode> map = source.getObjectTypeNodes( EntryPointId.DEFAULT );
        final ObjectTypeNode node = map.get( cot );
        final ObjectHashSet memory = ((ObjectTypeNodeMemory) this.wm.getNodeMemory( node )).memory;
      
        // All objects of this type that are already there were certainly stated,
        // since this method call happens at the first logical insert, for any given type.
        org.drools.core.util.Iterator it = memory.iterator();

        for ( Object obj = it.next(); obj != null; obj = it.next() ) {
          
            org.drools.core.util.ObjectHashSet.ObjectEntry holder = (org.drools.core.util.ObjectHashSet.ObjectEntry) obj;
    
            InternalFactHandle handle = (InternalFactHandle) holder.getValue();
            
            if ( handle != null && handle.getEqualityKey() == null ) {
                EqualityKey key = new EqualityKey( handle );
                handle.setEqualityKey( key );
                key.setStatus(EqualityKey.STATED);
                getTruthMaintenanceSystem().put(key);
            }
        }
      
        // Enable TMS for this type.
        conf.enableTMS();
      
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
                final ObjectHashSet memory = ((ObjectTypeNodeMemory) this.getInternalWorkingMemory().getNodeMemory( otn )).memory;
                Iterator it = memory.iterator();
                for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
                    InternalFactHandle handle = (InternalFactHandle) entry.getValue();
                    removePropertyChangeListener( handle, false );
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
}
