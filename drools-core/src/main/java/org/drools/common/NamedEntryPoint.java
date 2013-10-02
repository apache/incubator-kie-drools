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

package org.drools.common;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.RuntimeDroolsException;
import org.drools.WorkingMemory;
import org.drools.WorkingMemoryEntryPoint;
import org.drools.base.ClassObjectType;
import org.drools.core.util.Iterator;
import org.drools.core.util.ObjectHashSet;
import org.drools.core.util.ObjectHashSet.ObjectEntry;
import org.drools.definition.type.FactType;
import org.drools.factmodel.traits.TraitProxy;
import org.drools.factmodel.traits.TraitableBean;
import org.drools.impl.StatefulKnowledgeSessionImpl.ObjectStoreWrapper;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ObjectTypeNode.ObjectTypeNodeMemory;
import org.drools.reteoo.Rete;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NamedEntryPoint
    implements
    InternalWorkingMemoryEntryPoint,
    WorkingMemoryEntryPoint,
    PropertyChangeListener  {

    protected static transient Logger logger = LoggerFactory.getLogger(NamedEntryPoint.class);
    
    protected static final Class<?>[]       ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES = new Class[]{PropertyChangeListener.class};
    
    /** The arguments used when adding/removing a property change listener. */
    protected final Object[]                addRemovePropertyChangeListenerArgs = new Object[]{this};


    protected ObjectStore                   objectStore;

    protected transient InternalRuleBase    ruleBase;

    protected EntryPoint                    entryPoint;
    protected EntryPointNode                entryPointNode;

    private ObjectTypeConfigurationRegistry typeConfReg;

    private final AbstractWorkingMemory     wm;

    private FactHandleFactory               handleFactory;

    protected final ReentrantLock           lock;
    
    protected Set<InternalFactHandle>       dynamicFacts = null;

    public NamedEntryPoint(EntryPoint entryPoint,
                           EntryPointNode entryPointNode,
                           AbstractWorkingMemory wm) {
        this( entryPoint,
              entryPointNode,
              wm,
              new ReentrantLock() );
    }

    public NamedEntryPoint(EntryPoint entryPoint,
                           EntryPointNode entryPointNode,
                           AbstractWorkingMemory wm,
                           ReentrantLock lock) {
        this.entryPoint = entryPoint;
        this.entryPointNode = entryPointNode;
        this.wm = wm;
        this.ruleBase = (InternalRuleBase) this.wm.getRuleBase();
        this.lock = lock;
        this.typeConfReg = new ObjectTypeConfigurationRegistry( this.ruleBase );
        this.handleFactory = this.wm.getFactHandleFactory();
        this.objectStore = new SingleThreadedObjectStore( this.ruleBase.getConfiguration(),
                                                          this.lock );
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
     * @see WorkingMemory
     */
    public FactHandle insert(final Object object) throws FactException {
        return insert( object, /* Not-Dynamic */
                       null,
                       false,
                       false,
                       null,
                       null );
    }

    public FactHandle insert(final Object object,
                             final boolean dynamic) throws FactException {
        return insert( object,
                       null,
                       dynamic,
                       false,
                       null,
                       null );
    }

    protected FactHandle insert(final Object object,
                                final Object tmsValue,
                                final boolean dynamic,
                                boolean logical,
                                final Rule rule,
                                final Activation activation) throws FactException {
        if ( object == null ) {
            // you cannot assert a null object
            return null;
        }

        try {
            this.wm.startOperation();

            // ADDED, NOT IN THE ORIGINAL 6.x COMMIT
            wm.initInitialFact();

            ObjectTypeConf typeConf = this.typeConfReg.getObjectTypeConf( this.entryPoint,
                                                                          object );
            if ( logical && !typeConf.isTMSEnabled()) {
                enableTMS(object, typeConf);
            }

            InternalFactHandle handle = null;

            if ( this.wm.isSequential() ) {
                handle = createHandle( object,
                                       typeConf );
                insert( handle,
                        object,
                        rule,
                        activation,
                        typeConf );
                return handle;
            }
            try {
                this.lock.lock();
                this.ruleBase.readLock();
                // check if the object already exists in the WM
                handle = this.objectStore.getHandleForObject( object );

                if ( typeConf.isTMSEnabled() ) {
                  
                    EqualityKey key;

                    TruthMaintenanceSystem tms = wm.getTruthMaintenanceSystem();
                    if ( handle == null ) {
                        // lets see if the object is already logical asserted
                        key = tms.get( object );
                    } else {
                        // Object is already asserted, so check and possibly correct its
                        // status and then return the handle
                        key = handle.getEqualityKey();

                        if ( key == null ) {
                            // Edge case: another object X, equivalent (equals+hashcode) to "object" Y
                            // has been previously stated. However, if X is a subclass of Y, TMS
                            // may have not been enabled yet, and key would be null.
                            ObjectTypeConf typeC = this.typeConfReg.getObjectTypeConf( this.entryPoint,
                                    handle.getObject() );
                            enableTMS( handle.getObject(), typeC );
                            key = handle.getEqualityKey();
                        }

                        if ( key.getStatus() == EqualityKey.STATED ) {
                            // return null as you cannot justify a stated object.
                            return handle;
                        }

                        if ( !logical ) {
                            // this object was previously justified, so we have to override it to stated
                            key.setStatus( EqualityKey.STATED );
                            tms.removeLogicalDependencies( handle );
                        } else {
                            // this was object is already justified, so just add new logical dependency
                            tms.addLogicalDependency( handle,
                                                      tmsValue,
                                                      activation,
                                                      activation.getPropagationContext(),
                                                      rule,
                                                      typeConf );
                        }
                        return handle;
                    }

                    // At this point we know the handle is null
                    if ( key == null ) {
                      
                        handle = createHandle( object,
                                               typeConf );

                        key = createEqualityKey(handle);
                        
                        tms.put( key );
                        
                        if ( !logical ) {
                            key.setStatus( EqualityKey.STATED );
                        } else {
                            key.setStatus( EqualityKey.JUSTIFIED );
                            tms.addLogicalDependency( handle,
                                                      tmsValue,
                                                      activation,
                                                      activation.getPropagationContext(),
                                                      rule,
                                                      typeConf );
                            return handle;
                        }
                    } else if ( !logical ) {
                        if ( key.getStatus() == EqualityKey.JUSTIFIED ) {
                            // Its previous justified, so switch to stated and remove logical dependencies
                            final InternalFactHandle justifiedHandle = key.getFactHandle();
                            tms.removeLogicalDependencies( justifiedHandle );

                            if ( this.wm.discardOnLogicalOverride ) {
                                // override, setting to new instance, and return
                                // existing handle
                                key.setStatus( EqualityKey.STATED );
                                handle = key.getFactHandle();

                                if ( AssertBehaviour.IDENTITY.equals( this.ruleBase.getConfiguration().getAssertBehaviour() ) ) {
                                    // as assertMap may be using an "identity"
                                    // equality comparator,
                                    // we need to remove the handle from the map,
                                    // before replacing the object
                                    // and then re-add the handle. Otherwise we may
                                    // end up with a leak.
                                    this.objectStore.updateHandle( handle,
                                                                   object );
                                }
                                return handle;
                            } else {
                                // override, then instantiate new handle for
                                // assertion
                                key.setStatus( EqualityKey.STATED );
                                handle = createHandle( object,
                                                       typeConf );
                                handle.setEqualityKey( key );
                                key.addFactHandle( handle );
                            }

                        } else {
                            handle = createHandle( object,
                                                   typeConf );
                            key.addFactHandle( handle );
                            handle.setEqualityKey( key );

                        }

                    } else {
                        if ( key.getStatus() == EqualityKey.JUSTIFIED ) {
                            // only add as logical dependency if this wasn't previously stated
                            tms.addLogicalDependency( key.getFactHandle(),
                                                      tmsValue,
                                                      activation,
                                                      activation.getPropagationContext(),
                                                      rule,
                                                      typeConf );
                            return key.getFactHandle();
                        } else {
                            // You cannot justify a previously stated equality equal object, so return null
                            return null;
                        }
                    }

                } else {
                    if ( handle != null ) {
                        return handle;
                    }
                    handle = createHandle( object,
                                           typeConf );

                }

                // if the dynamic parameter is true or if the user declared the fact type with the meta tag:
                // @propertyChangeSupport
                if ( dynamic || typeConf.isDynamic() ) {
                    addPropertyChangeListener( handle, dynamic );
                }

                insert( handle,
                        object,
                        rule,
                        activation,
                        typeConf );

            } finally {
                this.ruleBase.readUnlock();
                this.lock.unlock();
            }
            return handle;
        } finally {
            this.wm.endOperation();
        }

    }

    public void insert(final InternalFactHandle handle,
                       final Object object,
                       final Rule rule,
                       final Activation activation,
                       ObjectTypeConf typeConf) {
        this.ruleBase.executeQueuedActions();
        
        this.wm.executeQueuedActions();

        if ( activation != null ) {
            // release resources so that they can be GC'ed
            activation.getPropagationContext().releaseResources();
        }
        final PropagationContext propagationContext = new PropagationContextImpl( this.wm.getNextPropagationIdCounter(),
                                                                                  PropagationContext.ASSERTION,
                                                                                  rule,
                                                                                  (activation == null) ? null : activation.getTuple(),
                                                                                  handle,
                                                                                  this.wm.agenda.getActiveActivations(),
                                                                                  this.wm.agenda.getDormantActivations(),
                                                                                  entryPoint );

        this.entryPointNode.assertObject( handle,
                                          propagationContext,
                                          typeConf,
                                          this.wm );
        
        propagationContext.evaluateActionQueue( this.wm );

        this.wm.workingMemoryEventSupport.fireObjectInserted( propagationContext,
                                                              handle,
                                                              object,
                                                              this.wm );
        
        this.wm.executeQueuedActions();        
        
        if ( rule == null ) {
            // This is not needed for internal WM actions as the firing rule will unstage
            this.wm.getAgenda().unstageActivations();
        }        
    }

    public void update(final org.drools.runtime.rule.FactHandle handle,
                       final Object object) throws FactException {
        update( handle,
                object,
                Long.MAX_VALUE,
                Object.class,
                null );
    }
    
    public void update(final org.drools.runtime.rule.FactHandle factHandle,
                       final Object object,
                       final long mask,
                       final Class<?> modifiedClass,
                       final Activation activation) throws FactException {

        update( (org.drools.FactHandle) factHandle,
                object,
                mask,
                modifiedClass,
                activation );
    }

    public void update(org.drools.FactHandle factHandle,
                       final Object object,
                       final long mask,
                       final Class<?> modifiedClass,
                       final Activation activation) throws FactException {
        try {
            this.lock.lock();
            this.ruleBase.readLock();
            this.wm.startOperation();
            this.ruleBase.executeQueuedActions();
            
            InternalFactHandle handle = (InternalFactHandle) factHandle;

            // the handle might have been disconnected, so reconnect if it has
            if ( handle.isDisconnected() ) {
                handle = this.objectStore.reconnect( factHandle );
            }

            final Object originalObject = handle.getObject();
            
            if ( handle.getEntryPoint() != this ) {
                throw new IllegalArgumentException( "Invalid Entry Point. You updated the FactHandle on entry point '" + handle.getEntryPoint().getEntryPointId() + "' instead of '" + getEntryPointId() + "'" );
            }
            
            final ObjectTypeConf typeConf = this.typeConfReg.getObjectTypeConf( this.entryPoint,
                                                                                object );

            // only needed if we maintain tms, but either way we must get it before we do the retract
            int status = -1;
            if ( typeConf.isTMSEnabled() ) {
                status = handle.getEqualityKey().getStatus();
            }


            if ( ! handle.isValid() || object == null || (handle.isEvent() && ((EventFactHandle) handle).isExpired()) ) {
                // the handle is invalid, most likely already retracted, so return and we cannot assert a null object
                return;
            }

            if ( activation != null ) {
                // release resources so that they can be GC'ed
                activation.getPropagationContext().releaseResources();
            }

            if ( originalObject != object || !AssertBehaviour.IDENTITY.equals( this.ruleBase.getConfiguration().getAssertBehaviour() ) ) {
                this.objectStore.removeHandle( handle );

                // set anyway, so that it updates the hashCodes
                handle.setObject( object );
                this.objectStore.addHandle( handle,
                                            object );
            }

            if ( typeConf.isTMSEnabled() ) {
            
                // the hashCode and equality has changed, so we must update the
                // EqualityKey
                EqualityKey key = handle.getEqualityKey();
                key.removeFactHandle( handle );
            
                TruthMaintenanceSystem tms = wm.getTruthMaintenanceSystem();

                // If the equality key is now empty, then remove it
                if ( key.isEmpty() ) {
                    tms.remove( key );
                }
    
                // now use an existing EqualityKey, if it exists, else create a new one
                key = tms.get( object );
                if ( key == null ) {
                    key = new EqualityKey( handle,
                                           status );
                    tms.put( key );
                } else {
                    key.addFactHandle( handle );
                }
    
                handle.setEqualityKey( key );

            }

            this.handleFactory.increaseFactHandleRecency( handle );

            Rule rule = activation == null ? null : activation.getRule();

            final PropagationContext propagationContext = new PropagationContextImpl( this.wm.getNextPropagationIdCounter(),
                                                                                      PropagationContext.MODIFICATION,
                                                                                      rule,
                                                                                      (activation == null) ? null : activation.getTuple(),
                                                                                      handle,
                                                                                      this.wm.agenda.getActiveActivations(),
                                                                                      this.wm.agenda.getDormantActivations(),
                                                                                      entryPoint,
                                                                                      mask,
                                                                                      modifiedClass,
                                                                                      null );

            this.entryPointNode.modifyObject( handle,
                                              propagationContext,
                                              typeConf,
                                              this.wm );
            
            propagationContext.evaluateActionQueue( this.wm );

            this.wm.workingMemoryEventSupport.fireObjectUpdated( propagationContext,
                                                              factHandle,
                                                              originalObject,
                                                              object,
                                                              this.wm );

           this.wm.executeQueuedActions();
           
           if ( rule == null ) {
               // This is not needed for internal WM actions as the firing rule will unstage
               this.wm.getAgenda().unstageActivations();
           }           
        } finally {
            this.wm.endOperation();
            this.ruleBase.readUnlock();
            this.lock.unlock();
        }
    }

    public void retract(final org.drools.runtime.rule.FactHandle handle) throws FactException {
        retract( (org.drools.FactHandle) handle,
                 true,
                 true,
                 null,
                 null );
    }

    public void retract(final org.drools.FactHandle factHandle,
                        final boolean removeLogical,
                        final boolean updateEqualsMap,
                        final Rule rule,
                        final Activation activation) throws FactException {
        if ( factHandle == null ) {
            throw new IllegalArgumentException( "FactHandle cannot be null " );
        }
        try {
            this.lock.lock();
            this.ruleBase.readLock();
            this.wm.startOperation();
            this.ruleBase.executeQueuedActions();

            InternalFactHandle handle = (InternalFactHandle) factHandle;
            if ( ! handle.isValid() ) {
                // can't retract an already retracted handle
                return;
            }

            // the handle might have been disconnected, so reconnect if it has
            if ( handle.isDisconnected() ) {
                handle = this.objectStore.reconnect( handle );
            }

            if ( handle.getObject() instanceof TraitableBean && ( (TraitableBean) handle.getObject() ).hasTraits() ) {
                PriorityQueue removedTypes = new PriorityQueue( ( (TraitableBean) handle.getObject() )._getTraitMap().values() );
                while ( ! removedTypes.isEmpty() ) {
                    retract( getFactHandle( removedTypes.poll() ),
                            removeLogical,
                            updateEqualsMap,
                            rule,
                            activation );
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
            final PropagationContext propagationContext = new PropagationContextImpl( this.wm.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RETRACTION,
                                                                                      rule,
                                                                                      (activation == null) ? null : activation.getTuple(),
                                                                                      handle,
                                                                                      this.wm.agenda.getActiveActivations(),
                                                                                      this.wm.agenda.getDormantActivations(),
                                                                                      this.entryPoint );

            this.entryPointNode.retractObject( handle,
                                               propagationContext,
                                               typeConf,
                                               this.wm );

            if ( typeConf.isTMSEnabled() ) {
                TruthMaintenanceSystem tms = wm.getTruthMaintenanceSystem();

                // Update the equality key, which maintains a list of stated
                // FactHandles
                final EqualityKey key = handle.getEqualityKey();

                // Its justified so attempt to remove any logical dependencies
                // for
                // the handle
                if ( key.getStatus() == EqualityKey.JUSTIFIED ) {
                    tms.removeLogicalDependencies( handle );
                }

                key.removeFactHandle( handle );
                handle.setEqualityKey( null );

                // If the equality key is now empty, then remove it
                if ( key.isEmpty() ) {
                    tms.remove( key );
                }
            }

            if ( handle.isTraitOrTraitable() && handle.getObject() instanceof TraitProxy ) {
                ((TraitableBean) ( (TraitProxy) handle.getObject() ).getObject()).removeTrait( ( (TraitProxy) handle.getObject() ).getTypeCode() );
            }

            propagationContext.evaluateActionQueue( this.wm );
            

            this.wm.workingMemoryEventSupport.fireObjectRetracted( propagationContext,
                                                                handle,
                                                                object,
                                                                this.wm );

            this.objectStore.removeHandle( handle );

            this.handleFactory.destroyFactHandle( handle );

            this.wm.executeQueuedActions();
            
            if ( rule == null ) {
                // This is not needed for internal WM actions as the firing rule will unstage
                this.wm.getAgenda().unstageActivations();
            }            
        } finally {
            this.wm.endOperation();
            this.ruleBase.readUnlock();
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
            logger.error( "Warning: Method addPropertyChangeListener not found" + " on the class " + object.getClass() + " so Drools will be unable to process JavaBean" + " PropertyChangeEvents on the asserted Object" );
        } catch ( final IllegalArgumentException e ) {
            logger.error( "Warning: The addPropertyChangeListener method" + " on the class " + object.getClass() + " does not take" + " a simple PropertyChangeListener argument" + " so Drools will be unable to process JavaBean"
                                + " PropertyChangeEvents on the asserted Object" );
        } catch ( final IllegalAccessException e ) {
            logger.error( "Warning: The addPropertyChangeListener method" + " on the class " + object.getClass() + " is not public" + " so Drools will be unable to process JavaBean" + " PropertyChangeEvents on the asserted Object" );
        } catch ( final InvocationTargetException e ) {
            logger.error( "Warning: The addPropertyChangeListener method" + " on the class " + object.getClass() + " threw an InvocationTargetException" + " so Drools will be unable to process JavaBean"
                                + " PropertyChangeEvents on the asserted Object: " + e.getMessage() );
        } catch ( final SecurityException e ) {
            logger.error( "Warning: The SecurityManager controlling the class " + object.getClass() + " did not allow the lookup of a" + " addPropertyChangeListener method" + " so Drools will be unable to process JavaBean"
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
            throw new RuntimeDroolsException( "Warning: The removePropertyChangeListener method on the class " + object.getClass() + " does not take a simple PropertyChangeListener argument so Drools will be unable to stop processing JavaBean"
                                              + " PropertyChangeEvents on the retracted Object" );
        } catch ( final IllegalAccessException e ) {
            throw new RuntimeDroolsException( "Warning: The removePropertyChangeListener method on the class " + object.getClass() + " is not public so Drools will be unable to stop processing JavaBean PropertyChangeEvents on the retracted Object" );
        } catch ( final InvocationTargetException e ) {
            throw new RuntimeDroolsException( "Warning: The removePropertyChangeL istener method on the class " + object.getClass() + " threw an InvocationTargetException so Drools will be unable to stop processing JavaBean"
                                              + " PropertyChangeEvents on the retracted Object: " + e.getMessage() );
        } catch ( final SecurityException e ) {
            throw new RuntimeDroolsException( "Warning: The SecurityManager controlling the class " + object.getClass() + " did not allow the lookup of a removePropertyChangeListener method so Drools will be unable to stop processing JavaBean"
                                              + " PropertyChangeEvents on the retracted Object: " + e.getMessage() );
        }
    }

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name) {
        return this.wm.getWorkingMemoryEntryPoint( name );
    }

    public ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry() {
        return this.typeConfReg;
    }

    public RuleBase getRuleBase() {
        return this.ruleBase;
    }

    public FactHandle getFactHandle(Object object) {
        return this.objectStore.getHandleForObject( object );
    }

    public EntryPoint getEntryPoint() {
        return this.entryPoint;
    }

    public InternalWorkingMemory getInternalWorkingMemory() {
        return this.wm;
    }

    public FactHandle getFactHandleByIdentity(final Object object) {
        return this.objectStore.getHandleForObjectIdentity( object );
    }

    public Object getObject(org.drools.runtime.rule.FactHandle factHandle) {
        return this.objectStore.getObjectForHandle(factHandle);
    }

    @SuppressWarnings("unchecked")
    public <T extends org.drools.runtime.rule.FactHandle> Collection<T> getFactHandles() {
        return new ObjectStoreWrapper( this.objectStore,
                                       null,
                                       ObjectStoreWrapper.FACT_HANDLE );
    }

    @SuppressWarnings("unchecked")
    public <T extends org.drools.runtime.rule.FactHandle> Collection<T> getFactHandles(org.drools.runtime.ObjectFilter filter) {
        return new ObjectStoreWrapper( this.objectStore,
                                       filter,
                                       ObjectStoreWrapper.FACT_HANDLE );
    }

    @SuppressWarnings("unchecked")
    public Collection<Object> getObjects() {
        return new ObjectStoreWrapper( this.objectStore,
                                       null,
                                       ObjectStoreWrapper.OBJECT );
    }

    @SuppressWarnings("unchecked")
    public Collection<Object> getObjects(org.drools.runtime.ObjectFilter filter) {
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
    
    /** Side-effects, will add the created key to the handle. */
    private EqualityKey createEqualityKey(InternalFactHandle handle) {
      EqualityKey key = new EqualityKey( handle );
      handle.setEqualityKey( key );
      return key;
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

        
        final Rete source = this.ruleBase.getRete();
        final ClassObjectType cot = new ClassObjectType( object.getClass() );
        final Map<ObjectType, ObjectTypeNode> map = source.getObjectTypeNodes( EntryPoint.DEFAULT );
        final ObjectTypeNode node = map.get( cot );
        final ObjectHashSet memory = ((ObjectTypeNodeMemory) this.wm.getNodeMemory( node )).memory;
      
        // All objects of this type that are already there were certainly stated,
        // since this method call happens at the first logical insert, for any given type.
        org.drools.core.util.Iterator it = memory.iterator();

        for ( Object obj = it.next(); obj != null; obj = it.next() ) {
          
            org.drools.core.util.ObjectHashSet.ObjectEntry holder = (org.drools.core.util.ObjectHashSet.ObjectEntry) obj;
    
            InternalFactHandle handle = (InternalFactHandle) holder.getValue();
            
            if ( handle != null) {
                EqualityKey key = createEqualityKey(handle);
                key.setStatus(EqualityKey.STATED);
                this.wm.getTruthMaintenanceSystem().put(key);
            }
        }
      
        // Enable TMS for this type.
        conf.enableTMS();
      
    }
    
    public void propertyChange(final PropertyChangeEvent event) {
        final Object object = event.getSource();

        try {
            FactHandle handle = getFactHandle( object );
            if ( handle == null ) {
                throw new FactException( "Update error: handle not found for object: " + object + ". Is it in the working memory?" );
            }
            update( handle,
                    object );
        } catch ( final FactException e ) {
            throw new RuntimeDroolsException( e.getMessage() );
        }
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


}
