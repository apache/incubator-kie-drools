package org.drools.reteoo;

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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.drools.FactException;
import org.drools.RuleBaseConfiguration;
import org.drools.RuntimeDroolsException;
import org.drools.base.ShadowProxy;
import org.drools.base.ShadowProxyFactory;
import org.drools.base.ShadowProxyHelper;
import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.facttemplates.Fact;
import org.drools.facttemplates.FactImpl;
import org.drools.rule.Package;
import org.drools.spi.PropagationContext;
import org.drools.util.ClassUtils;
import org.drools.util.Iterator;
import org.drools.util.ObjectHashMap;
import org.drools.util.ObjectHashMap.ObjectEntry;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * The Rete-OO network.
 * 
 * The Rete class is the root <code>Object</code>. All objects are asserted into
 * the Rete node where it propagates to all matching ObjectTypeNodes.
 * 
 * The first time an  instance of a Class type is asserted it does a full
 * iteration of all ObjectTyppeNodes looking for matches, any matches are 
 * then cached in a HashMap which is used for future assertions.
 * 
 * While Rete  extends ObjectSource nad implements ObjectSink it nulls the 
 * methods attach(), remove() and  updateNewNode() as this is the root node
 * they are no applicable
 * 
 * @see ObjectTypeNode
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
public class Rete extends ObjectSource
    implements
    Serializable,
    ObjectSink,
    NodeMemory {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
     * 
     */
    private static final long   serialVersionUID = 320L;
    /** The <code>Map</code> of <code>ObjectTypeNodes</code>. */
    private final ObjectHashMap objectTypeNodes;
    
    private transient InternalRuleBase ruleBase;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    public Rete(InternalRuleBase ruleBase) {
        super( 0 );
        this.objectTypeNodes = new ObjectHashMap();
        this.ruleBase = ruleBase;
    }
    
    public void setRuleBase(InternalRuleBase ruleBase) {
        this.ruleBase = ruleBase;
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * This is the entry point into the network for all asserted Facts. Iterates a cache
     * of matching <code>ObjectTypdeNode</code>s asserting the Fact. If the cache does not
     * exist it first iteraes and builds the cache.
     * 
     * @param handle
     *            The FactHandle of the fact to assert
     * @param context
     *            The <code>PropagationContext</code> of the <code>WorkingMemory</code> action   
     * @param workingMemory
     *            The working memory session.
     */
    public void assertObject(final InternalFactHandle handle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        final ObjectHashMap memory = (ObjectHashMap) workingMemory.getNodeMemory( this );

        final Object object = handle.getObject();

        ObjectTypeConf ojectTypeConf;
        if ( object instanceof FactImpl ) {
            String key = ((Fact) object).getFactTemplate().getName();
            ojectTypeConf = (ObjectTypeConf) memory.get( key );
            if ( ojectTypeConf == null ) {
                ojectTypeConf = new ObjectTypeConf( null, this.ruleBase);            
                memory.put( key,
                            ojectTypeConf,
                            false );
            }            
        } else {
            Class cls = object.getClass();
            if ( object instanceof ShadowProxy ) {
                cls = cls.getSuperclass();                                   
            }
            
            ojectTypeConf = (ObjectTypeConf) memory.get( cls );
            if ( ojectTypeConf == null ) {
                ojectTypeConf = new ObjectTypeConf( cls, this.ruleBase);            
                memory.put( cls,
                            ojectTypeConf,
                            false );
            }            
            
            // checks if shadow is enabled
            if ( ojectTypeConf.isShadowEnabled() ) {
                // need to improve this
                if ( !(handle.getObject() instanceof ShadowProxy) ) {
                    // replaces the actual object by its shadow before propagating
                    handle.setObject( ojectTypeConf.getShadow( handle.getObject() ) );
                    handle.setShadowFact( true );
                } else {
                    ((ShadowProxy) handle.getObject()).updateProxy();
                }
            }               
        }
        
        ObjectTypeNode[] cachedNodes = ojectTypeConf.getObjectTypeNodes( object );             

        for ( int i = 0, length = cachedNodes.length; i < length; i++ ) {
            cachedNodes[i].assertObject( handle,
                                         context,
                                         workingMemory );
        }
    }

    /**
     * Retract a fact object from this <code>RuleBase</code> and the specified
     * <code>WorkingMemory</code>.
     * 
     * @param handle
     *            The handle of the fact to retract.
     * @param workingMemory
     *            The working memory session.
     */
    public void retractObject(final InternalFactHandle handle,
                              final PropagationContext context,
                              final InternalWorkingMemory workingMemory) {
        final ObjectHashMap memory = (ObjectHashMap) workingMemory.getNodeMemory( this );

        final Object object = handle.getObject();

        ObjectTypeConf objectTypeConf;
        if ( object instanceof ShadowProxy ) {
            objectTypeConf = (ObjectTypeConf) memory.get( object.getClass().getSuperclass() );
        } else {
            objectTypeConf = (ObjectTypeConf) memory.get( object.getClass() );
        }
        
        ObjectTypeNode[] cachedNodes = objectTypeConf.getObjectTypeNodes( object );

//        // cached might have been cleared, so recalculate matching nodes
//        if ( cachedNodes == null ) {
//            cachedNodes = getMatchingNodes( object );
//            Object key = null;
//
//            if ( object instanceof FactImpl ) {
//                key = ((Fact) object).getFactTemplate().getName();
//            } else {
//                key = object.getClass();
//            }
//            memory.put( key,
//                        cachedNodes,
//                        false );
//        }

        if ( cachedNodes == null ) {
            // it is  possible that there are no ObjectTypeNodes for an  object being retracted
            return;
        }

        for ( int i = 0; i < cachedNodes.length; i++ ) {
            cachedNodes[i].retractObject( handle,
                                          context,
                                          workingMemory );
        }
    }



    /**
     * Adds the <code>TupleSink</code> so that it may receive
     * <code>Tuples</code> propagated from this <code>TupleSource</code>.
     * 
     * @param tupleSink
     *            The <code>TupleSink</code> to receive propagated
     *            <code>Tuples</code>.
     */
    protected void addObjectSink(final ObjectSink objectSink) {
        final ObjectTypeNode node = (ObjectTypeNode) objectSink;
        this.objectTypeNodes.put( node.getObjectType(),
                                  node,
                                  true );
    }

    protected void removeObjectSink(final ObjectSink objectSink) {
        this.objectTypeNodes.remove( objectSink );
    }

    public void attach() {
        throw new UnsupportedOperationException( "cannot call attach() from the root Rete node" );
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        throw new UnsupportedOperationException( "cannot call attach() from the root Rete node" );
    }

    public void remove(final BaseNode node,
                       final InternalWorkingMemory[] workingMemories) {
        final ObjectTypeNode objectTypeNode = (ObjectTypeNode) node;
        removeObjectSink( objectTypeNode );
        //@todo: we really should attempt to clear the memory cache for this ObjectTypeNode        
    }

    public ObjectHashMap getObjectTypeNodes() {
        return this.objectTypeNodes;
    }

    public Object createMemory(final RuleBaseConfiguration config) {
        return new ObjectHashMap();
    }

    public InternalRuleBase getRuleBase() {
        return this.ruleBase;
    }
    
    public int hashCode() {
        return this.objectTypeNodes.hashCode();
    }

    public boolean equals(final Object object) {
        if ( object == this ) {
            return true;
        }

        if ( object == null || !(object instanceof Rete) ) {
            return false;
        }

        final Rete other = (Rete) object;
        return this.objectTypeNodes.equals( other.objectTypeNodes );
    }

    public void updateSink(final ObjectSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        // JBRULES-612: the cache MUST be invalidated when a new node type is added to the network, so iterate and reset all caches.
        final ObjectHashMap memory = (ObjectHashMap) workingMemory.getNodeMemory( this );
        Iterator it = memory.iterator();
        for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
            (( ObjectTypeConf) entry.getValue() ).resetCache();
        }

        final ObjectTypeNode node = (ObjectTypeNode) sink;
        it = workingMemory.getFactHandleMap().iterator();
        for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
            final InternalFactHandle handle = (InternalFactHandle) entry.getValue();
            if ( node.matches( handle.getObject() ) ) {
                node.assertObject( handle,
                                   context,
                                   workingMemory );
            }
        }
    }
    
    public static class ObjectTypeConf implements Serializable  {
        // Objenesis instance without cache (false) 
        private static final Objenesis         OBJENESIS        = new ObjenesisStd( false );
        
        private final Class cls;
        private final InternalRuleBase ruleBase;        
        private ObjectTypeNode[] objectTypeNodes;
        
        protected boolean                      shadowEnabled;
        protected Class                        shadowClass;
        protected transient ObjectInstantiator instantiator;
        protected transient Field              delegate;        
        //private final InternalRuleBase ruleBase;
        
        public ObjectTypeConf(Class cls, InternalRuleBase ruleBase) {
            this.cls = cls;
            this.ruleBase = ruleBase;
            Rete rete = ruleBase.getRete(); 
            
            if ( cls == null || !ruleBase.getConfiguration().isShadowed( cls.getName() ) ) {
                return;
            }
            
            String pkgName =  cls.getPackage().getName();
            if ( "org.drools.reteoo".equals( pkgName ) || "org.drools.base".equals( pkgName )) {
                // We don't shadow internal classes
                this.shadowEnabled = false;
                return;
            }
            
            Class shadowClass = null;
            final String shadowProxyName = ShadowProxyFactory.getProxyClassNameForClass( this.cls );
            try {
                // if already loaded
                shadowClass =  rete.getRuleBase().getMapBackedClassLoader().loadClass( shadowProxyName );
            } catch ( final ClassNotFoundException cnfe ) {
                // otherwise, create and load
                final byte[] proxyBytes = ShadowProxyFactory.getProxyBytes( cls );
                if ( proxyBytes != null ) {
                    rete.getRuleBase().getMapBackedClassLoader().addClass( shadowProxyName,
                                                                              proxyBytes );
                    try {
                        shadowClass =  rete.getRuleBase().getMapBackedClassLoader().loadClass( shadowProxyName );
                    } catch ( ClassNotFoundException e ) {
                        throw new RuntimeException( "Unable to find or generate the ShadowProxy implementation for '" + this.cls.getName() + "'" );
                    }
                }

            }            
            
            if ( shadowClass != null ) {
                this.shadowClass = shadowClass;
                this.shadowEnabled = true;
                setInstantiator();
                setDelegateFieldObject();
            }             
        }
        
        /**
         * 
         */
        private void setInstantiator() {
            this.instantiator = OBJENESIS.getInstantiatorOf( this.shadowClass );
        }

        /**
         * 
         */
        private void setDelegateFieldObject() {
            try {
                this.delegate = this.shadowClass.getDeclaredField( ShadowProxyFactory.DELEGATE_FIELD_NAME );
                this.delegate.setAccessible( true );
            } catch ( final Exception e ) {
                throw new RuntimeDroolsException( "Error retriving delegate field for shadow proxy class: " + this.shadowClass.getName(),
                                                  e );
            }
        }        
        
        public Object getShadow(final Object fact) throws RuntimeDroolsException {
            ShadowProxy proxy = null;
            if ( isShadowEnabled() ) {
                try {
                    if ( this.delegate == null ) {
                        this.setDelegateFieldObject();
                    }
                    if ( this.instantiator == null ) {
                        this.setInstantiator();
                    }
                    proxy = (ShadowProxy) this.instantiator.newInstance();
                    this.delegate.set( proxy,
                                  fact );
                } catch ( final Exception e ) {
                    throw new RuntimeDroolsException( "Error creating shadow fact for object: " + fact,
                                                      e );
                }
            }
            return proxy;
        }    
        
        public boolean isShadowEnabled() {
            return this.shadowEnabled;
        }        
        
        public void resetCache() {
            this.objectTypeNodes = null;
        }
        
        public ObjectTypeNode[] getObjectTypeNodes(final Object object) {
            if ( this.objectTypeNodes == null ) {
                buildCache( object );
            }
            return this.objectTypeNodes;
        }
        
        private void buildCache(final Object object) throws FactException {
            final List cache = new ArrayList();

            final Iterator it = ruleBase.getRete().getObjectTypeNodes().iterator();
            for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
                final ObjectTypeNode node = (ObjectTypeNode) entry.getValue();
                if ( node.matches( object ) ) {
                    cache.add( node );
                }
            }

            this.objectTypeNodes =  ( ObjectTypeNode[] ) cache.toArray( new ObjectTypeNode[cache.size()] );
        }        
    }

}
