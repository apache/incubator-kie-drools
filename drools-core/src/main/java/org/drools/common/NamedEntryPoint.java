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

/**
 * 
 */
package org.drools.common;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuntimeDroolsException;
import org.drools.WorkingMemory;
import org.drools.WorkingMemoryEntryPoint;
import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.base.ClassObjectType;
import org.drools.core.util.ObjectHashSet;
import org.drools.impl.StatefulKnowledgeSessionImpl.ObjectStoreWrapper;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.Rete;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;

public class NamedEntryPoint
    implements
    InternalWorkingMemoryEntryPoint,
    WorkingMemoryEntryPoint,
    PropertyChangeListener  {
    protected static final Class[]                               ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES = new Class[]{PropertyChangeListener.class};    
    
    /** The arguments used when adding/removing a property change listener. */
    protected final Object[]                addRemovePropertyChangeListenerArgs = new Object[]{this};    

    private static final long               serialVersionUID                    = 510l;

    protected ObjectStore                   objectStore;

    protected transient InternalRuleBase    ruleBase;

    protected EntryPoint                    entryPoint;
    protected EntryPointNode                entryPointNode;

    private ObjectTypeConfigurationRegistry typeConfReg;

    private final AbstractWorkingMemory     wm;

    private FactHandleFactory               handleFactory;

    protected final ReentrantLock           lock;

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
                       false,
                       false,
                       null,
                       null );
    }

    public FactHandle insert(final Object object,
                             final boolean dynamic) throws FactException {
        return insert( object,
                       dynamic,
                       false,
                       null,
                       null );
    }

    protected FactHandle insert(final Object object,
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
                this.ruleBase.readLock();
                this.lock.lock();
                // check if the object already exists in the WM
                handle = (InternalFactHandle) this.objectStore.getHandleForObject( object );

                if ( typeConf.isTMSEnabled() ) {
                  
                    EqualityKey key = null;

                    if ( handle == null ) {
                        // lets see if the object is already logical asserted
                        key = this.wm.tms.get( object );
                    } else {
                        // Object is already asserted, so check and possibly correct its
                        // status and then return the handle
                        key = handle.getEqualityKey();

                        if ( key.getStatus() == EqualityKey.STATED ) {
                            // return null as you cannot justify a stated object.
                            return handle;
                        }

                        if ( !logical ) {
                            // this object was previously justified, so we have to override it to stated
                            key.setStatus( EqualityKey.STATED );
                            this.wm.tms.removeLogicalDependencies( handle );
                        } else {
                            // this was object is already justified, so just add new logical dependency
                            this.wm.tms.addLogicalDependency( handle,
                                                           activation,
                                                           activation.getPropagationContext(),
                                                           rule );
                        }

                        return handle;
                    }

                    // At this point we know the handle is null
                    if ( key == null ) {
                      
                        handle = createHandle( object,
                                               typeConf );

                        key = createEqualityKey(handle);
                        
                        this.wm.tms.put( key );
                        
                        if ( !logical ) {
                            key.setStatus( EqualityKey.STATED );
                        } else {
                            key.setStatus( EqualityKey.JUSTIFIED );
                            this.wm.tms.addLogicalDependency( handle,
                                                           activation,
                                                           activation.getPropagationContext(),
                                                           rule );
                        }
                    } else if ( !logical ) {
                        if ( key.getStatus() == EqualityKey.JUSTIFIED ) {
                            // Its previous justified, so switch to stated and remove logical dependencies
                            final InternalFactHandle justifiedHandle = key.getFactHandle();
                            this.wm.tms.removeLogicalDependencies( justifiedHandle );

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
                                } else {
                                    Object oldObject = handle.getObject();
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
                            this.wm.tms.addLogicalDependency( key.getFactHandle(),
                                                           activation,
                                                           activation.getPropagationContext(),
                                                           rule );
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
                    addPropertyChangeListener( object );
                }

                insert( handle,
                        object,
                        rule,
                        activation,
                        typeConf );

            } finally {
                this.lock.unlock();
                this.ruleBase.readUnlock();
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
                                                                                  (activation == null) ? null : (LeftTuple) activation.getTuple(),
                                                                                  handle,
                                                                                  this.wm.agenda.getActiveActivations(),
                                                                                  this.wm.agenda.getDormantActivations(),
                                                                                  entryPoint );

        this.entryPointNode.assertObject( handle,
                                          propagationContext,
                                          typeConf,
                                          this.wm );

        this.wm.executeQueuedActions();

        this.wm.workingMemoryEventSupport.fireObjectInserted( propagationContext,
                                                              handle,
                                                              object,
                                                              this.wm );
    }

    public void update(final org.drools.runtime.rule.FactHandle handle,
                       final Object object) throws FactException {
        update( handle,
                object,
                null,
                null );
    }
    
    public void update(final org.drools.runtime.rule.FactHandle factHandle,
                       final Object object,
                       final Rule rule,
                       final Activation activation) throws FactException {

        update( (org.drools.FactHandle) factHandle,
                object,
                rule,
                activation );
    }    

    public void update(org.drools.FactHandle factHandle,
                       final Object object,
                       final Rule rule,
                       final Activation activation) throws FactException {
        try {
            this.ruleBase.readLock();
            this.lock.lock();
            this.wm.startOperation();
            this.ruleBase.executeQueuedActions();

            // the handle might have been disconnected, so reconnect if it has
            if ( ((InternalFactHandle)factHandle).isDisconnected() ) {
                factHandle = this.objectStore.reconnect( factHandle );
            }
            
            final ObjectTypeConf typeConf = this.typeConfReg.getObjectTypeConf( this.entryPoint,
                object );

            // only needed if we maintain tms, but either way we must get it before we do the retract
            int status = -1;
            if ( typeConf.isTMSEnabled() ) {
                status = ((InternalFactHandle) factHandle).getEqualityKey().getStatus();
            }
            final InternalFactHandle handle = (InternalFactHandle) factHandle;
            final Object originalObject = handle.getObject();

            if ( handle.getId() == -1 || object == null || (handle.isEvent() && ((EventFactHandle) handle).isExpired()) ) {
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
            

                // If the equality key is now empty, then remove it
                if ( key.isEmpty() ) {
                    this.wm.tms.remove( key );
                }
    
                // now use an existing EqualityKey, if it exists, else create a new one
                key = this.wm.tms.get( object );
                if ( key == null ) {
                    key = new EqualityKey( handle,
                                           status );
                    this.wm.tms.put( key );
                } else {
                    key.addFactHandle( handle );
                }
    
                handle.setEqualityKey( key );

            }

            this.handleFactory.increaseFactHandleRecency( handle );

            final PropagationContext propagationContext = new PropagationContextImpl( this.wm.getNextPropagationIdCounter(),
                                                                                      PropagationContext.MODIFICATION,
                                                                                      rule,
                                                                                      (activation == null) ? null : (LeftTuple) activation.getTuple(),
                                                                                      handle,
                                                                                      this.wm.agenda.getActiveActivations(),
                                                                                      this.wm.agenda.getDormantActivations(),
                                                                                      entryPoint );

            this.entryPointNode.modifyObject( handle,
                                              propagationContext,
                                              typeConf,
                                              this.wm );

            this.wm.workingMemoryEventSupport.fireObjectUpdated( propagationContext,
                                                              (org.drools.FactHandle) factHandle,
                                                              originalObject,
                                                              object,
                                                              this.wm );

           this.wm.executeQueuedActions();
        } finally {
            this.wm.endOperation();
            this.lock.unlock();
            this.ruleBase.readUnlock();
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
        try {
            this.ruleBase.readLock();
            this.lock.lock();
            this.wm.startOperation();
            this.ruleBase.executeQueuedActions();

            InternalFactHandle handle = (InternalFactHandle) factHandle;
            if ( handle.getId() == -1 ) {
                // can't retract an already retracted handle
                return;
            }

            // the handle might have been disconnected, so reconnect if it has
            if ( handle.isDisconnected() ) {
                handle = this.objectStore.reconnect( handle );
            }

            removePropertyChangeListener( handle );

            if ( activation != null ) {
                // release resources so that they can be GC'ed
                activation.getPropagationContext().releaseResources();
            }
            final PropagationContext propagationContext = new PropagationContextImpl( this.wm.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RETRACTION,
                                                                                      rule,
                                                                                      (activation == null) ? null : (LeftTuple) activation.getTuple(),
                                                                                      handle,
                                                                                      this.wm.agenda.getActiveActivations(),
                                                                                      this.wm.agenda.getDormantActivations(),
                                                                                      this.entryPoint );

            final Object object = handle.getObject();
            
            final ObjectTypeConf typeConf = this.typeConfReg.getObjectTypeConf( this.entryPoint,
                object );

            this.entryPointNode.retractObject( handle,
                                               propagationContext,
                                               typeConf,
                                               this.wm );

            if ( typeConf.isTMSEnabled() ) {

                // Update the equality key, which maintains a list of stated
                // FactHandles
                final EqualityKey key = handle.getEqualityKey();

                // Its justified so attempt to remove any logical dependencies
                // for
                // the handle
                if ( key.getStatus() == EqualityKey.JUSTIFIED ) {
                    this.wm.tms.removeLogicalDependencies( handle );
                }

                key.removeFactHandle( handle );
                handle.setEqualityKey( null );

                // If the equality key is now empty, then remove it
                if ( key.isEmpty() ) {
                    this.wm.tms.remove( key );
                }
            }
            

            this.wm.workingMemoryEventSupport.fireObjectRetracted( propagationContext,
                                                                handle,
                                                                object,
                                                                this.wm );

            this.objectStore.removeHandle( handle );

            this.handleFactory.destroyFactHandle( handle );

            this.wm.executeQueuedActions();
        } finally {
            this.wm.endOperation();
            this.lock.unlock();
            this.ruleBase.readUnlock();
        }
    }

    protected void addPropertyChangeListener(final Object object) {
        try {
            final Method method = object.getClass().getMethod( "addPropertyChangeListener",
                                                               NamedEntryPoint.ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES );

            method.invoke( object,
                           this.addRemovePropertyChangeListenerArgs );
        } catch ( final NoSuchMethodException e ) {
            System.err.println( "Warning: Method addPropertyChangeListener not found" + " on the class " + object.getClass() + " so Drools will be unable to process JavaBean" + " PropertyChangeEvents on the asserted Object" );
        } catch ( final IllegalArgumentException e ) {
            System.err.println( "Warning: The addPropertyChangeListener method" + " on the class " + object.getClass() + " does not take" + " a simple PropertyChangeListener argument" + " so Drools will be unable to process JavaBean"
                                + " PropertyChangeEvents on the asserted Object" );
        } catch ( final IllegalAccessException e ) {
            System.err.println( "Warning: The addPropertyChangeListener method" + " on the class " + object.getClass() + " is not public" + " so Drools will be unable to process JavaBean" + " PropertyChangeEvents on the asserted Object" );
        } catch ( final InvocationTargetException e ) {
            System.err.println( "Warning: The addPropertyChangeListener method" + " on the class " + object.getClass() + " threw an InvocationTargetException" + " so Drools will be unable to process JavaBean"
                                + " PropertyChangeEvents on the asserted Object: " + e.getMessage() );
        } catch ( final SecurityException e ) {
            System.err.println( "Warning: The SecurityManager controlling the class " + object.getClass() + " did not allow the lookup of a" + " addPropertyChangeListener method" + " so Drools will be unable to process JavaBean"
                                + " PropertyChangeEvents on the asserted Object: " + e.getMessage() );
        }
    }

    protected void removePropertyChangeListener(final FactHandle handle) {
        Object object = null;
        try {
            object = ((InternalFactHandle) handle).getObject();

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
        return this.objectStore.getObjectForHandle( (InternalFactHandle) factHandle );
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
        final ObjectHashSet memory = (ObjectHashSet) this.wm.getNodeMemory( node );
      
        // All objects of this type that are already there were certainly stated,
        // since this method call happens at the first logical insert, for any given type.
        org.drools.core.util.Iterator it = memory.iterator();

        for ( Object obj = it.next(); obj != null; obj = it.next() ) {
          
            org.drools.core.util.ObjectHashSet.ObjectEntry holder = (org.drools.core.util.ObjectHashSet.ObjectEntry) obj; 
    
            InternalFactHandle handle = (InternalFactHandle) holder.getValue();
            
            if ( handle != null) {
                EqualityKey key = createEqualityKey(handle);
                key.setStatus(EqualityKey.STATED);
                this.wm.tms.put(key);
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


}
